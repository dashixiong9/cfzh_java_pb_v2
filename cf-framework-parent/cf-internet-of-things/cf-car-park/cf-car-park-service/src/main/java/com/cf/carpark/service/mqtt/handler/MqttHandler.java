package com.cf.carpark.service.mqtt.handler;

import com.alibaba.fastjson.JSONObject;
import com.cf.carpark.service.IMqttDataService;
import com.cf.carpark.service.core.cache.ClientCache;
import com.cf.carpark.service.core.dto.ClientDTO;
import com.cf.carpark.service.core.dto.InitParamDTO;
import com.cf.framework.exception.CustomException;
import com.cf.framework.utils.SpringUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.netty.handler.codec.mqtt.MqttQoS.*;

/**
* @author: gebilaowang
* @date: 2020/7/29 13:22
* @description: MQTT业务类
*/
@Slf4j
@ChannelHandler.Sharable
public class MqttHandler extends ChannelInboundHandlerAdapter {

    public static InitParamDTO initParamDTO;

    public static Integer packetId;

    public static Map<String, Channel> nofFoundDevice = new ConcurrentHashMap<>();  //非法不存在的设备

    public static Map<String, Long> offlineDevice = new ConcurrentHashMap<>();  //记录已经掉线的设备

    public static String PARKING_DEVICE_LINK_IP = "PARKING_DEVICE_LINK_IP_";    //停车设备所在服务的局域网ip

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            if (msg instanceof MqttMessage) {
                Channel channel = ctx.channel();
                MqttMessage message = (MqttMessage) msg;

                MqttMessageType messageType = message.fixedHeader().messageType();  //CONNECT  SUBSCRIBE

                switch (messageType) {
                    // 建立连接
                    case CONNECT:
                            try {
                                updateDeviceLastOnLineTime(channel);
                                this.connect(channel, (MqttConnectMessage) message);
                            }catch (Exception e){
                                //如果用户密码，客户端ID校验不成功，会二次建立CONNECT类型连接
                                //但是没有实际意义
                            }
                        break;
                    // 推送数据
                    case PUBLISH:
                        updateDeviceLastOnLineTime(channel);
                        this.publish(channel, (MqttPublishMessage) message);
                        break;
                    // 订阅主题
                    case SUBSCRIBE:
                        this.subscribe(channel, (MqttSubscribeMessage) message);
                        break;
                    // 退订主题
                    case UNSUBSCRIBE:
                        this.unSubscribe(channel, (MqttUnsubscribeMessage) message);
                        break;
                    // 心跳包
                    case PINGREQ:
                        this.pingReq(channel, message);
                        break;
                    // 断开连接
                    case DISCONNECT:
                        this.disConnect(channel);
                        break;
                    // 确认收到响应报文,用于服务器向客户端推送qos1后，客户端返回服务器的响应
                    case PUBACK:
                        this.puback(channel, (MqttPubAckMessage) message);
                        break;
                    default:
                        if (log.isDebugEnabled()) {
                            log.debug("Nonsupport server message  type of '{}'.", messageType);
                        }
                        break;
                }
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    public void connect(Channel channel, MqttConnectMessage msg) {
        // 客户端登录校验
        if (initParamDTO.getAuthLoginOnOff()) {
            String userName = msg.payload().userName();
            String password = msg.payload().password();
            if(! initParamDTO.getName().equals(userName) || !initParamDTO.getPassword().equals(password)){
                log.info("账号密码不正确，通道关闭===>{}:{}",userName,password);
                // 向客户端返回账号密码不正确消息，其实客户端有重连机制，可直接关闭
//            MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
//                            MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
//                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_BAD_USER_NAME_OR_PASSWORD, true), null);
//            channel.writeAndFlush(okResp);
                channel.close();
                return;
            }
        }

        //客户端ID校验
        if (initParamDTO.getAuthClientIdOnOff()) {
            String clientId = msg.payload().clientIdentifier();
            if(initParamDTO.getClientIds().stream().filter(e->e.equals(clientId)).collect(Collectors.toList()).size() == 0 ){
                log.info("客户端id不匹配，通道关闭===>{}",clientId);
                // 向客户端返回账号密码不正确消息，其实客户端有重连机制，可直接关闭
//            MqttConnAckMessage okResp = (MqttConnAckMessage) MqttMessageFactory.newMessage(new MqttFixedHeader(
//                            MqttMessageType.CONNACK, false, MqttQoS.AT_LEAST_ONCE, false, 0),
//                    new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_REFUSED_IDENTIFIER_REJECTED, true), null);
//            channel.writeAndFlush(okResp);
                channel.close();
                return;
            }
        }

        String channelId = channel.id().asShortText();
        //连接业务校验完成,Qos1类型，需要答复
        MqttFixedHeader mqttFixedHeaderInfo = msg.fixedHeader();
        MqttConnectVariableHeader mqttConnectVariableHeaderInfo = msg.variableHeader();
        //构建返回报文， 可变报头
        MqttConnAckVariableHeader mqttConnAckVariableHeaderBack = new MqttConnAckVariableHeader(MqttConnectReturnCode.CONNECTION_ACCEPTED, mqttConnectVariableHeaderInfo.isCleanSession());
        //构建返回报文， 固定报头
        MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.CONNACK,mqttFixedHeaderInfo.isDup(), MqttQoS.AT_MOST_ONCE, mqttFixedHeaderInfo.isRetain(), 0x02);
        //构建连接回复消息体
        MqttConnAckMessage connAck = new MqttConnAckMessage(mqttFixedHeaderBack, mqttConnAckVariableHeaderBack);
        channel.writeAndFlush(connAck);
        //如果原有池子已经有了，删掉原有池子的通道
        if(ClientCache.channels.size()>0){
            ConcurrentHashMap<String, ClientDTO> channels = ClientCache.channels;

            String sn = "";
            if(msg.payload().clientIdentifier().indexOf("_")>=0){
                sn = msg.payload().clientIdentifier().substring(0, msg.payload().clientIdentifier().indexOf("_"));
            }else{
                sn = msg.payload().clientIdentifier();
            }
            //将设备在线服务器局域网ip记录到redis中去
            StringRedisTemplate redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
            try {
                redisTemplate.boundValueOps(MqttHandler.PARKING_DEVICE_LINK_IP+sn).set(InetAddress.getLocalHost().getHostAddress());
                redisTemplate.expire(MqttHandler.PARKING_DEVICE_LINK_IP+sn,365, TimeUnit.DAYS);
            } catch (UnknownHostException e) {
                System.out.println("连接redis报错");
            }

            for (Map.Entry channelEntry: channels.entrySet()) {
                ClientDTO clientDTO = (ClientDTO)channelEntry.getValue();
                if(clientDTO.getFlagKey().equals(sn)){
                    //移除旧通道
                    removeClient(clientDTO.getChannel());
                    break;
                }
            }
        }
        addClient(channel,msg);
    }

