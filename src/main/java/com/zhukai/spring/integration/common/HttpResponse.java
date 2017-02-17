package com.zhukai.spring.integration.common;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhukai on 17-2-16.
 */
public class HttpResponse<T> {

    private String contentType = "text/plain; charset=utf-8";
    private int statusCode = 200;
    private String statusCodeStr = "OK";
    private String protocol;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private T result;

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusCodeStr() {
        return statusCodeStr;
    }

    public void setStatusCodeStr(String statusCodeStr) {
        this.statusCodeStr = statusCodeStr;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookie(String key, String value) {
        this.cookies.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeader(String key, String value) {
        this.headers.put(key, value);
    }
}
