package com.cf.auto.pack;

import com.cf.carpark.device.camera.ZhenShi.SeriaDataUtils;
import com.cf.carpark.device.camera.ZhenShiResponse911202002050;
import com.cf.framework.utils.DateUtil;
import com.cf.framework.utils.PlateCode;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotEmpty;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("pack/")
@Validated
public class Execute implements ExecuteSwagger{

    @Autowired
    StringRedisTemplate stringRedisTemplate;


    @RequestMapping(value = "testDuty")
    public Object testDuty(String a1, String b1) throws Exception{

        BigDecimal a = new BigDecimal(a1);
        BigDecimal b = new BigDecimal(b1);
        if(a.compareTo(b) == -1){
            System.out.println("a小于b");
        }

        if(a.compareTo(b) == 0){
            System.out.println("a等于b");
        }

        if(a.compareTo(b) == 1){
            System.out.println("a大于b");
        }

//        if(a.compareTo(b) > -1){
//            System.out.println("a大于等于b");
//        }
//
//        if(a.compareTo(b) < 1){
//            System.out.println("a小于等于b");
//        }
        return null;
//        ZhenShiResponse911202002050 zhenShiResponse911202002050 = new ZhenShiResponse911202002050();

//        SeriaDataUtils.setSerialDataZhenshi(zhenShiResponse911202002050,"京A88888",0,(byte)1,"red","to_top","to_top","miu_new","text","");

//        SeriaDataUtils.setSerialDataZhenshi(zhenShiResponse911202002050,"京A88888",0,(byte)0,"","","","miu_new","voice","add_and_play");
//        SeriaDataUtils.setSerialDataZhenshi(zhenShiResponse911202002050,"月卡车",0,(byte)0,"","","","miu_new","voice","only_add");
//        SeriaDataUtils.setSerialDataZhenshi(zhenShiResponse911202002050,"请缴费12.00元",0,(byte)0,"","","","miu_new","voice","add_and_play");

//        return zhenShiResponse911202002050;
    }

    @RequestMapping(value = "start", method = RequestMethod.GET)
    public String start(
            @NotEmpty(message = "请提供您的网址或者域名ip") String ip, @NotEmpty(message = "请提供您的数据库ip地址") String dbIp,
            @NotEmpty(message = "请提供您的数据库用户名") String dbUserName, @NotEmpty(message = "请提供您的数据库密码") String dbPassword,
            @NotEmpty(message = "请提供您的redis地址") String redisIp, @NotEmpty(message = "请提供您的rdis密码") String redisPassword,
            @NotEmpty(message = "请提供您的mongodb地址") String mgIp, @NotEmpty(message = "请提供您的mongodb用户名") String mgUserName,
            @NotEmpty(message = "请提供您的mongodb密码") String mgPassword, @NotEmpty(message = "打包码1000元/3次(请加微信Dove981011512购买)") String packCode)
    {

        String redisKey = "packing";
        try {
            String packCpde = stringRedisTemplate.opsForValue().get(packCode);
            if(StringUtils.isEmpty(packCpde)){
                return "请联系微信Dove981011512购买打包码,1000元/3次。不退不换，非开发人员勿扰，谢谢配合";
            }
            String pack = stringRedisTemplate.opsForValue().get(redisKey);
            if(pack!=null && !pack.equals("")){
                return "您前面有人正在打包，请您稍等10分钟左右再进行打包，如有问题请联系微信Dove981011512";
            }
            stringRedisTemplate.boundValueOps(redisKey).set("doing",600000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }

        String fileName = (new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date());
        exeCmd("sh /www/java/replace.sh "+ ip+" "+dbIp+" "+dbUserName+" "+dbPassword+" "+redisIp+" "+redisPassword+" "+mgIp+" "+mgUserName+" "+mgPassword+" "+fileName);
        return "打包成功,安装包地址 http://203.195.208.183/"+fileName+".zip， 请5分钟后再下载，请尽快下载，打包的文件会定时清理，centos的linux" +
                "系统可以直接使用wget 安装包远程地址 下载安装包到你的linux服务";
    }

    public void exeCmd(String cmd)
    {
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            System.out.println("出现异常:"+e.getMessage());
        }
    }

    @ApiIgnore
    @RequestMapping(value = "forceStart", method = RequestMethod.GET)
    public String forceStart()
    {
        String redisKey = "packing";
        try {
            stringRedisTemplate.delete(redisKey);
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "redis数据已经清空";
    }

}
