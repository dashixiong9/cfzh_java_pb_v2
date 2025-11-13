package com.cf.carpark.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.carpark.device.camera.HuaXia.HuaXiaResponse;
import com.cf.carpark.device.camera.HuaXia.HuaXiaWhitelistData;
import com.cf.carpark.device.camera.HuaXia.Rs485Data;
import com.cf.carpark.device.camera.ZhenShi.*;
import com.cf.carpark.device.camera.ZhenShiResponse911202002050;
import com.cf.carpark.device.led.PlayRule;
import com.cf.carpark.domain.CfCarPark;
import com.cf.carpark.domain.CfCarParkDevice;
import com.cf.carpark.domain.CfCarParkPackage;
import com.cf.carpark.domain.CfCarParkWhitelistIssuedLog;
import com.cf.carpark.domain.request.CfCarParkDeviceQuery;
import com.cf.carpark.domain.request.CfCarParkPackageQuery;
import com.cf.carpark.domain.request.CfCarParkWhitelistIssuedLogQuery;
import com.cf.carpark.domain.type.DeviceBrand;
import com.cf.carpark.service.*;
import com.cf.carpark.service.core.cache.ClientCache;
import com.cf.carpark.service.core.cache.IServerListCache;
import com.cf.carpark.service.core.dto.ClientDTO;
import com.cf.carpark.service.core.enums.ServerTypeEnum;
import com.cf.carpark.service.mqtt.handler.MqttHandler;
import com.cf.framework.exception.CustomException;
import com.cf.framework.utils.DateUtil;
import com.cf.framework.utils.HttpClient;
import com.cf.framework.utils.IdWorker;
import com.cf.framework.utils.StringTools;
import com.cf.ucenter.domain.CfWeixinConfig;
import com.cf.ucenter.service.CfWeixinConfigService;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * MQTT服务业务实现对象
 * @author gebilaowang
 * @date 2021/3/10 16:40
 */
@Slf4j
@Service(version = "1.0.0", loadbalance = "roundrobin")
@Transactional
public class MqttMqttDataServiceImplMqtt implements IMqttDataService {

    @Value("${cf.getIpUrl}")
    String getIpUrl;

    public static Map<String, Long> channelActionLimit = new ConcurrentHashMap<>();
    public static Map<String, Long> channelHeartBeatActionLimit = new ConcurrentHashMap<>();

    public static String DEVICE_LINK_COUNTS_REDIS_KEY = "DEVICE_LINK_COUNTS_REDIS_KEY";

    @Autowired
    private CfCarParkUseLogService cfCarParkUseLogService;
    @Autowired
    private CfCarParkService cfCarParkService;
    @Autowired
    private CfCarParkPackageService cfCarParkPackageService;
    @Autowired
    private CfCarParkDeviceService cfCarParkDeviceService;
    @Autowired
    private CfCarParkWhitelistIssuedLogService cfCarParkWhitelistIssuedLogService;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private IdWorker idWorker;

    @Reference(version = "1.0.0", retries = 0, timeout = 5000, check = false)
    private CfWeixinConfigService cfWeixinConfigService;

    private static boolean listenOffline = false;


    @Override
    public void push(String userOnlyCode, String message) {
    }

