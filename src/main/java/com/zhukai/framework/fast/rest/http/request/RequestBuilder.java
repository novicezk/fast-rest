package com.zhukai.framework.fast.rest.http.request;

import java.io.IOException;

import org.apache.commons.fileupload.FileUploadException;

public interface RequestBuilder {

	HttpRequest buildUrl() throws IOException;

	HttpRequest buildHead() throws IOException;

	HttpRequest buildBody() throws FileUploadException, IOException;
}
