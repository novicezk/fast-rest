package com.zhukai.framework.spring.integration.common;

import org.apache.commons.fileupload.FileItem;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MultipartFile {
    private final FileItem fileItem;
    private final long size;

    public MultipartFile(FileItem fileItem) {
        this.fileItem = fileItem;
        this.size = this.fileItem.getSize();
    }

    public String getName() {
        return this.fileItem.getFieldName();
    }

    public String getOriginalFilename() {
        String filename = this.fileItem.getName();
        if (filename == null) {
            return "";
        } else {
            int unixSep = filename.lastIndexOf("/");
            int winSep = filename.lastIndexOf("\\");
            int pos = winSep > unixSep ? winSep : unixSep;
            return pos != -1 ? filename.substring(pos + 1) : filename;
        }
    }

    public String getContentType() {
        return this.fileItem.getContentType();
    }

    public boolean isEmpty() {
        return this.size == 0L;
    }

    public long getSize() {
        return this.size;
    }

    public byte[] getBytes() {
        byte[] bytes = this.fileItem.get();
        return bytes != null ? bytes : new byte[0];
    }

    public InputStream getInputStream() throws IOException {
        InputStream inputStream = this.fileItem.getInputStream();
        return inputStream != null ? inputStream : new ByteArrayInputStream(new byte[0]);
    }

    public void transferTo(File dest) throws Exception {
        if (dest.exists() && !dest.delete()) {
            throw new IOException(dest.getAbsolutePath() + " already exists and could not be deleted");
        } else {
            this.fileItem.write(dest);
        }
    }
}
