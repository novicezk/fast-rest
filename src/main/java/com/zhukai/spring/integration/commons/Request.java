package com.zhukai.spring.integration.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhukai on 17-1-12.
 */
public class Request {

    private Map<String, Object> attributes;
    private Map<String, String> parameters;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private String actionType;
    private String path;
    private String requestContext;

    public String getRequestContext() {
        return requestContext;
    }

    public void setRequestContext(String requestContext) {
        this.requestContext = requestContext;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getParameter(String key) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        return this.parameters.get(key);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameter(String key, String value) {
        if (this.parameters == null) {
            this.parameters = new HashMap<>();
        }
        this.parameters.put(key, value);
    }

    public String getHeader(String key) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeader(String key, String value) {
        if (this.headers == null) {
            this.headers = new HashMap<>();
        }
        this.headers.put(key, value);
    }

    public Object getAttribute(String key) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        return attributes.get(key);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttribute(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

    public String getCookie(String key) {
        if (this.cookies == null) {
            this.cookies = new HashMap<>();
        }
        return this.cookies.get(key);
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public void setCookie(String key, String value) {
        if (this.cookies == null) {
            this.cookies = new HashMap<>();
        }
        this.cookies.put(key, value);
    }

    @Override
    public String toString() {
        return
                "\n\t attributes: " + attributes +
                        "\n\t parameters: " + parameters +
                        "\n\t headers: " + headers +
                        "\n\t cookies: " + cookies +
                        "\n\t actionType: " + actionType +
                        "\n\t path: " + path;
    }
}
