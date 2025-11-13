package com.cf.carpark.service.mqtt;

import com.alibaba.fastjson.JSONObject;
import com.cf.carpark.service.core.cache.ClientCache;
import com.cf.carpark.service.core.cache.IServerListCache;
import com.cf.carpark.service.core.dto.ClientDTO;
import com.cf.carpark.service.core.dto.InitParamDTO;
import com.cf.carpark.service.core.enums.ServerRunStatusEnum;
import com.cf.carpark.service.core.enums.ServerTypeEnum;
import com.cf.carpark.service.core.server.AbsServer;
import com.cf.carpark.service.core.server.IServer;
import com.cf.carpark.service.impl.MqttMqttDataServiceImplMqtt;
import com.cf.carpark.service.mqtt.handler.MqttHandler;
import com.cf.carpark.service.mqtt.handler.MqttRequest;
import com.cf.framework.utils.HttpClient;
import com.cf.framework.utils.SpringUtil;
import com.cf.ucenter.domain.CfWeixinConfig;
import com.cf.ucenter.service.CfWeixinConfigService;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.mqtt.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: gebilaowang
 * @date: 2020-11-04 14:28
 * @description: netty服务
 **/
@Slf4j
public class MqttServer extends AbsServer {

    @Autowired
    private MqttHandler mqttHandler;

    // 主题:客户端
    public ConcurrentHashMap<String, Map<String,List<ClientDTO>>> topics = new ConcurrentHashMap<>();


    public IServer run (){
        if (initParamDTO == null) {
            throw new RuntimeException("启动参数错误:"+ JSONObject.toJSONString(initParamDTO));
        }
        this.initParamDTO = initParamDTO;
        log.info("netty服务启动中...线程数-boss:{},work:{}",initParamDTO.getBoosTread(),initParamDTO.getWorkTread());
        EventLoopGroup boosGroup = new NioEventLoopGroup(initParamDTO.getBoosTread());
        EventLoopGroup workGroup = new NioEventLoopGroup(initParamDTO.getWorkTread());
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boosGroup,workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
//                    .childOption(ChannelOption.SO_BACKLOG,1024)     //等待队列
                    .childOption(ChannelOption.SO_REUSEADDR,true)   //快速复用
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new MqttDecoder(1024*1024));
                            socketChannel.pipeline().addLast( MqttEncoder.INSTANCE);
                            // 加载MQTT编解码协议，包含业务逻辑对象
                            socketChannel.pipeline().addLast("mqttHandler",new MqttHandler(initParamDTO));
                        }
                    });
            serverBootstrap.bind(initParamDTO.getPort()).addListener(future -> {
                log.info("服务端成功绑定端口号={}",initParamDTO.getPort());
            });
            init();
        }catch (Exception e){
            boosGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.error("mqttServer启动失败:{}",e);
        }
        // 将已启动的服务添加到 服务列表，便于以后管理使用
        IServerListCache.SERVER_LIST_MAP.put(ServerTypeEnum.MQTT.TYPE,this);
        return this;
    }

    public ChannelFuture send(Channel channel, String topic,String sendMessage, Integer packetId) throws Exception {
        if(packetId<=0){
            packetId = 1;
        }
        MqttRequest request = new MqttRequest((sendMessage.getBytes()));
        MqttPublishMessage pubMessage = (MqttPublishMessage) MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBLISH,
                        request.isDup(),
                        request.getQos(),
                        request.isRetained(),
                        0),
                new MqttPublishVariableHeader(topic, packetId),
                Unpooled.buffer().writeBytes(request.getPayload()));
        // 超过高水位，则采取同步模式
        if (channel.isWritable()) {
            return channel.writeAndFlush(pubMessage);
        }
        return channel.writeAndFlush(pubMessage).sync();
    }

    @Override
    public void sendByFlageKey(String flageKey, String topic, String sendMessage) throws Exception {
        if (initParamDTO.getClientCache() == null) {
            return;
        }
        if(ClientCache.channels.size()>0){
            ConcurrentHashMap<String, ClientDTO> channels = ClientCache.channels;
            for (Map.Entry channelEntry: channels.entrySet()) {
                ClientDTO clientDTO = (ClientDTO)channelEntry.getValue();
                if(!clientDTO.getFlagKey().equals(flageKey)){
                    continue;
                }
                try {
                    send(clientDTO.getChannel(),topic,sendMessage,clientDTO.getPacketId());
                }catch (Exception e){
                    MqttHandler.removeClient(clientDTO.getChannel());
                    throw e;
                }
            }
        }
    }

    public void sendAll(String topic, String sendMessage) throws Exception {
        if (initParamDTO.getClientCache() == null) {
            return;
        }
        if(initParamDTO.getClientCache().count()>0){
            for (ClientDTO clientDTO : initParamDTO.getClientCache().getAllList()) {
                try {
                    send(clientDTO.getChannel(),topic,sendMessage,clientDTO.getPacketId());
                }catch (Exception e){
                    MqttHandler.removeClient(clientDTO.getChannel());
                    throw e;
                }
            }
        }
    }

    @Override
    public void sendAllWithoutTopic(String sendMessage) throws Exception {
        if (initParamDTO.getClientCache() == null) {
            return;
        }
        if(ClientCache.channels.size()>0){
            ConcurrentHashMap<String, ClientDTO> channels = ClientCache.channels;
            for (Map.Entry channelEntry: channels.entrySet()) {
                ClientDTO clientDTO = (ClientDTO)channelEntry.getValue();
                try {
                    send(clientDTO.getChannel(),"/device/" + clientDTO.getFlagKey() + "/command",sendMessage, clientDTO.getPacketId());
                }catch (Exception e){
                    MqttHandler.removeClient(clientDTO.getChannel());
                    throw e;
                }
            }
        }
    }

    @Override
    public Collection<ClientDTO> getClientAllList() {
        if (initParamDTO.getClientCache() == null || CollectionUtils.isEmpty(initParamDTO.getClientCache().getAllList())){
            return new ArrayList<>();
        }
        return initParamDTO.getClientCache().getAllList();
    }

    @Override
    public List<ClientDTO> getClientListByTopic(String... topic) {
        return null;
    }

    @Override
    public ServerRunStatusEnum status() {
        return null;
    }

    public MqttServer(InitParamDTO initParamDTO) {
        this.initParamDTO = initParamDTO;
    }

    public MqttServer() {
    }

    private void init(){
        try {
            StringRedisTemplate redisTemplate = SpringUtil.getBean(StringRedisTemplate.class);
            String serviceIp = getServiceIp();
            redisTemplate.boundZSetOps(MqttMqttDataServiceImplMqtt.DEVICE_LINK_COUNTS_REDIS_KEY).add(serviceIp, 0d);
            Double score = redisTemplate.boundZSetOps(MqttMqttDataServiceImplMqtt.DEVICE_LINK_COUNTS_REDIS_KEY).score(serviceIp);
            System.out.println("初始化当前服务器"+serviceIp+"的mqtt在线数量："+score.intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取本服务器的外网ip
     * @return
     */
    private String getServiceIp() throws Exception {
        CfWeixinConfigService cfWeixinConfigService = SpringUtil.getBean(CfWeixinConfigService.class);
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
}
