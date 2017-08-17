package com.zhukai.framework.fast.rest.http.request;

import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileUploadException;

import com.zhukai.framework.fast.rest.exception.HttpReadException;

public class HttpRequestDirector {
	private RequestBuilder builder;

	public HttpRequestDirector(RequestBuilder builder) {
		this.builder = builder;
	}

	public HttpRequest createRequest() throws HttpReadException, FileUploadException, UnsupportedEncodingException {
		HttpRequest request = builder.buildUrl();
		if (request != null) {
			builder.buildHead();
			builder.buildBody();
		}
		return request;
	}
}
