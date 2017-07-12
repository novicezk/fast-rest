package com.zhukai.framework.spring.integration.http.request;

import com.zhukai.framework.spring.integration.exception.HttpReadException;

public class HttpRequestDirector {
    private RequestBuilder builder;

    public HttpRequestDirector(RequestBuilder builder) {
        this.builder = builder;
    }

    public HttpRequest createRequest() throws HttpReadException {
        HttpRequest request = builder.buildUrl();
        if (request != null) {
            builder.buildHead();
            builder.buildBody();
        }
        return request;
    }
}
