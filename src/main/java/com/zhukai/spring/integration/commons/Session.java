package com.zhukai.spring.integration.commons;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhukai on 17-1-12.
 */
public class Session {

    private String sessionId;

    private Map<String, Object> attributes;

    public Session(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Object getAttribute(String key) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        return attributes.get(key);
    }

    public Object getAttributes() {
        return attributes;
    }

    public void setAttribute(String key, Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }

}
