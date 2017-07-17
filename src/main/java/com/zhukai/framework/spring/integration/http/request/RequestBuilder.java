package com.zhukai.framework.spring.integration.http.request;

import com.zhukai.framework.spring.integration.exception.HttpReadException;
import org.apache.commons.fileupload.FileUploadException;

public interface RequestBuilder {

    HttpRequest buildUrl() throws HttpReadException;

    HttpRequest buildHead() throws HttpReadException;

    HttpRequest buildBody() throws HttpReadException, FileUploadException;
}
