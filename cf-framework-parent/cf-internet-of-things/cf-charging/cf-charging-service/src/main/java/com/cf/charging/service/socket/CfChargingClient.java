package com.cf.charging.service.socket;

import java.io.Serializable;
import io.netty.channel.Channel;

/**
 * 充电连接客户端
 */
public class CfChargingClient implements Serializable {

    private Channel channel;

    private String frameHeader; //帧头

    private String frameLength; //帧长

    private String protocol;    //协议版本

    private String communicationLogo;   //通讯标识

    private String deviceIdLength;  //设备id长度

    private String deviceId;    //设备id

    private String messageId;   //消息id

    private String command; //指令

    private String messageContent;  //消息内容

    private Byte deviceType;    //设备类型，1-两轮充电桩，2-新能源汽车(4轮)充电桩

    @Override
    public String toString() {
        return "CfChargingClient{" +
                "channel=" + channel +
                ", frameHeader='" + frameHeader + '\'' +
                ", frameLength='" + frameLength + '\'' +
                ", protocol='" + protocol + '\'' +
                ", communicationLogo='" + communicationLogo + '\'' +
                ", deviceIdLength='" + deviceIdLength + '\'' +
                ", deviceId='" + deviceId + '\'' +
                ", messageId='" + messageId + '\'' +
                ", command='" + command + '\'' +
                ", messageContent='" + messageContent + '\'' +
                '}';
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public String getFrameHeader() {
        return frameHeader;
    }

    public void setFrameHeader(String frameHeader) {
        this.frameHeader = frameHeader;
    }

    public String getFrameLength() {
        return frameLength;
    }

    public void setFrameLength(String frameLength) {
        this.frameLength = frameLength;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getCommunicationLogo() {
        return communicationLogo;
    }

    public void setCommunicationLogo(String communicationLogo) {
        this.communicationLogo = communicationLogo;
    }

    public String getDeviceIdLength() {
        return deviceIdLength;
    }

    public void setDeviceIdLength(String deviceIdLength) {
        this.deviceIdLength = deviceIdLength;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Byte getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Byte deviceType) {
        this.deviceType = deviceType;
    }
}
