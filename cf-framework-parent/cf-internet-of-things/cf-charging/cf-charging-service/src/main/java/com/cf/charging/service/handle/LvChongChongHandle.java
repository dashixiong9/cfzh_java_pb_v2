package com.cf.charging.service.handle;

import com.alibaba.fastjson.JSONObject;
import com.cf.charging.domain.CfChargingPort;
import com.cf.charging.domain.CfChargingStation;
import com.cf.charging.domain.CfChargingStationDevice;
import com.cf.charging.domain.CfChargingUseLog;
import com.cf.charging.domain.customize.CfChargingDeviceSetting;
import com.cf.charging.domain.customize.CfChargingState;
import com.cf.charging.domain.request.CfChargingPortQuery;
import com.cf.charging.domain.request.CfChargingStationDeviceQuery;
import com.cf.charging.domain.request.CfChargingUseLogQuery;
import com.cf.charging.service.*;
import com.cf.charging.service.socket.CfChargingClient;
import com.cf.chat.domain.CfUserMessage;
import com.cf.framework.utils.StringTools;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * 驴充充 充电桩处理器
 */
@Component
public class LvChongChongHandle {

    @Autowired
    private CfChargingUseLogService cfChargingUseLogService;
    @Autowired
    private CfChargingPortService cfChargingPortService;
    @Autowired
    private CfChargingStationBusinessService cfChargingStationBusinessService;
    @Autowired
    private CfChargingStationDeviceService cfChargingStationDeviceService;
    @Autowired
    private CfChargingStationService cfChargingStationService;

    /**
     * 获取充电客户端
     * @param ctx
     * @param hexString
     * @return
     */
    public CfChargingClient getCfChargingClient(ChannelHandlerContext ctx, String hexString){
        //设备id十六进制字符长度(十进制)
        int idLength = 0;
        CfChargingClient cfChargingClient = new CfChargingClient();

        cfChargingClient.setChannel(ctx.channel());

        cfChargingClient.setFrameHeader(hexString.substring(0,8));                        //帧头
        cfChargingClient.setFrameLength(hexString.substring(8,16));                       //帧长
        cfChargingClient.setProtocol(hexString.substring(16,20));                         //协议版本

        //如果解析出来的协议版本不为0001说明为两轮充电桩，否则为汽车充电桩
        if (cfChargingClient.getProtocol().equals("0001") || cfChargingClient.getProtocol().equals("0002")) {
            cfChargingClient.setDeviceType((byte)2);
            idLength = (Integer.parseInt(hexString.substring(22,24), 16))*2;
            cfChargingClient.setCommunicationLogo(hexString.substring(20,22));                //通讯标识
            cfChargingClient.setDeviceIdLength(hexString.substring(22,24));                   //设备id长度
            cfChargingClient.setDeviceId(hexString.substring(24,24+idLength));                //设备id
            cfChargingClient.setMessageId(hexString.substring(24+idLength,24+idLength+4));    //消息id
            cfChargingClient.setCommand(hexString.substring(24+idLength+4,24+idLength+4+4));  //指令
            if(24+idLength+4+4+3<hexString.length()){
                //说明存在消息内容
                cfChargingClient.setMessageContent(hexString.substring(24+idLength+4+4,hexString.length()-2));
            }
        } else {
            cfChargingClient.setDeviceType((byte)1);
            idLength = (Integer.parseInt(hexString.substring(18,20), 16))*2;
            cfChargingClient.setFrameLength(hexString.substring(8,12));                       //帧长
            cfChargingClient.setProtocol(hexString.substring(12,16));                         //协议版本
            cfChargingClient.setCommunicationLogo(hexString.substring(16,18));                //通讯标识
            cfChargingClient.setDeviceIdLength(hexString.substring(18,20));                   //设备id长度
            cfChargingClient.setDeviceId(hexString.substring(20,20+idLength));                //设备id
            cfChargingClient.setMessageId(hexString.substring(20+idLength,20+idLength+4));    //消息id
            cfChargingClient.setCommand(hexString.substring(20+idLength+4,20+idLength+4+4));  //指令
            if(20+idLength+4+4+3<hexString.length()){
                //说明存在消息内容
                cfChargingClient.setMessageContent(hexString.substring(20+idLength+4+4,hexString.length()-2));
            }
        }
        handleByCommand(ctx, cfChargingClient);

        return cfChargingClient;
    }

