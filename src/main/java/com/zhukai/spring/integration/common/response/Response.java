package com.zhukai.spring.integration.common.response;

/**
 * Created by zhukai on 16-11-29.
 */
public class Response<T> {
    private int code;
    private String message;
    private T body;

    public Response(int code, String message, T body) {
        this.code = code;
        this.message = message;
        this.body = body;
    }

    public Response(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "Response{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", body=" + body +
                '}';
    }
}
