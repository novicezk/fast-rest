package com.zhukai.framework.fast.rest.http.request;

import java.io.IOException;

public class HttpRequestDirector {
	private RequestBuilder builder;

	public HttpRequestDirector(RequestBuilder builder) {
		this.builder = builder;
	}

	public HttpRequest createRequest() throws Exception {
		HttpRequest request = builder.buildUrl();
		if (request != null) {
			builder.buildHead();
			builder.buildBody();
		}
		return request;
	}
}
