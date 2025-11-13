package com.cf.carpark.service.socket;

import io.netty.channel.Channel;

import java.io.Serializable;

/**
 * 平板地锁接客户端
 */
public class CfGroundLockClient implements Serializable {

    private Channel channel;

    private String deviceId;    //设备id

    private String messageId;   //消息id(16进制)

    private String command; //指令(16进制)

    private Integer status;    //锁的状态

    private Integer voltage;  //电池电压

    private Integer exceptionCode;   //异常代码

    private Integer loRaSignalStrength;    //LoRa信号强度

    private Float percentageOfBatteryRemaining; //电池剩余电量百分比

    private Integer baffleLiftAngle;  //挡板升起角度

    private boolean heartbeatSetted;    //心跳是否已经设置

    @Override
    public String toString() {
        return "CfGroundLockClient{" +
                "设备号='" + deviceId + '\'' +
                ", 消息id='" + messageId + '\'' +
                ", 命令='" + command + '\'' +
                ", 状态=" + status +
                ", 电压=" + voltage +
                ", 异常编号=" + exceptionCode +
                ", lora信号强度=" + loRaSignalStrength +
                ", 电池剩余电量百分比=" + percentageOfBatteryRemaining +
                ", 挡板升起角度=" + baffleLiftAngle +
                '}';
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getVoltage() {
        return voltage;
    }

    public void setVoltage(Integer voltage) {
        this.voltage = voltage;
    }

    public Integer getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(Integer exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public Integer getLoRaSignalStrength() {
        return loRaSignalStrength;
    }

    public void setLoRaSignalStrength(Integer loRaSignalStrength) {
        this.loRaSignalStrength = loRaSignalStrength;
    }

    public Float getPercentageOfBatteryRemaining() {
        return percentageOfBatteryRemaining;
    }

    public void setPercentageOfBatteryRemaining(Float percentageOfBatteryRemaining) {
        this.percentageOfBatteryRemaining = percentageOfBatteryRemaining;
    }

    public Integer getBaffleLiftAngle() {
        return baffleLiftAngle;
    }

    public void setBaffleLiftAngle(Integer baffleLiftAngle) {
        this.baffleLiftAngle = baffleLiftAngle;
    }

    public boolean getHeartbeatSetted() {
        return heartbeatSetted;
    }

    public void setHeartbeatSetted(boolean heartbeatSetted) {
        this.heartbeatSetted = heartbeatSetted;
    }
}
