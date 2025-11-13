package com.cf.auto.pack;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import javax.validation.constraints.NotEmpty;

@Api(tags = {"在线云打包"})
public interface ExecuteSwagger {

    @ApiOperation(value = "在线打包(如果地址你提供的是域名请不要带http://或者https://)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ip", value = "服务器ip", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dbIp", value = "数据库地址", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dbUserName", value = "数据库用户名", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "dbPassword", value = "数据库密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "redisIp", value = "redis地址", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "redisPassword", value = "redis密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mgIp", value = "mongodb地址", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mgUserName", value = "mongodb用户名", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "mgPassword", value = "mongodb密码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "packCode", value = "打包码1000元/3次(请加微信Dove981011512购买)", required = true, paramType = "query", dataType = "string")
    })
    public String start(
            @NotEmpty(message = "请提供您的网址或者域名ip") String ip,
            @NotEmpty(message = "请提供您的数据库ip地址") String dbIp,
            @NotEmpty(message = "请提供您的数据库用户名") String dbUserName,
            @NotEmpty(message = "请提供您的数据库密码") String dbPassword,
            @NotEmpty(message = "请提供您的redis地址") String redisIp,
            @NotEmpty(message = "请提供您的rdis密码") String redisPassword,
            @NotEmpty(message = "请提供您的mongodb地址") String mgIp,
            @NotEmpty(message = "请提供您的mongodb用户名") String mgUserName,
            @NotEmpty(message = "请提供您的mongodb密码") String mgPassword,
            @NotEmpty(message = "打包码1000元/3次(请加微信Dove981011512购买)") String packCode);
}