    /**
     * 更新设备最近在线时间
     * @param channel
     */
    public void updateDeviceLastOnLineTime(Channel channel){
        try {
            if(ClientCache.channels.size()>0){
                ConcurrentHashMap<String, ClientDTO> channels = ClientCache.channels;
                for (Map.Entry channelEntry: channels.entrySet()) {
                    ClientDTO clientDTO = (ClientDTO)channelEntry.getValue();
                    if(clientDTO.getChannel().id().asLongText().equals(channel.id().asLongText())){
                        //修改设备最新在线时间
                        clientDTO.setLastOnLineTime(System.currentTimeMillis());
                        break;
                    }
                }
            }
        } catch (Exception e){
            //操作失败不能影响后续操作
        }
    }

    public void pingReq(Channel channel, MqttMessage msg) {
        if (log.isDebugEnabled()) {
            log.debug("MQTT pingReq received.");
        }
        MqttMessage pingResp = new MqttMessage(new MqttFixedHeader(MqttMessageType.PINGRESP, false,
                AT_LEAST_ONCE, false, 0));
        channel.writeAndFlush(pingResp);
    }

    public void disConnect(Channel channel) {
        removeClient(channel);
    }

    public void puback(Channel channel, MqttPubAckMessage msg){
        MqttPubAckMessage sendMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                msg.fixedHeader(),
                MqttMessageIdVariableHeader.from(1),
                null);
        channel.writeAndFlush(sendMessage);
    }

    public void publish(Channel channel, MqttPublishMessage msg) {
        MqttFixedHeader mqttFixedHeaderInfo = msg.fixedHeader();
        MqttQoS qos = mqttFixedHeaderInfo.qosLevel();

        switch (qos) {
            case AT_MOST_ONCE: // 至多一次
//                logger.error("至多一次");
                break;
            case AT_LEAST_ONCE: // 至少一次
                // 构建返回报文， 可变报头
                MqttMessageIdVariableHeader mqttMessageIdVariableHeaderBack = MqttMessageIdVariableHeader.from(msg.variableHeader().packetId());
                // 构建返回报文， 固定报头
                MqttFixedHeader mqttFixedHeaderBack = new MqttFixedHeader(MqttMessageType.PUBACK, false, AT_MOST_ONCE, false, 0x02);
                // 构建PUBACK消息体
                MqttPubAckMessage pubAck = new MqttPubAckMessage(mqttFixedHeaderBack, mqttMessageIdVariableHeaderBack);
                channel.writeAndFlush(pubAck);
                break;
            case EXACTLY_ONCE: // 刚好一次
                // 构建返回报文， 固定报头
                MqttFixedHeader mqttFixedHeaderBack2 = new MqttFixedHeader(MqttMessageType.PUBREC,false, MqttQoS.AT_MOST_ONCE,false,0x02);
                // 构建返回报文， 可变报头
                MqttMessageIdVariableHeader mqttMessageIdVariableHeaderBack2 = MqttMessageIdVariableHeader.from(msg.variableHeader().packetId());
                MqttMessage mqttMessageBack = new MqttMessage(mqttFixedHeaderBack2,mqttMessageIdVariableHeaderBack2);
                channel.writeAndFlush(mqttMessageBack);
                break;
            default:
                break;
        }
//        if(initParamDTO.getCheckQos() != msg.fixedHeader().qosLevel()){
//            log.info("qos类型不是{}，而是{}", initParamDTO.getCheckQos().value(),msg.fixedHeader().qosLevel());
//            channel.close();
//            return;
//        }
        ByteBuf buf = msg.content().duplicate();
        byte[] tmp = new byte[buf.readableBytes()];
        buf.readBytes(tmp);
        String content = new String(tmp);
        //校验传入的数据是否符合要求
        if(StringUtils.isBlank(content)){
            log.error("MQTT接收到的数据包为空===》{}",content);
            puback(channel,msg,"MQTT接收到的数据包为空");
            return;
        }
        // 校验接收的数据是否是JSON格式
        if (initParamDTO.getAuthFormatToJson()) {
            if(!isJsonObject(content)){
                log.error("MQTT接收到的数据包不为JSON格式===》{}",content);
                puback(channel,msg,"MQTT接收到的数据包不为JSON格式");
                return;
            }
        }
        // 校验是否需要返回应答
        if (AT_LEAST_ONCE == initParamDTO.getCheckQos() || EXACTLY_ONCE == initParamDTO.getCheckQos()) {
            // 如果需要加载业务逻辑
            if (initParamDTO.getDataService() != null) {
                String topic = msg.variableHeader().topicName();    //主题名称
                Collection<ClientDTO> clientDTOS = initParamDTO.getClientCache().getAllList();
                for(ClientDTO clientDTO: clientDTOS){
                    if(clientDTO.getChannel().id().asLongText().equals(channel.id().asLongText()) && msg.variableHeader().packetId()>=1){
                        clientDTO.setPacketId(msg.variableHeader().packetId());
                    }else{
                        clientDTO.setPacketId(1);
                    }
                }
                try {
                    initParamDTO.getDataService().inputListening(channel,topic,content,msg.variableHeader().packetId());
                } catch (CustomException e) {
                    if(e.getResultCode().code()==10004){
                        nofFoundDevice.put(channel.id().asLongText(),channel);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getUTF8StringFromGBKString(String gbkStr) {
        try {
            return new String(getUTF8BytesFromGBKString(gbkStr), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError();
        }
    }

    public byte[] getUTF8BytesFromGBKString(String gbkStr) {
        int n = gbkStr.length();
        byte[] utfBytes = new byte[3 * n];
        int k = 0;
        for (int i = 0; i < n; i++) {
            int m = gbkStr.charAt(i);
            if (m < 128 && m >= 0) {
                utfBytes[k++] = (byte) m;
                continue;
            }
            utfBytes[k++] = (byte) (0xe0 | (m >> 12));
            utfBytes[k++] = (byte) (0x80 | ((m >> 6) & 0x3f));
            utfBytes[k++] = (byte) (0x80 | (m & 0x3f));
        }
        if (k < utfBytes.length) {
            byte[] tmp = new byte[k];
            System.arraycopy(utfBytes, 0, tmp, 0, k);
            return tmp;
        }
        return utfBytes;
    }

    public void subscribe(Channel channel, MqttSubscribeMessage msg) {
        MqttFixedHeader fixedHeaderInfo = msg.fixedHeader();
        MqttQoS qos = fixedHeaderInfo.qosLevel();
//        MqttSubAckMessage subAckMessage = (MqttSubAckMessage) MqttMessageFactory.newMessage(
//                new MqttFixedHeader(MqttMessageType.SUBACK, mqttFixedHeaderInfo.isDup(), qos, mqttFixedHeaderInfo.isRetain(), 0x02),
//                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()),
//                new MqttSubAckPayload(0));
//        channel.writeAndFlush(subAckMessage);

        MqttFixedHeader mqttFixedHeaderInfo = new MqttFixedHeader(MqttMessageType.SUBACK, false, AT_MOST_ONCE, false, 0x02);
        MqttMessageIdVariableHeader mqttMessageIdVariableHeader = MqttMessageIdVariableHeader.from(msg.variableHeader().messageId());
        MqttSubAckPayload mqttSubAckPayload = new MqttSubAckPayload(0x01);
        MqttSubAckMessage mqttSubAckMessage = new MqttSubAckMessage(mqttFixedHeaderInfo, mqttMessageIdVariableHeader, mqttSubAckPayload);
        channel.writeAndFlush(mqttSubAckMessage);
    }

    public void unSubscribe(Channel channel, MqttUnsubscribeMessage msg) {
        MqttUnsubAckMessage unSubAckMessage = (MqttUnsubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.UNSUBACK, false, AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(msg.variableHeader().messageId()), null);
        channel.writeAndFlush(unSubAckMessage);
    }

    // 客户端远程关闭
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        log.error("MQTT客户端被强制关闭:{}:{}",ctx.channel().id().asShortText(),cause);
        if (ctx.channel().isActive()) {
            Collection<ClientDTO> allList = initParamDTO.getClientCache().getAllList();
            for (ClientDTO clientDTO: allList){
                if(ctx.channel().id().equals(clientDTO.getChannel().id())){
                    log.error("{} 设备被移除",clientDTO.getFlagKey());
                }
            }
            removeClient(ctx.channel());
        }
    }
    // 客户端新增
    private void addClient(Channel channel,MqttConnectMessage message){
        if (initParamDTO.getClientCache() != null) {
            initParamDTO.getClientCache().add(channel,message);
        }
        try {
            IMqttDataService iMqttDataService = SpringUtil.getBean(IMqttDataService.class);
            iMqttDataService.countServerLinkCounts(initParamDTO.getClientCache().count());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 删除客户端
    public static void removeClient(Channel channel){
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        if (channel.isActive()) {
            channel.close();
        }
        if (initParamDTO.getClientCache() != null) {
            initParamDTO.getClientCache().remove(channel);
        }
        try {
            IMqttDataService iMqttDataService = SpringUtil.getBean(IMqttDataService.class);
            iMqttDataService.countServerLinkCounts(initParamDTO.getClientCache().count());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // 客户端订阅主题
    private void addTopic(Channel channel,String topic,String message){
        if (initParamDTO.getITopicCache() != null) {
            initParamDTO.getITopicCache().add(channel,topic,message);
        }
    }
    // 客户端取消订阅主题
    private void removeTopic(Channel channel,String topic,String message){
        if (initParamDTO.getITopicCache() != null) {
            initParamDTO.getITopicCache().remove(channel,topic,message);
        }
    }

    // 客户端QOS1消息类型，需要服务器响应包
    private void puback(Channel channel, MqttPublishMessage msg, String payLoad){
        int i = msg.variableHeader().packetId();
        if(i<1){
            i = 1;
        }
        MqttPubAckMessage sendMessage = (MqttPubAckMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBACK, false, AT_LEAST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(i),
                payLoad);
        channel.writeAndFlush(sendMessage);
    }

    public MqttHandler(InitParamDTO initParamDTO) {
        this.initParamDTO = initParamDTO;
    }

    /**
     * 判断字符串是否可以转化为json对象
     * @param content
     * @return
     */
    public static boolean isJsonObject(String content) {
        if(StringUtils.isBlank(content))
            return false;
        try {
            JSONObject jsonStr = JSONObject.parseObject(content);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
