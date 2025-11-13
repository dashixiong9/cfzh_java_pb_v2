package com.cf.carpark.service.impl;

import com.cf.carpark.service.MQTTClient;
import com.cf.carpark.service.vzenith.cloud.client.BaseClient;
import com.cf.carpark.service.vzenith.cloud.client.MqttClient;
import com.cf.carpark.service.vzenith.cloud.mqtt.MqttCallback;
import com.cf.carpark.service.vzenith.cloud.mqtt.ReplyMessage;
import com.cf.carpark.service.vzenith.cloud.mqtt.UpMessage;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service(version = "1.0.0", loadbalance = "roundrobin")
@Transactional
public class MQTTClientImpl implements MQTTClient {

    private static final Logger logger = LoggerFactory.getLogger(MQTTClientImpl.class);

    public static Map<String, MqttClient> mqttClientList = new HashMap<>();

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public void startMQTTClient(String sn, String payLoad) throws Exception {
        String actionName = "customize";
        if(mqttClientList.get(sn)!=null){
            MqttClient mqttClient = mqttClientList.get(sn);
            try {
                mqttClient.publish(sn, actionName, payLoad);
            } catch (Exception e) {
                mqttClientList.remove(sn);
                e.printStackTrace();
            }
            return;
        }

        // 创建 BaseClient
        BaseClient client = new BaseClient("", "", "");
        client.setReadTimeout(86400);

        MqttClient mqttClient = new MqttClient(client);
        mqttClient.setSn(sn);
        mqttClient.setMqttCallback(new MqttCallback() {
            @Override
            public void deviceStateChanged(String sn, String state) {
                logger.info("{} state changed: {}", sn, state);
            }

            @Override
            public void deviceReplied(String sn, String name, ReplyMessage replyMessage) {
                logger.info("Reply message of {}/{} arrived: {}", sn, name, replyMessage);
            }

            @Override
            public void messageArrived(String sn, String name, UpMessage message) {
                logger.info("Message of {}/{} arrived: {}", sn, name, message);
            }

            @Override
            public void connected(String sn) {
                mqttClientList.put(sn, mqttClient);
                try {
                    mqttClient.publish(sn, actionName, payLoad);
                } catch (Exception e) {
                    mqttClientList.remove(sn);
                    e.printStackTrace();
                }
            }

            @Override
            public void connectionLost(Throwable throwable, String sn) {
                mqttClientList.remove(sn);
                logger.error("Connection lost: {}", throwable.toString());
            }
        });
        String deviceBindServiceIp = redisTemplate.boundValueOps("device_ip_" + sn).get();
        if(StringUtils.isNotEmpty(deviceBindServiceIp)){
            mqttClient.connect(deviceBindServiceIp, sn+"@client");
        }

    }

}
