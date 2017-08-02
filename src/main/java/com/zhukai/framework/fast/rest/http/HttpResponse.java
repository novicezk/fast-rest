package com.zhukai.framework.fast.rest.http;

import com.zhukai.framework.fast.rest.FastRestApplication;
import com.zhukai.framework.fast.rest.common.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private String contentType;
    private String fileName;
    private int statusCode = 200;
    private String statusCodeStr = "OK";
    private String protocol;
    private Map<String, String> cookies = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private Object result;

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public String getContentType() {
        return contentType == null ? "text/plain; charset=" + FastRestApplication.getServerConfig().getCharset() : contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusCodeStr() {
        return statusCodeStr;
    }

    public void setStatus(HttpStatus status) {
        statusCode = status.getCode();
        statusCodeStr = status.getCodeStr();
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

    public String getHeaderValue(String key) {
        return this.headers.get(key);
    }
}
