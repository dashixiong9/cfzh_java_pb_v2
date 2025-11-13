package com.cf.pay.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.*;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.internal.util.StringUtils;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeCreateRequest;
import com.alipay.api.response.AlipayTradeCreateResponse;
import com.cf.framework.domain.pay.PayCode;
import com.cf.framework.exception.ExceptionCast;
import com.cf.framework.utils.DateUtil;
import com.cf.pay.domain.CfOrder;
import com.cf.pay.domain.CfUserPaymentAgency;
import com.cf.pay.domain.response.ResultMap;
import com.cf.pay.domain.type.PayStatus;
import com.cf.pay.service.AlipayService;
import com.cf.pay.service.CfOrderService;
import com.cf.pay.service.CfUserPaymentAgencyService;
import com.cf.pay.service.config.AlipayConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付宝支付服务
 *
 * @ClassName AlipayServiceImpl
 * @Author 隔壁小王子 981011512@qq.com
 * @Date 2020/6/2/002 6:04
 * @Version 1.0
 **/
@Service(version = "1.0.0", loadbalance = "roundrobin")
@Transactional
public class AlipayServiceImpl implements AlipayService {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private CfUserPaymentAgencyService cfUserPaymentAgencyService;
    @Autowired
    private CfOrderService cfOrderService;

    public AlipayServiceImpl(){}

    public Object initAlipayConfig(CfUserPaymentAgency cfUserPaymentAgency) throws Exception{
        AlipayConfig alipayConfig = new AlipayConfig();
        alipayConfig.setAppId(cfUserPaymentAgency.getAppid());
        alipayConfig.setSubAppId(cfUserPaymentAgency.getSubAppid());
        alipayConfig.setPrivateKey(cfUserPaymentAgency.getPayKey());
        alipayConfig.setAlipayPublicKey(cfUserPaymentAgency.getSecret());
        alipayConfig.setMchId(cfUserPaymentAgency.getMchId());
        alipayConfig.setAuthToken(cfUserPaymentAgency.getAuthToken());

        alipayConfig.setNotifyUrl(cfUserPaymentAgency.getPayNotifyUrl());
        return alipayConfig;
    }

    @Override
    public ResultMap doPay(CfOrder cfOrder, String buyerId, CfUserPaymentAgency cfUserPaymentAgency) throws Exception{

        com.alipay.api.AlipayConfig alipayConfig = new com.alipay.api.AlipayConfig();
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(cfUserPaymentAgency.getSubAppid())){
            alipayConfig.setAppId(cfUserPaymentAgency.getSubAppid());
        }else{
            alipayConfig.setAppId(cfUserPaymentAgency.getAppid());
        }

        alipayConfig.setPrivateKey(cfUserPaymentAgency.getPayKey());
        alipayConfig.setFormat("json");
        alipayConfig.setCharset("UTF-8");
        alipayConfig.setAlipayPublicKey(cfUserPaymentAgency.getSecret());
        alipayConfig.setSignType("RSA2");
        AlipayClient alipayClient = new DefaultAlipayClient(alipayConfig);

