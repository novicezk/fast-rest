package com.zhukai.framework.fast.rest.http.request;

import java.io.UnsupportedEncodingException;

import org.apache.commons.fileupload.FileUploadException;

import com.zhukai.framework.fast.rest.exception.HttpReadException;

public interface RequestBuilder {

	HttpRequest buildUrl() throws HttpReadException, UnsupportedEncodingException;

	HttpRequest buildHead() throws HttpReadException;

	HttpRequest buildBody() throws HttpReadException, FileUploadException;
}
