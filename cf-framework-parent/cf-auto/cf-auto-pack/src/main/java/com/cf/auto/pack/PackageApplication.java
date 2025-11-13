package com.cf.auto.pack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 请在此填写描述
 *
 * @ClassName SmsApiApplication
 * @Author 隔壁小王子 981011512@qq.com
 * @Date 2020/11/21/021 23:36
 * @Version 1.0
 **/
@EnableAutoConfiguration(exclude = {MultipartAutoConfiguration.class})
@ComponentScan(basePackages = {"com.cf.auto.pack"})
@EnableSwagger2
public class PackageApplication {
    public static void main(String[] args) {
        SpringApplication.run(PackageApplication.class,args);
    }
}
