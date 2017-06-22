package com.zhukai.framework.spring.integration.http.request;

/**
 * Created by homolo on 17-6-20.
 */
public class HttpRequestDirector {
    private RequestBuilder builder;

    public HttpRequestDirector(RequestBuilder builder) {
        this.builder = builder;
    }

    public HttpRequest createRequest() {
        HttpRequest request = builder.buildUrl();
        if (request != null) {
            builder.buildHead();
            builder.buildBody();
        }
        return request;
    }
}