    public void handleByCommand(ChannelHandlerContext ctx,CfChargingClient cfChargingClient){
        try {
            ByteBuf returnProtocolBuf = Unpooled.buffer();
            Integer protocolLength = 0;
            if(cfChargingClient.getDeviceType()==(byte)1){
                protocolLength = 4;
            }else{
                protocolLength = 8;
            }
            switch (cfChargingClient.getCommand()){
                case "0001"://注册（请求方：设备）
                    String currentTimeMillis = System.currentTimeMillis()+"";
                    String response = "";
                    if(cfChargingClient.getDeviceType()==(byte)1){
                        response = "0000000000000000000001010000";
                    }
                    String returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0001" + Integer.toHexString(Integer.valueOf(currentTimeMillis.substring(0,currentTimeMillis.length()-3)))+response;
                    String hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    parseChargingStateAndHandle(cfChargingClient, "");
                    break;
                case "0002"://心跳（发起方：设备 主动上报文 服务端需要响应心跳）
                    currentTimeMillis = System.currentTimeMillis()+"";
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0002" + Integer.toHexString(Integer.valueOf(currentTimeMillis.substring(0,currentTimeMillis.length()-3)));
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0003":
                    currentTimeMillis = System.currentTimeMillis()+"";
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0003" + Integer.toHexString(Integer.valueOf(currentTimeMillis.substring(0,currentTimeMillis.length()-3)));
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0004"://注册（请求方：设备）
                    currentTimeMillis = System.currentTimeMillis()+"";
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0004" + Integer.toHexString(Integer.valueOf(currentTimeMillis.substring(0,currentTimeMillis.length()-3)));
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0005"://开启充电（发起方：网关），接受到设备方上报的充电状态
                    parseChargingStateAndHandle(cfChargingClient, "");
                    break;
                case "0006"://查询充电状态（发起方：网关），接受到设备方上报的充电状态，目前是两轮专用
                    parseChargingStateAndHandle(cfChargingClient, "");
                    break;
                case "0017"://重启设备（发起方：网关）
                    System.out.println("设备重启成功!");
                    break;
                case "x0008"://刷卡查询返回可充电时间（发起方：设备）
                    break;
                case "0009"://上报刷卡订单（刷卡开电成功后上报）（发起方：设备）
                    break;
                case "0007"://停止充电（发起方：网关）
                    parseChargingStateAndHandle(cfChargingClient, "");
                    break;
                case "0010"://充电结束（充电结算帐单上传）（发起方：设备）
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0010" + "01";
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);

                    parseChargingStateAndHandle(cfChargingClient, "");
                    break;
                case "0016"://故障上报(主动上报报文)（发起方：设备）
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0016" + "01";
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0015"://主动触发 OTA 升级（发起方：网关）
                    break;
                case "0021"://两轮充电插座状态更改上报（发起方：设备）
                    String messageContent = cfChargingClient.getMessageContent();
                    if(StringUtils.isNotEmpty(messageContent)){
                        byte charingStatus = Byte.valueOf(messageContent.substring(2,4),16);
                        if(charingStatus==(byte)2){
                            //说明有故障，更新端口被占用状态
                            CfChargingStationDeviceQuery cfChargingStationDeviceQuery = new CfChargingStationDeviceQuery();
                            cfChargingStationDeviceQuery.setBarCode(cfChargingClient.getDeviceId());
                            cfChargingStationDeviceQuery.setType((byte)1);
                            List<CfChargingStationDevice> cfChargingStationDeviceList = cfChargingStationDeviceService.getListByQuery(cfChargingStationDeviceQuery);
                            if(cfChargingStationDeviceList!=null && cfChargingStationDeviceList.size()>0){
                                CfChargingPortQuery cfChargingPortQuery = new CfChargingPortQuery();
                                cfChargingPortQuery.setChargingDeviceId(cfChargingStationDeviceList.get(0).getId());
                                cfChargingPortQuery.setPortNumber(Byte.valueOf(messageContent.substring(0,2),16));
                                CfChargingPort updateCfChargingPort = new CfChargingPort();
                                updateCfChargingPort.setStatus((byte)0);
                                cfChargingPortService.updateByQuery(updateCfChargingPort, cfChargingPortQuery);
                            }
                        }

                    }

                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0021" + "01";
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0025"://OTA 可用版本查询据（发起方：设备）
                    break;
                case "0026"://请求 OTA 数据（发起方：设备）
                    break;
                case "0030"://两轮充电状态定时上报所有端口功率（发起方：设备）
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0030" + "01";
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0035"://获取文本数据（发起方：设备）
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0035" + "01"+"001c"+"7777772e6366656e672e77616e67";
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0048"://两轮 实时充电状态定时上报所有端口耗电量（发起方：设备）
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0048" + "01";
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);
                    break;
                case "0101"://读取充电枪实时数据（发起方：网关）
                    parseChargingStateAndHandle(cfChargingClient, "");
                    break;
                case "0105"://充电枪实时数据上报（发起方：设备）
                    returnProtocol = cfChargingClient.getProtocol()+"01" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0105" + "01";
                    hexXOP = StringTools.hexXOP(returnProtocol);
                    returnProtocol += hexXOP;
                    returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();
                    returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
                    ctx.channel().writeAndFlush(returnProtocolBuf);

                    cfChargingClient.setCommand("0101");
                    parseChargingStateAndHandle(cfChargingClient, "");
                    break;
                case "0102"://计费模型设置据（发起方：网关 主动下发报文）
                    break;
                case "0104"://车辆基础信息读取（发起方：网关）
                    break;
                case "0106"://充电枪状态变更上报（发起方：设备）
                    break;
                case "0107"://设置设备配置信息（发起方：网关）
                    break;
                case "0108"://查询设备配置信息（发起方：网关）
                    break;
                case "0012"://查询通讯模块信息（发起方：网关）
                    break;
                case "0060"://通用配置调试指令（发起方：网关）
                    break;
                default:
                    System.out.println("暂时不支持的指令："+cfChargingClient.getCommand());
                    //调用此方法，避免没有调用release()方法，最终导致gc问题
                    ctx.channel().writeAndFlush(new TextWebSocketFrame("FF"));
            }
        } catch (Exception e) {
            //调用此方法，避免没有调用release()方法，最终导致gc问题
            ctx.channel().writeAndFlush(new TextWebSocketFrame("FF"));
            throw e;
        }
    }

    /**
     * 开始充电
     * @param cfChargingClient
     * @param chargingPort  充电口
     * @param chargingAmount   充电额度
     */
    public void startCharging(CfChargingClient cfChargingClient, String chargingPort, Integer chargingAmount, String chargingUseLogId) throws Exception{
        ByteBuf returnProtocolBuf = Unpooled.buffer();
        cfChargingClient.setMessageId(StringTools.preMakeUp("0", Integer.toHexString(StringTools.getRadomInt(0,65534)), 4));
        String returnProtocol = "";
        Integer protocolLength = 0;
        if(cfChargingClient.getDeviceType()==(byte)2){
            //汽车
            protocolLength = 8;
            returnProtocol = cfChargingClient.getProtocol()+"00" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0005" + StringTools.preMakeUp("0", Integer.toHexString(new Integer(chargingPort)), 2)+"00000000"+"00004e20"+StringTools.preMakeUp("0", Integer.toHexString(chargingAmount), 8)+
                    "03"+StringTools.preMakeUp("0",chargingUseLogId,40)+"01"+"00000000"+"01"+"05a0"+"0001"+"0001";
        }else{
            //两轮
            protocolLength = 4;
            returnProtocol = cfChargingClient.getProtocol()+"00" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0005" + StringTools.preMakeUp("0", Integer.toHexString(new Integer(chargingPort)), 2)+"0014"+"0708"+"001e"+"001e"+StringTools.preMakeUp("0", Integer.toHexString(chargingAmount), 4)+
                    StringTools.preMakeUp("0",chargingUseLogId,40)+"01"+"01"+"01";
        }

        String hexXOP = StringTools.hexXOP(returnProtocol);
        returnProtocol += hexXOP;
        returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();

        returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
        cfChargingClient.getChannel().writeAndFlush(returnProtocolBuf);
    }

    /**
     * 停止充电
     * @param cfChargingClient
     * @param chargingPort  充电口
     */
    public void stopCharging(CfChargingClient cfChargingClient, String chargingPort){
        Integer protocolLength = 0;
        if(cfChargingClient.getDeviceType()==(byte)2){
            protocolLength = 8;
        }else{
            protocolLength = 4;
        }
        ByteBuf returnProtocolBuf = Unpooled.buffer();
        cfChargingClient.setMessageId(StringTools.preMakeUp("0", Integer.toHexString(StringTools.getRadomInt(0,65534)), 4));
        String returnProtocol = cfChargingClient.getProtocol()+"00" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0007" + StringTools.preMakeUp("0", Integer.toHexString(new Integer(chargingPort)), 2);
        String hexXOP = StringTools.hexXOP(returnProtocol);
        returnProtocol += hexXOP;
        returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), protocolLength)) + returnProtocol).toLowerCase();

        returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
        cfChargingClient.getChannel().writeAndFlush(returnProtocolBuf);

        parseChargingStateAndHandle(cfChargingClient, chargingPort);
    }

    /**
     * 配置充电设备配置
     * @param cfChargingClient
     * @param cfChargingDeviceSetting
     */
    public void chargingSettiong(CfChargingClient cfChargingClient, CfChargingDeviceSetting cfChargingDeviceSetting){
        ByteBuf returnProtocolBuf = Unpooled.buffer();
        cfChargingClient.setMessageId(StringTools.preMakeUp("0", Integer.toHexString(StringTools.getRadomInt(0,65534)), 4));
        String returnProtocol = cfChargingClient.getProtocol()+"00" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0107"
                + StringTools.preMakeUp("0", Integer.toHexString(cfChargingDeviceSetting.getMaxVolt()*100), 8)
                +StringTools.preMakeUp("0", Integer.toHexString(cfChargingDeviceSetting.getMaxAmpere()*100), 8)
                +StringTools.preMakeUp("0", Integer.toHexString(cfChargingDeviceSetting.getReportRegularly()), 4)
                +StringTools.preMakeUp("0", Integer.toHexString(cfChargingDeviceSetting.getHeartbeat()), 4)
                +StringTools.preMakeUp("0", Integer.toHexString(cfChargingDeviceSetting.getMaxPower()), 8);
        String hexXOP = StringTools.hexXOP(returnProtocol);
        returnProtocol += hexXOP;
        returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), 8)) + returnProtocol).toLowerCase();
        returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));

        cfChargingClient.getChannel().writeAndFlush(returnProtocolBuf);
    }

    /**
     * 获取充电口实时数据
     * @param cfChargingClient
     * @param chargingPort  充电口
     */
    public void getPortRealTimeData(CfChargingClient cfChargingClient, String chargingPort){
        ByteBuf returnProtocolBuf = Unpooled.buffer();

        cfChargingClient.setMessageId(StringTools.preMakeUp("0", Integer.toHexString(StringTools.getRadomInt(0,65534)), 4));
        String returnProtocol = cfChargingClient.getProtocol()+"00" + cfChargingClient.getDeviceIdLength() + cfChargingClient.getDeviceId() + cfChargingClient.getMessageId() + "0101" + StringTools.preMakeUp("0", Integer.toHexString(new Integer(chargingPort)), 2);
        String hexXOP = StringTools.hexXOP(returnProtocol);
        returnProtocol += hexXOP;
        returnProtocol = (cfChargingClient.getFrameHeader() + (StringTools.preMakeUp("0", Integer.toHexString(returnProtocol.length()/2), 8)) + returnProtocol).toLowerCase();

        returnProtocolBuf.writeBytes(StringTools.hexStringToBytes(returnProtocol));
        cfChargingClient.getChannel().writeAndFlush(returnProtocolBuf);
    }

    /**
     * 解析充电状态并且处理相关业务
     * @param cfChargingClient
     * @param port  端口号(设备主动上报时，该参数会从设备报文中解析出来，此参数参数传空字符串即可)
     * @return
     */
    public CfChargingState parseChargingStateAndHandle(CfChargingClient cfChargingClient, String port){
        String messageContent = cfChargingClient.getMessageContent();
        CfChargingState cfChargingState = new CfChargingState();
        List<CfChargingStationDevice> cfChargingStationDevices = null;
        try{
            switch (cfChargingClient.getCommand()){
                case "0001":
                    //该充电站名下的所有充电口状态恢复空闲状态
                    cfChargingStationDevices = getDeviceListBySerialNumber(cfChargingClient.getDeviceId());
                    if(cfChargingStationDevices!=null && cfChargingStationDevices.size()>0){
                        CfChargingPortQuery cfChargingPortQuery = new CfChargingPortQuery();
                        cfChargingPortQuery.setChargingDeviceId(cfChargingStationDevices.get(0).getId());
                        CfChargingPort cfChargingPort = new CfChargingPort();
                        cfChargingPort.setStatus((byte)0);
                        cfChargingPortService.updateByQuery(cfChargingPort, cfChargingPortQuery);
                    }
                    break;
                case "0101":
                    cfChargingState = parseComplicatedChargingState(cfChargingClient, port);
                    break;
                case "0005":
                    if(StringUtils.isNotEmpty(messageContent)){
                        cfChargingState.setPort(new Integer(Integer.parseInt(messageContent.substring(2,4),16)).toString());
                        CfChargingUseLog cfChargingUseLog = cfChargingUseLogService.getCharingUseLogByDevcieSerialNumber(cfChargingClient.getDeviceId(), cfChargingState.getPort());
                        if(new Integer(Integer.parseInt(messageContent.substring(0,2),16)).byteValue()==(byte)1){
                            cfChargingState.setChargingStatus((byte)2);
                            cfChargingState.setPortStatus((byte)2);
                            //开始扣费
                            if(cfChargingUseLog!=null){
                                cfChargingStationBusinessService.charingAndBilling(cfChargingUseLog);
                            }
                        }else{
                            //说明启动充电失败 删除充电记录和账单
                            if(cfChargingUseLog.getEndTime()==0){
                                cfChargingUseLogService.delete(cfChargingUseLog.getId());
                            }
                        }
                        if(messageContent.length()>4){
                            cfChargingState.setCarVin(messageContent.substring(4));
                        }
                        //获取实时充电状态
                        getPortRealTimeData(cfChargingClient, cfChargingState.getPort());
                    }
                    break;
                case "0006":
                    if(StringUtils.isNotEmpty(messageContent)){
                        if(cfChargingClient.getDeviceType()==(byte)1){
                            //两轮
                            if(new Integer(Integer.parseInt(messageContent.substring(0,2),16)).byteValue()==(byte)1){
                                cfChargingState.setPort(new Integer(Integer.parseInt(messageContent.substring(2,4),16)).toString());
                                cfChargingState.setChargingPower(new Float(Long.valueOf(messageContent.substring(4,8),16)));
                                cfChargingState.setOrderId(messageContent.substring(18,58).substring(21));
                                cfChargingState.setChargingedKwh(new Double(Long.valueOf(messageContent.substring(58,62),16))/100);
                            }
                        }
                    }
                    break;
                case "0007":
                    if(StringUtils.isNotEmpty(messageContent)){
                        if(new Integer(Integer.parseInt(messageContent.substring(0,2),16)).byteValue()==(byte)1){
                            cfChargingState.setChargingStatus((byte)3);
                        }
                        cfChargingState.setPort(port);
                        if(cfChargingClient.getDeviceType()==(byte)2 && messageContent.length()>4){
                            cfChargingState.setOrderId(messageContent.substring(4));
                        }else if(cfChargingClient.getDeviceType()==(byte)1){
                            //根据端口查询正在充电的账单
                            CfChargingUseLog cfChargingUseLog = cfChargingUseLogService.getCharingUseLogByDevcieSerialNumber(cfChargingClient.getDeviceId(), cfChargingState.getPort());
                            if(cfChargingUseLog!=null){
                                cfChargingState.setOrderId(cfChargingUseLog.getId());
                            }
                        }
                    }
                    break;
                case "0010":
                    cfChargingState = parseFinishCharingComplicatedChargingState(cfChargingClient, port);
                    cfChargingStationBusinessService.finishCharing(cfChargingState);
                    break;
                case "0105":
                    cfChargingState = parseComplicatedChargingState(cfChargingClient, port);
                    break;
            }

        } catch (Exception e){
            e.printStackTrace();
        }

        if(StringUtils.isEmpty(cfChargingState.getPort())){
            return cfChargingState;
        }
        //给相关车主实时推送充电消息
        if(StringUtils.isNotEmpty(cfChargingState.getOrderId()) && cfChargingState.getChargingStatus()!=null){
            CfChargingUseLogQuery cfChargingUseLogQuery = new CfChargingUseLogQuery();
            cfChargingUseLogQuery.setId(cfChargingState.getOrderId());
            List<CfChargingUseLog> cfChargingUseLogList = cfChargingUseLogService.getListByQuery(cfChargingUseLogQuery);
            if(cfChargingUseLogList!=null && cfChargingUseLogList.size()>0 && cfChargingUseLogList.get(0).getChargingStatus()!=(byte)3){
                try{
                    cfChargingStationBusinessService.sendMessage(cfChargingState, cfChargingUseLogList.get(0).getUid(), 5);
                    //如果充电状态为正在充电，实时更新相关停车记录的 充电费用和已经充电度数
                    if(cfChargingUseLogList.get(0).getEndTime()==0 && cfChargingUseLogList.get(0).getChargingStatus().byteValue()==(byte)2){
                        CfChargingUseLog updateCfChargingUseLog = new CfChargingUseLog();
                        updateCfChargingUseLog.setId(cfChargingUseLogList.get(0).getId());
                        updateCfChargingUseLog.setChargingPower((new Double(cfChargingState.getChargingPower())).intValue());
                        updateCfChargingUseLog.setChargingedKwh(cfChargingState.getChargingedKwh().floatValue());
                        //计算充电记录的实时电量费用
                        updateCfChargingUseLog.setChargingFee((new BigDecimal(cfChargingState.getChargingedKwh())).multiply(cfChargingUseLogList.get(0).getChargingPrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
                        //计算充电记录的实时服务费用
                        updateCfChargingUseLog.setServiceFee((new BigDecimal(cfChargingState.getChargingedKwh())).multiply(cfChargingUseLogList.get(0).getServicePrice()).setScale(2,BigDecimal.ROUND_HALF_UP));
                        cfChargingUseLogService.update(updateCfChargingUseLog);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if(StringUtils.isNotEmpty(cfChargingClient.getDeviceId()) && (StringUtils.isNotEmpty(port) || StringUtils.isNotEmpty(cfChargingState.getPort())) && cfChargingState!=null && cfChargingState.getPortStatus()!=null){
            if(cfChargingStationDevices==null || cfChargingStationDevices.size()==0){
                cfChargingStationDevices = getDeviceListBySerialNumber(cfChargingClient.getDeviceId());
            }
            if(cfChargingStationDevices!=null && cfChargingStationDevices.size()>0){
                //更新该设备的枪口的占用情况
                CfChargingPort cfChargingPort = new CfChargingPort();
                cfChargingPort.setStatus(cfChargingState.getPortStatus());

                CfChargingPortQuery cfChargingPortQuery = new CfChargingPortQuery();
                cfChargingPortQuery.setChargingDeviceId(cfChargingStationDevices.get(0).getId());
                cfChargingPortQuery.setPortNumber(new Byte(cfChargingState.getPort()));
                cfChargingPortService.updateByQuery(cfChargingPort, cfChargingPortQuery);
            }
        }
        return cfChargingState;
    }

    private List<CfChargingStationDevice> getDeviceListBySerialNumber(String serialNumber){
        CfChargingStationDeviceQuery cfChargingStationDeviceQuery = new CfChargingStationDeviceQuery();
        cfChargingStationDeviceQuery.setType((byte)1);
        cfChargingStationDeviceQuery.setBarCode(serialNumber);
        cfChargingStationDeviceQuery.setBrand("lv_chong_chong");
        return cfChargingStationDeviceService.getListByQuery(cfChargingStationDeviceQuery);
    }

    /**
     * 解析复杂的充电状态(充电数据实时上报 时 用到)
     * @param cfChargingClient
     * @param port
     * @return
     */
    public CfChargingState parseComplicatedChargingState(CfChargingClient cfChargingClient, String port){

        String messageContent = cfChargingClient.getMessageContent();
        CfChargingState cfChargingState = new CfChargingState();

        cfChargingState.setPort(new Integer(Integer.parseInt(messageContent.substring(0,2),16)).toString());
        cfChargingState.setChargingStatus(new Integer(Integer.parseInt(messageContent.substring(2,4),16)).byteValue());
        //驴充充设备充电口状态为 归位/未连接/已连接 ，此处我们自己转换一下 为空闲/连接/正在充电
        if(cfChargingState.getChargingStatus()>=(byte)0 && cfChargingState.getChargingStatus()<=(byte)2){
            cfChargingState.setPortStatus(cfChargingState.getChargingStatus());
        }else{
            cfChargingState.setPortStatus((byte)1); //否则均设置为已经连接，但是未充电
        }
        cfChargingState.setParkingSpaceOccupancy(new Integer(Integer.parseInt(messageContent.substring(6,8),16)).byteValue());
        cfChargingState.setOrderId(messageContent.substring(8,48).substring(21));   //本系统订单号长度为19位
        cfChargingState.setCarVin(messageContent.substring(48,82));
        cfChargingState.setGroundLockStatus(Byte.valueOf(messageContent.substring(82,84),16));
        cfChargingState.setBmsVoltRequest(new Double(Long.valueOf(messageContent.substring(84,88),16)).floatValue()/10);
        cfChargingState.setBmsAmpereRequest(new Double(Long.valueOf(messageContent.substring(88,92),16)).floatValue()/10);
        cfChargingState.setBmsChargingMode(Byte.valueOf(messageContent.substring(92,94),16));
        cfChargingState.setChargingVolt(new Long(Long.valueOf(messageContent.substring(94,98),16)).intValue()/10);
        cfChargingState.setChargingAmpere(new Integer(Integer.parseInt(messageContent.substring(98,102),16)).intValue()/10);
        cfChargingState.setChargingedKwh(new Double(Long.valueOf(messageContent.substring(102,110),16))/10000);
        cfChargingState.setChargingedTime(new Long(Long.valueOf(messageContent.substring(110,114),16)).longValue()*60000);
        cfChargingState.setChargingPower(new Double(Long.valueOf(messageContent.substring(114,122),16)).floatValue());
        cfChargingState.setBeforeElectricityMeter(new Double(Long.valueOf(messageContent.substring(122,138),16)).doubleValue()/10000);
        cfChargingState.setCurrentElectricityMeter(new Double(Long.valueOf(messageContent.substring(138,154),16)).doubleValue()/10000);
        cfChargingState.setChargingedRemainingTime(new Long(Long.valueOf(messageContent.substring(154,158),16)).longValue()*60000);
        cfChargingState.setCurrentSoc(Byte.valueOf(messageContent.substring(158,160),16));
        return cfChargingState;
    }

    /**
     * 解析结束充电时的数据
     * @param cfChargingClient
     * @param port
     * @return
     */
    public CfChargingState parseFinishCharingComplicatedChargingState(CfChargingClient cfChargingClient, String port){

        String messageContent = cfChargingClient.getMessageContent();
        CfChargingState cfChargingState = new CfChargingState();

        cfChargingState.setFinishCode(new Integer(Integer.parseInt(messageContent.substring(0,2),16)).byteValue());
        cfChargingState.setPort(new Integer(Integer.parseInt(messageContent.substring(2,4),16)).toString());
        cfChargingState.setOrderId(messageContent.substring(4,44).substring(21));   //本系统订单号长度为19位
        if(cfChargingClient.getDeviceType()==(byte)2){
            cfChargingState.setChargingedKwh(new Double(Long.valueOf(messageContent.substring(44,52),16))/10000);
            cfChargingState.setChargingedTime((new Long(Long.valueOf(messageContent.substring(68,76),16))-Long.valueOf(messageContent.substring(76,84),16))*1000);
            cfChargingState.setCurrentSoc(new Integer(Integer.parseInt(messageContent.substring(84,86),16)).byteValue());
            cfChargingState.setCarVin(messageContent.substring(54,88));
        }else{
            cfChargingState.setChargingedKwh(new Double(Long.valueOf(messageContent.substring(44,48),16))/100);
            cfChargingState.setChargingPower(new Float(Long.valueOf(messageContent.substring(48,52),16)));
            cfChargingState.setEndTime(Long.valueOf(messageContent.substring(52,60),16)*1000);  //转为毫秒
        }

        return cfChargingState;
    }

}