    @Override
    public void pushTopic(Channel channel, String message, String topic, Integer packetId) throws Exception {
        try{
            IServerListCache.getServer(ServerTypeEnum.MQTT).send(channel, topic,message,packetId);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void pushTopics(String flageKey, String message, String topic) throws Exception {
        IServerListCache.getServer(ServerTypeEnum.MQTT).sendByFlageKey(flageKey, topic, message);
    }

    @Override
    public void pushTopicAll(String message,String... topics ) throws Exception {
        for (String topic: topics) {
            IServerListCache.getServer(ServerTypeEnum.MQTT).sendAll(topic,message);
        }
    }

    @Override
    public void pushAll(String message) throws Exception {
        IServerListCache.getServer(ServerTypeEnum.MQTT).sendAllWithoutTopic(message);
    }


    /**
     * 解析MQTT接收到的车牌数据
     * @param carLogMap
     * @return
     * @throws Exception
     */
    Object parseCarParkUseLogForm(Map carLogMap) throws Exception{
        try {
            return cfCarParkUseLogService.parseCarParkUseLogForm(carLogMap);
        } catch (Exception e){
            log.error("解析停车数据时出现错误，传入的数据：{}",JSONObject.toJSONString(carLogMap));
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public String inputListening(Channel channel, String topic, String inputMessage, Integer packetId) throws Exception {
//        log.info("MQTT监听到了数据:{}",inputMessage);

        Map carLogMap = (JSONObject.parseObject(inputMessage));

        if(carLogMap.containsKey("payload") && carLogMap.get("payload")==null){
            return null;
        }
        Map alarmInfoPlate = (Map)carLogMap.get("AlarmInfoPlate");
        if(alarmInfoPlate==null && !carLogMap.containsKey("cmd")){
            alarmInfoPlate = (Map)((Map)carLogMap.get("payload")).get("AlarmInfoPlate");
            carLogMap = (Map)carLogMap.get("payload");
            carLogMap.put("use_base64","yes");
        }

        long currentTimeMillis = System.currentTimeMillis();
        String chennelId = channel.id().asLongText();

        if(topic.equals("/device/message/up/keep_alive") || alarmInfoPlate!=null && alarmInfoPlate.get("heartbeat")!=null && ((Integer) alarmInfoPlate.get("heartbeat")).intValue()==1){
            if(!channelHeartBeatActionLimit.containsKey(chennelId) || currentTimeMillis-channelHeartBeatActionLimit.get(chennelId)>=10000){
                channelHeartBeatActionLimit.put(chennelId,currentTimeMillis);
            }else if(channelHeartBeatActionLimit.containsKey(chennelId) && (currentTimeMillis-channelHeartBeatActionLimit.get(chennelId))<=10000){
                System.out.println("臻识设备心跳操作请求过于频繁");
                return null; //请求过于频繁，驳回
            }
            if(alarmInfoPlate!=null){
                checkOffLineDevice(alarmInfoPlate.get("serialno").toString(),channel);
            }else{
                alarmInfoPlate = new HashMap();
                Map heartMessage = (JSONObject.parseObject(inputMessage));
                alarmInfoPlate.put("serialno",heartMessage.get("sn"));
            }

            if(!MqttMqttDataServiceImplMqtt.listenOffline){
                //先初始化本服务器的在线设备数量
                MqttMqttDataServiceImplMqtt.listenOffline = true;
                startListenOutTimeHandle();
            }

            updateCarParkDeviceStatus(alarmInfoPlate.get("serialno").toString());
            JSONObject jsonObject = HttpClient.doGet(getIpUrl);

            String ip = jsonObject.getString("data");
            String serviceIp = redisTemplate.boundValueOps("device_ip_" + alarmInfoPlate.get("serialno").toString()).get();
            if(StringUtils.isEmpty(serviceIp) || !serviceIp.equals(ip)){
                redisTemplate.boundValueOps("device_ip_" + alarmInfoPlate.get("serialno").toString()).set(ip);
                redisTemplate.expire("device_ip_" + alarmInfoPlate.get("serialno").toString(), 3, TimeUnit.DAYS);
            }

            //处理白名单下发
            //添加/更新 白名单
            issuedWhitelist(channel, alarmInfoPlate.get("serialno").toString(), packetId, (byte)0);
            //删除过期 白名单
            issuedWhitelist(channel, alarmInfoPlate.get("serialno").toString(), packetId, (byte)1);


        }else if(alarmInfoPlate!=null && alarmInfoPlate.get("result")!=null){
            //如果是10分钟之前传入的数据，直接丢掉
            Integer dataTime = ((Map<String, Integer>) (((Map<String, Object>) ((Map<String, Object>) (((Map<String, Object>) alarmInfoPlate.get("result")).get("PlateResult"))).get("timeStamp")).get("Timeval"))).get("sec");
            long dataTimeLong = dataTime.longValue() * 1000;
            if(System.currentTimeMillis()-dataTimeLong>600000){
                return null;
            }
            if(!channelActionLimit.containsKey(chennelId) || currentTimeMillis-channelActionLimit.get(chennelId)>=1000){
                channelActionLimit.put(chennelId,currentTimeMillis);
            }else if(channelActionLimit.containsKey(chennelId) && (currentTimeMillis-channelActionLimit.get(chennelId))<=1000){
                System.out.println("臻识设备车牌数据推送操作请求过于频繁");
                return null; //请求过于频繁，驳回
            }
            checkOffLineDevice(alarmInfoPlate.get("serialno").toString(),channel);
            carLogMap.put("deviceBrand", DeviceBrand.ZHEN_SHI);
            ZhenShiResponse911202002050 zhenShiResponse911202002050 = (ZhenShiResponse911202002050) parseCarParkUseLogForm(carLogMap);
            if(StringUtils.isEmpty(zhenShiResponse911202002050.getResponse_AlarmInfoPlate().getIs_pay())){
                pushTopic(channel, JSON.toJSONString(zhenShiResponse911202002050), "/device/" + alarmInfoPlate.get("serialno").toString() + "/command", packetId);
            }else{
                //走最新的mqtt模式
                pushTopic(channel, zhenShiResponse911202002050.getResponse_AlarmInfoPlate().getIs_pay(), "/device/" + alarmInfoPlate.get("serialno").toString() + "/message/down/gpio_out", packetId);
                if(zhenShiResponse911202002050.getResponse_AlarmInfoPlate().getInfo().equals("ok")){
                    //同时增加开闸
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", StringTools.getRandomString(null,16));
                    jsonObject.put("sn", alarmInfoPlate.get("serialno").toString());
                    jsonObject.put("name", "gpio_out");
                    jsonObject.put("version", "1.0");
                    jsonObject.put("timestamp", System.currentTimeMillis()/1000);
                    JSONObject body = new JSONObject();
                    body.put("delay",500);
                    body.put("io",0);
                    body.put("value",2);
                    JSONObject payload = new JSONObject();
                    payload.put("type","gpio_out");
                    payload.put("body",body);
                    jsonObject.put("payload", payload);
                    pushTopic(channel, jsonObject.toString(), "/device/" + alarmInfoPlate.get("serialno").toString() + "/command", packetId);
                }
            }
        }else if(carLogMap.get("name")!=null && carLogMap.get("name").equals("customize")){
            //@client
            String trueSn = carLogMap.get("sn").toString().replace("@client", "");
            pushTopics(carLogMap.get("sn").toString(), carLogMap.get("payload").toString(), "/device/" + trueSn + "/command");
        }else if(carLogMap.get("devId")!=null && carLogMap.get("msgId")!=null && carLogMap.get("utcTs")!=null){
            Map<String, Object> huaXiaMap = new HashMap<>();
            huaXiaMap.put("deviceBrand", carLogMap.get(DeviceBrand.HUA_XIA));
            huaXiaMap.put("camera_id", carLogMap.get("devId"));
            //说明是华夏相机
            if(carLogMap.get("cmd")!=null && carLogMap.get("cmd").equals("heartbeat")){
                if(!channelHeartBeatActionLimit.containsKey(chennelId) || currentTimeMillis-channelHeartBeatActionLimit.get(chennelId)>=10000){
                    channelHeartBeatActionLimit.put(chennelId,currentTimeMillis);
                }else if(channelHeartBeatActionLimit.containsKey(chennelId) && (currentTimeMillis-channelHeartBeatActionLimit.get(chennelId))<=10000){
                    System.out.println("华夏设备心跳操作请求过于频繁");
                    return null; //请求过于频繁，驳回
                }
                checkOffLineDevice(carLogMap.get("devId").toString(),channel);

                if(!MqttMqttDataServiceImplMqtt.listenOffline){
                    //先初始化本服务器的在线设备数量
                    MqttMqttDataServiceImplMqtt.listenOffline = true;
                    startListenOutTimeHandle();
                }

                updateCarParkDeviceStatus(carLogMap.get("devId").toString());
                JSONObject jsonObject = HttpClient.doGet(getIpUrl);

                String ip = jsonObject.getString("data");
                String serviceIp = redisTemplate.boundValueOps("device_ip_" + carLogMap.get("devId").toString()).get();
                if(StringUtils.isEmpty(serviceIp) || !serviceIp.equals(ip)){
                    redisTemplate.boundValueOps("device_ip_" + carLogMap.get("devId").toString()).set(ip);
                    redisTemplate.expire("device_ip_" + carLogMap.get("devId").toString(), 3, TimeUnit.DAYS);
                }

                //处理白名单下发
                //添加/更新 白名单
                issuedWhitelist(channel, carLogMap.get("devId").toString(), packetId, (byte)0);
                //删除过期 白名单
                issuedWhitelist(channel, carLogMap.get("devId").toString(), packetId, (byte)1);

            }else if(carLogMap.get("cmd")!=null && carLogMap.get("cmd").equals("plateResult")){
                //正常车牌识别
                huaXiaMap.put("deviceBrand","hua_xia");
                huaXiaMap.put("type","result");
                huaXiaMap.put("camera_id", carLogMap.get("devId"));
                huaXiaMap.put("cam_id", carLogMap.get("devId"));

                Map content = (Map) carLogMap.get("content");
                huaXiaMap.put("car_plate", content.get("plateNum"));
                huaXiaMap.put("plate_num", content.get("plateNum"));
                huaXiaMap.put("color", content.get("color"));
                huaXiaMap.put("start_time", content.get("triggerTime").toString());

                huaXiaMap.put("bigImagePath", content.get("fullPicPath"));

                HuaXiaResponse huaXiaResponse = (HuaXiaResponse) parseCarParkUseLogForm(huaXiaMap);

                if(huaXiaResponse.getGpio_data().get(0).getAction().equals("on")){
                    //执行开闸操作
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("cmd", "ioOutput");
                    jsonObject.put("msgId", System.currentTimeMillis()+StringTools.getRandomString(null,7));
                    jsonObject.put("utcTs", System.currentTimeMillis()/1000);
                    com.alibaba.fastjson.JSONObject gpioData = new com.alibaba.fastjson.JSONObject();
                    gpioData.put("ioNum","io1");
                    gpioData.put("action","on");
                    jsonObject.put("gpioData", gpioData);
                    pushTopic(channel, jsonObject.toString(), "/device/"+carLogMap.get("devId")+"/get", packetId);
                }
                //下发485数据(控制led显示屏的文字显示和语音播报)
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("cmd", "rs485Transmit");
                jsonObject.put("encodeType", "hex2string");
                jsonObject.put("msgId", System.currentTimeMillis()+StringTools.getRandomString(null,7));
                jsonObject.put("utcTs", System.currentTimeMillis()/1000);
                List<Map<String, String>> dataList = new ArrayList<>();
                for(Rs485Data rs485Data: huaXiaResponse.getRs485_data()){
                    Map<String,String> data = new HashMap<>();
                    data.put("data",rs485Data.getData());
                    dataList.add(data);
                }
                jsonObject.put("chn1Data", dataList);
                System.out.println(jsonObject);
                pushTopic(channel, jsonObject.toString(), "/device/"+carLogMap.get("devId")+"/get", packetId);

            }

        }
        return inputMessage;
    }

    //检查指定序列号的设备是否在服务器中已经掉线，但是却意外的通过新channel连接到服务器中推送数据且为注册到服务器中
    private void checkOffLineDevice(String sn, Channel channel){
        if(MqttHandler.offlineDevice.containsKey(sn)){
            MqttHandler.offlineDevice.remove(sn);
            MqttHandler.removeClient(channel);
        }
    }

    @Override
    public void issuedWhitelist(Channel channel, String sn, Integer packetId, byte operateType) throws Exception {

        CfCarParkDevice cfCarParkDevice = cfCarParkDeviceService.findByCode(sn, false);

        CfCarPark cfCarPark = cfCarParkService.findById(cfCarParkDevice.getCarParkId());
        if(cfCarPark==null || cfCarPark.getAutoIssuedWhitelist()==(byte)0){
            return;
        }

        List<String> numberPlateList = null;

        CfCarParkWhitelistIssuedLogQuery cfCarParkWhitelistIssuedLogQuery = new CfCarParkWhitelistIssuedLogQuery();
        cfCarParkWhitelistIssuedLogQuery.setDeviceSn(sn);
        List<CfCarParkWhitelistIssuedLog> cfCarParkWhitelistIssuedLogs = cfCarParkWhitelistIssuedLogService.getListByQuery(cfCarParkWhitelistIssuedLogQuery);
        if(cfCarParkWhitelistIssuedLogs!=null && cfCarParkWhitelistIssuedLogs.size()>0){
            numberPlateList = new ArrayList<>();
            for (CfCarParkWhitelistIssuedLog cfCarParkWhitelistIssuedLog: cfCarParkWhitelistIssuedLogs){
                numberPlateList.add(cfCarParkWhitelistIssuedLog.getNumberPlate());
            }
        }

        CfCarParkPackageQuery cfCarParkPackageQuery = new CfCarParkPackageQuery();
        cfCarParkPackageQuery.setCarParkId(cfCarParkDevice.getCarParkId());
        //家庭组的车辆不下发
        cfCarParkPackageQuery.setGroupFlag("");
        if(operateType==(byte)0){
            cfCarParkPackageQuery.setMaxStartTime(System.currentTimeMillis());
            cfCarParkPackageQuery.setMinEndTime(System.currentTimeMillis());
            //排除已下发的车牌
            if(numberPlateList!=null){
                cfCarParkPackageQuery.setNumberPlateNotIn(numberPlateList);
            }
        }else{
            cfCarParkPackageQuery.setMaxEndTime(System.currentTimeMillis());
            //筛选已下发的车牌
            if(numberPlateList!=null){
                cfCarParkPackageQuery.setNumberPlates(numberPlateList);
            }
        }
        cfCarParkPackageQuery.setPage(1);
        cfCarParkPackageQuery.setSize(5);
        List<CfCarParkPackage> cfCarParkPackages = cfCarParkPackageService.getListByQuery(cfCarParkPackageQuery);

        if(cfCarParkPackages==null || cfCarParkPackages.size()==0){
            //如果过期的数据没有，检查家庭组车辆，删除家庭组车辆白名单
            if(operateType==(byte)0){
                cfCarParkPackageQuery.setGroupFlag(null);
                cfCarParkPackageQuery.setMaxEndTime(null);
                cfCarParkPackageQuery.setNumberPlates(null);
                cfCarParkPackageQuery.setGroupFlagNotEmpty(true);
            }
            cfCarParkPackages = cfCarParkPackageService.getListByQuery(cfCarParkPackageQuery);
            if(cfCarParkPackages==null || cfCarParkPackages.size()==0){
                return;
            }
        }

        synchronizePackageDataToDevice(channel, sn, packetId, operateType, cfCarParkPackages, cfCarParkDevice);
    }

    @Override
    public void synchronizePackageDataToDevice(Channel channel, String sn, Integer packetId, byte operateType, List<CfCarParkPackage> cfCarParkPackages, CfCarParkDevice cfCarParkDevice) throws Exception {
        CfCarParkWhitelistIssuedLogQuery cfCarParkWhitelistIssuedLogQuery = new CfCarParkWhitelistIssuedLogQuery();
        cfCarParkWhitelistIssuedLogQuery.setDeviceSn(sn);

        List<String> carParkPackageIds = new ArrayList<>();
        List<String> carParkPackageNumberPlates = new ArrayList<>();
        List<CfCarParkWhitelistIssuedLog> addCarParkWhitelistIssuedLogs = new ArrayList<>();
        if(cfCarParkDevice.getBrand().equals("zhen_shi")){
            String model = cfCarParkDevice.getModel();
            if(channel==null){
                ConcurrentHashMap<String, ClientDTO> channels = ClientCache.channels;
                for (Map.Entry channelEntry: channels.entrySet()) {
                    ClientDTO clientDTO = (ClientDTO)channelEntry.getValue();
                    if(!clientDTO.getFlagKey().equals(sn)){
                        continue;
                    }
                    channel = clientDTO.getChannel();
                    packetId = clientDTO.getPacketId();
                    break;
                }
            }
            if(channel==null){
                return;
            }

            ZhenShiResponse911202002050 zhenShiResponse911202002050 = new ZhenShiResponse911202002050();
            Response_AlarmInfoPlate response_alarmInfoPlate = new Response_AlarmInfoPlate();
            WhiteListOperate whiteListOperate = new WhiteListOperate();

            whiteListOperate.setOperate_type(operateType);
            IssuedWhitelist[] issuedWhitelists = new IssuedWhitelist[cfCarParkPackages.size()];
            IssuedWhiteNewlist[] issuedWhiteNewlists = new IssuedWhiteNewlist[cfCarParkPackages.size()];

            int i = 0;
            for(CfCarParkPackage cfCarParkPackage : cfCarParkPackages){

                carParkPackageIds.add(cfCarParkPackage.getId());
                carParkPackageNumberPlates.add(cfCarParkPackage.getNumberPlate());

                if( model!=null && cfCarParkDevice.getLinkMode().equals("mqtt") && StringUtils.isNumeric(model.substring((model.indexOf("-"))+1)) && new Integer(model.substring((model.indexOf("-"))+1)).intValue()>=202210285 ){
                    IssuedWhiteNewlist issuedWhiteNewlist = new IssuedWhiteNewlist();
                    issuedWhiteNewlist.setPlate(cfCarParkPackage.getNumberPlate());
                    issuedWhiteNewlist.setEnable((byte)1);
                    issuedWhiteNewlist.setNeed_alarm((byte)0);
                    issuedWhiteNewlist.setEnable_time(DateUtil.stampToDate(cfCarParkPackage.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
                    issuedWhiteNewlist.setOverdue_time(DateUtil.stampToDate(cfCarParkPackage.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
                    issuedWhiteNewlist.setCreate_time(DateUtil.stampToDate(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
                    issuedWhiteNewlist.setTime_seg_enable((byte)0);
                    issuedWhiteNewlist.setSeg_time_start("");
                    issuedWhiteNewlist.setSeg_time_end("");
                    issuedWhiteNewlists[i] = issuedWhiteNewlist;
                }else{
                    IssuedWhitelist issuedWhitelist = new IssuedWhitelist();
                    issuedWhitelist.setPlate(cfCarParkPackage.getNumberPlate());
                    issuedWhitelist.setEnable((byte)1);
                    issuedWhitelist.setNeed_alarm((byte)0);
                    issuedWhitelist.setEnable_time(DateUtil.stampToDate(cfCarParkPackage.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
                    issuedWhitelist.setOverdue_time(DateUtil.stampToDate(cfCarParkPackage.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
                    issuedWhitelists[i] = issuedWhitelist;
                }

                CfCarParkWhitelistIssuedLog carParkWhitelistIssuedLog = new CfCarParkWhitelistIssuedLog();
                carParkWhitelistIssuedLog.setId(idWorker.nextId());
                carParkWhitelistIssuedLog.setNumberPlate(cfCarParkPackage.getNumberPlate());
                carParkWhitelistIssuedLog.setIssuedTime(System.currentTimeMillis());
                carParkWhitelistIssuedLog.setDeviceSn(sn);
                addCarParkWhitelistIssuedLogs.add(carParkWhitelistIssuedLog);

                i++;
            }
            whiteListOperate.setWhite_list_data(issuedWhitelists);
            response_alarmInfoPlate.setWhite_list_operate(whiteListOperate);

            zhenShiResponse911202002050.setResponse_AlarmInfoPlate(response_alarmInfoPlate);
            if(whiteListOperate.getOperate_type()==(byte)0){
                //增加
                for(int j=0; j<issuedWhiteNewlists.length; j++){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id", StringTools.getRandomString(null,16));
                    jsonObject.put("sn", sn);
                    jsonObject.put("name", "white_list_operator");
                    jsonObject.put("version", "1.0");
                    jsonObject.put("timestamp", System.currentTimeMillis()/1000);
                    JSONObject payload = new JSONObject();
                    payload.put("type","white_list_operator");
                    JSONObject body = new JSONObject();
                    body.put("operator_type","update_or_add");
                    body.put("dldb_rec",issuedWhiteNewlists[j]);
                    payload.put("body",body);
                    jsonObject.put("payload", payload);
                    pushTopic(channel, jsonObject.toString(), "/device/" + sn + "/message/down/white_list_operator", packetId);
                    Thread.sleep(100);  //休眠100毫秒，避免相机宕机或丢包
                }
            }else{
                //删除
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", StringTools.getRandomString(null,16));
                jsonObject.put("sn", sn);
                jsonObject.put("name", "white_list_operator");
                jsonObject.put("version", "1.0");
                jsonObject.put("timestamp", System.currentTimeMillis()/1000);
                JSONObject payload = new JSONObject();
                payload.put("type","white_list_operator");
                JSONObject body = new JSONObject();
                body.put("operator_type","delete");
                body.put("plate",carParkPackageNumberPlates);
                payload.put("body",body);
                jsonObject.put("payload", payload);
                pushTopic(channel, jsonObject.toString(), "/device/" + sn + "/message/down/white_list_operator", packetId);
                Thread.sleep(100);  //休眠100毫秒，避免相机宕机或丢包
            }
        }else if(cfCarParkDevice.getBrand().equals("hua_xia")){
            if (operateType==(byte)0){
                //下发白名单
                List<HuaXiaWhitelistData> huaXiaWhitelistDataList = new ArrayList<>();
                int whiteSize = cfCarParkPackages.size();
                for(int i=0; i<whiteSize; i++){
                    HuaXiaWhitelistData huaXiaWhitelistData = new HuaXiaWhitelistData();
                    huaXiaWhitelistData.setPlateNum(cfCarParkPackages.get(i).getNumberPlate());
                    huaXiaWhitelistData.setType("W");
                    huaXiaWhitelistData.setStartTime(DateUtil.stampToDate(cfCarParkPackages.get(i).getStartTime(),"yyyy/MM/dd HH:mm:ss"));
                    huaXiaWhitelistData.setEndTime(DateUtil.stampToDate(cfCarParkPackages.get(i).getEndTime(),"yyyy/MM/dd HH:mm:ss"));
                    huaXiaWhitelistDataList.add(huaXiaWhitelistData);
                    carParkPackageIds.add(cfCarParkPackages.get(i).getId());

                    CfCarParkWhitelistIssuedLog carParkWhitelistIssuedLog = new CfCarParkWhitelistIssuedLog();
                    carParkWhitelistIssuedLog.setId(idWorker.nextId());
                    carParkWhitelistIssuedLog.setNumberPlate(cfCarParkPackages.get(i).getNumberPlate());
                    carParkWhitelistIssuedLog.setIssuedTime(System.currentTimeMillis());
                    carParkWhitelistIssuedLog.setDeviceSn(sn);
                    addCarParkWhitelistIssuedLogs.add(carParkWhitelistIssuedLog);
                }
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("cmd", "listAdd");
                jsonObject.put("msgId", System.currentTimeMillis()+StringTools.getRandomString(null,7));
                jsonObject.put("utcTs", System.currentTimeMillis()/1000);
                jsonObject.put("listData", huaXiaWhitelistDataList);
                pushTopics(sn, jsonObject.toString(), "/device/"+sn+"/get");
            }else{
                //删除白名单
                List<Map<String,String>> huaXiaWhitelistDataDeleteList = new ArrayList<>();
                int whiteSize = cfCarParkPackages.size();
                for(int i=0; i<whiteSize; i++){
                    Map<String,String> huaXiaWhitelistDataDelete = new HashMap<>();
                    huaXiaWhitelistDataDelete.put("plateNum",cfCarParkPackages.get(i).getNumberPlate());
                    huaXiaWhitelistDataDeleteList.add(huaXiaWhitelistDataDelete);
                    carParkPackageNumberPlates.add(cfCarParkPackages.get(i).getNumberPlate());

                    CfCarParkWhitelistIssuedLog carParkWhitelistIssuedLog = new CfCarParkWhitelistIssuedLog();
                    carParkWhitelistIssuedLog.setId(idWorker.nextId());
                    carParkWhitelistIssuedLog.setNumberPlate(cfCarParkPackages.get(i).getNumberPlate());
                    carParkWhitelistIssuedLog.setIssuedTime(System.currentTimeMillis());
                    carParkWhitelistIssuedLog.setDeviceSn(sn);
                    addCarParkWhitelistIssuedLogs.add(carParkWhitelistIssuedLog);
                }
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("cmd", "listDel");
                jsonObject.put("msgId", System.currentTimeMillis()+StringTools.getRandomString(null,7));
                jsonObject.put("utcTs", System.currentTimeMillis()/1000);
                jsonObject.put("listData", huaXiaWhitelistDataDeleteList);
                pushTopics(sn, jsonObject.toString(), "/device/"+sn+"/get");
            }
        }

        if(carParkPackageIds.size()>0){
            CfCarParkPackageQuery cfCarParkPackageUpdateQuery = new CfCarParkPackageQuery();
            cfCarParkPackageUpdateQuery.setIds(carParkPackageIds);
            CfCarParkPackage cfCarParkPackage = new CfCarParkPackage();
            cfCarParkPackage.setIssuedWhitelist((byte)1);
            cfCarParkPackage.setIssuedWhitelistTime(System.currentTimeMillis());
            cfCarParkPackageService.updateByQuery(cfCarParkPackageUpdateQuery, cfCarParkPackage);
        }

        if(operateType==(byte)0){
            //添加车牌白名单下发记录
            if(addCarParkWhitelistIssuedLogs.size()>0){
                cfCarParkWhitelistIssuedLogService.batchAdd(addCarParkWhitelistIssuedLogs);
            }
        }else{
            //删除车牌下发记录
            if(carParkPackageNumberPlates.size()>0){
                cfCarParkWhitelistIssuedLogQuery.setNumberPlates(carParkPackageNumberPlates);
                cfCarParkWhitelistIssuedLogService.deleteByQuery(cfCarParkWhitelistIssuedLogQuery);
            }
        }
    }


    public void countServerLinkCounts(int value) throws Exception {
        String serviceIp = getServiceIp();
        redisTemplate.boundZSetOps(MqttMqttDataServiceImplMqtt.DEVICE_LINK_COUNTS_REDIS_KEY).add(serviceIp, value);
        System.out.println(DateUtil.stampToDate(System.currentTimeMillis(),"yyyy-MM-dd HH:mm:ss")+"当前服务器"+serviceIp+"在线设备数量:"+(value));
    }

    /**
     * 获取本服务器的外网ip
     * @return
     */
    private String getServiceIp() throws Exception {
        JSONObject jsonObject = HttpClient.doGet(cfWeixinConfigService.returnGetIpUrl());

        //获取配置
        List<CfWeixinConfig> cfWeixinConfigs = cfWeixinConfigService.getWeiXinLoginConfigragtion("server_ip_list");
        CfWeixinConfig cfWeixinConfig = cfWeixinConfigs.get(0);
        Map<String, String> map = (Map<String, String>)JSONObject.parseObject(cfWeixinConfig.getValue(), Map.class);
        String ip = "";
        int i = 0;
        String defaultIp = "";
        for (Map.Entry<String, String> entry:map.entrySet()){
            if(i==0){
                defaultIp = entry.getKey();
            }
            i++;
            if(!map.containsKey(jsonObject.getString("data"))){
                break;
            }
            if(jsonObject.getString("data").equals(entry.getKey())){
                ip = entry.getKey();
                break;
            }
        }
        return ip.equals("") ? defaultIp : ip;
    }

    /**
     * 主动监听是否有不需要判断是否频繁操作的handle
     */
    private void startListenOutTimeHandle() throws Exception{
        while (true){
            Thread.sleep(1500); //延时1.5秒
            //剔除掉过期的channel
            long currentTimeMillis = System.currentTimeMillis();
            for (Map.Entry<String, Long> entry: channelActionLimit.entrySet()){
                if(currentTimeMillis-entry.getValue()>5000){
                    channelActionLimit.remove(entry.getKey());
                    break;
                }
            }
            for (Map.Entry<String, Long> entry: channelHeartBeatActionLimit.entrySet()){
                if(currentTimeMillis-entry.getValue()>30000){
                    channelHeartBeatActionLimit.remove(entry.getKey());
                    break;
                }
            }
        }
    }

    @Override
    public String getMinDeviceLinkCountsServiceIp() {
        Set<String> range = redisTemplate.boundZSetOps(MqttMqttDataServiceImplMqtt.DEVICE_LINK_COUNTS_REDIS_KEY).range(0l, 0l);

        //如果数据为空，进行ip组数据初始化
        if(range==null || range.size()==0){
            String ip = null;
            //获取配置
            List<CfWeixinConfig> cfWeixinConfigs = cfWeixinConfigService.getWeiXinLoginConfigragtion("server_ip_list");
            //初始化redis数据
            if(cfWeixinConfigs!=null && cfWeixinConfigs.size()>0){
                CfWeixinConfig cfWeixinConfig = cfWeixinConfigs.get(0);
                Map<String, Integer> map = (Map<String, Integer>)JSONObject.parseObject(cfWeixinConfig.getValue(), Map.class);

                for (Map.Entry<String, Integer> entry:map.entrySet()){
                    redisTemplate.boundZSetOps(MqttMqttDataServiceImplMqtt.DEVICE_LINK_COUNTS_REDIS_KEY).add(entry.getKey(), 0);
                    ip = entry.getKey();
                }
            }
            return ip;

        }else{
            List<String> strings = new ArrayList<>(range);
            return strings.get(0);
        }
    }

    @Override
    public Integer updateCarParkDeviceStatus(String serialNumber) throws Exception {
        CfCarParkDeviceQuery cfCarParkDeviceQuery = new CfCarParkDeviceQuery();
        cfCarParkDeviceQuery.setBarCode(serialNumber);
        CfCarParkDevice updateCarParkDevice = new CfCarParkDevice();
        updateCarParkDevice.setNoticed((byte)0);
        updateCarParkDevice.setLastOnlineTime(System.currentTimeMillis());
        return cfCarParkDeviceService.updateByQuery(updateCarParkDevice,cfCarParkDeviceQuery);
    }
}