        JSONObject json = new JSONObject();
        //订单号
        json.put("out_trade_no",cfOrder.getId());
        //金额 这里的金额是以元为单位的可以不转换但必须是字符串
        json.put("total_amount",cfOrder.getAmountsPayable().setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue()+"");
        //描述
        json.put("subject",cfOrder.getGoodsName());
        switch (cfUserPaymentAgency.getUseScenes()){
            case "ali_app":    //支付宝APP
                AlipayTradeAppPayRequest request = new AlipayTradeAppPayRequest();
                //对象转化为json字符串
                String jsonStr=json.toString();
                //商户通过该接口进行交易的创建下单
                request.setBizContent(jsonStr);
                //回调地址 是能够访问到的域名加上方法名
                request.setNotifyUrl(cfUserPaymentAgency.getPayNotifyUrl());
                try {
                    //使用的是execute
                    AlipayResponse response = alipayClient.sdkExecute(request);
                    return ResultMap.ok().put("data", JSONObject.parse(response.getBody()));
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            case "ali_mp":    //支付宝小程序
                //用户唯一标识id 这里必须使用buyer_id 参考文档  APP支付时不需要
                json.put("buyer_id",buyerId);
                if(org.apache.commons.lang3.StringUtils.isNotEmpty(cfUserPaymentAgency.getAuthToken())){
                    json.put("app_auth_token",cfUserPaymentAgency.getAuthToken());
                }
                try {
                    AlipayTradeCreateRequest mpRequest = new AlipayTradeCreateRequest();
                    //回调地址 是能够访问到的域名加上方法名
                    mpRequest.setNotifyUrl(cfUserPaymentAgency.getPayNotifyUrl());
                    //对象转化为json字符串
                    jsonStr=json.toString();
                    //使用的是execute
                    mpRequest.setBizContent(jsonStr);
                    AlipayTradeCreateResponse response = null;
                    if(StringUtils.isEmpty(cfUserPaymentAgency.getAuthToken())){
                        response = alipayClient.execute(mpRequest);
                    }else{
                        response = alipayClient.execute(mpRequest,null,cfUserPaymentAgency.getAuthToken());
                    }

                    if(response.getCode().equals("10000")){
                        return ResultMap.ok().put("data", JSONObject.parse(response.getBody()));
                    }else{
                        throw new Exception(response.getSubMsg());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            default:
                ExceptionCast.cast(PayCode.PAYMENT_INSTITUTION_DOES_NOT_EXIST);
        }
        return null;
    }

    @Override
    public boolean rsaCertCheck(Map<String, String> params) throws Exception {
        CfOrder cfOrder = cfOrderService.findById(params.get("out_trade_no"), false);
        if(cfOrder.getStatus()== PayStatus.PAID){
            return true;    //直接结束订单后续操作
        }
        CfUserPaymentAgency cfUserPaymentAgency = cfUserPaymentAgencyService.findById(cfOrder.getUserPaymentAgencyId(), false);
        boolean rsaCertCheckV2 = AlipaySignature.rsaCheckV1(params, cfUserPaymentAgency.getSecret(), "utf-8", "RSA2");
        if(rsaCertCheckV2){

            //判断支付宝支付状体
            if(!params.get("trade_status").equals("TRADE_SUCCESS")){
                return true;
            }

            cfOrder.setThirdPartyOrderId(params.get("trade_no"));
            cfOrder.setPayTime(DateUtil.dateToStamp(params.get("gmt_payment"),"yyyy-MM-dd HH:mm:ss"));
            cfOrderService.paySuccessAndupdateOrder(cfOrder,new BigDecimal(params.get("receipt_amount")));
        }
        //如果支付方式为套餐，设置为支付宝支付
        if(cfOrder.getPaymentAgencyShortName().equals("package")){
            if(cfUserPaymentAgency!=null){
                cfOrder.setPaymentAgencyShortName(cfUserPaymentAgency.getPaymentAgencyShortName());
            }else{
                cfOrder.setPaymentAgencyShortName("ali_h5_pay_cny");
            }
        }
        return rsaCertCheckV2;
    }

    private ObjectNode getAliPayRequest(AlipayTradeAppPayModel model) throws UnsupportedEncodingException {
        ObjectNode obj = mapper.createObjectNode();
        //obj.put("subject", URLEncoder.encode(payRequest.getBody(), "GBK"));
        obj.put("subject", model.getSubject());
        obj.put("out_trade_no", model.getOutTradeNo());
        obj.put("timeout_express", model.getTimeoutExpress());
        //obj.put("time_expire", DateTimeUtils.getYmdhmFormatDate(System.currentTimeMillis() + DateUtils.DAY_IN_MILLS));
        obj.put("total_amount", model.getTotalAmount());
        obj.put("product_code", model.getProductCode());
        obj.put("body", model.getBody());
        return obj;
    }

}
