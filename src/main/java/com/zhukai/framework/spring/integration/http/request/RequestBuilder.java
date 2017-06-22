package com.zhukai.framework.spring.integration.http.request;

/**
 * Created by homolo on 17-6-20.
 */
public interface RequestBuilder {

    HttpRequest buildUrl();

    HttpRequest buildHead();

    HttpRequest buildBody();
}
