package com.zhukai.framework.fast.rest.http.request;

import com.zhukai.framework.fast.rest.exception.HttpReadException;
import org.apache.commons.fileupload.FileUploadException;

import java.io.UnsupportedEncodingException;

public interface RequestBuilder {

    HttpRequest buildUrl() throws HttpReadException, UnsupportedEncodingException;

    HttpRequest buildHead() throws HttpReadException;

    HttpRequest buildBody() throws HttpReadException, FileUploadException;
}
