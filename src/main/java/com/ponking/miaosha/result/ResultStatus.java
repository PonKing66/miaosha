package com.ponking.miaosha.result;

import lombok.Getter;

/**
 * @author Ponking
 * @ClassName ResultStatus
 * @date 2020/3/28--11:13
 **/
@Getter
public enum ResultStatus {
    SUCCESS(0, "成功"),
    FAILED(500, "失败"),
    SERVER_ERROR(500100, "服务端异常"),
    BIND_ERROR(500101, "参数校验异常：%s"),
    //登录模块 5002XX
    SESSION_ERROR(500210, "Session不存在或者已经失效"),
    PASSWORD_EMPTY(500211, "登录密码不能为空"),
    MOBILE_EMPTY(500212, "手机号不能为空"),
    MOBILE_ERROR(500213, "手机号格式错误"),
    MOBILE_NOT_EXIST(500214, "手机号不存在"),
    PASSWORD_ERROR(500215, "密码错误"),
    //商品模块 5003XX

    //订单模块 5004XX

    //秒杀模块 5005XX
    MIAO_SHA_OVER(500500, "商品已经秒杀完毕"),
    REPEATE_MIAOSHA(500501,"不能重复秒杀");


    private Integer code;
    private String message;

    ResultStatus(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
