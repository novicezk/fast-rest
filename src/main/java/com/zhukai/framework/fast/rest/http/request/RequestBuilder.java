package com.zhukai.framework.fast.rest.http.request;

import com.zhukai.framework.fast.rest.exception.HttpReadException;
import org.apache.commons.fileupload.FileUploadException;

public interface RequestBuilder {

    HttpRequest buildUrl() throws HttpReadException;

    HttpRequest buildHead() throws HttpReadException;

    HttpRequest buildBody() throws HttpReadException, FileUploadException;
}
