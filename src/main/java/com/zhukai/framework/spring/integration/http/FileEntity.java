package com.zhukai.framework.spring.integration.http;

import java.io.InputStream;

/**
 * Created by zhukai on 17-3-9.
 */
public class FileEntity {

    private String fileName;
    private InputStream inputStream;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
