package com.zhukai.spring.integration.commons.response;

/**
 * 接口统一返回编码枚举类
 * Created by ShengyangKong
 * on 2015/12/28.
 */
public enum ResponseCodeEnums {

    SUCCESS(-1, "接口调用成功"),

    RUNTIME_ERROR(-2, "程序运行时异常"),

    DB_ERROR(-6, "数据库操作失败"),

    PARAMS_ERROR(-3, "参数错误"),

    LOGIN_ERROR(-4, "用户名或密码错误"),

    USERNAME_REPEAT(-5, "用户名重复"),

    NO_DATA(-7, "无数据"),

    PARAMS_EMPTY(-8, "参数为空"),

    NEED_LOGIN(-9, "需要登陆");

    // 接口返回编码
    private int code;

    // 接口返回编码描述
    private String message;

    ResponseCodeEnums(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
