package com.zhukai.framework.spring.integration.http.request;

import com.zhukai.framework.spring.integration.exception.HttpReadException;

/**
 * Created by homolo on 17-6-20.
 */
public interface RequestBuilder {

    HttpRequest buildUrl() throws HttpReadException;

    HttpRequest buildHead() throws HttpReadException;

    HttpRequest buildBody() throws HttpReadException;
}
