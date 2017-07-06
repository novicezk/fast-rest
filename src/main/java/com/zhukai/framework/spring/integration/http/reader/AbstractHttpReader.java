package com.zhukai.framework.spring.integration.http.reader;

import com.zhukai.framework.spring.integration.exception.HttpReadException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by homolo on 17-6-20.
 */
public abstract class AbstractHttpReader {
    private static final String DEFAULT_PARSE_CHARSET = "utf-8";

    public String readLine() throws HttpReadException {
        try {
            byte[] bytes = readByteArrayLimitSize(0);
            int length = bytes.length > 0 && bytes[bytes.length - 1] == 13 ? bytes.length - 1 : bytes.length;
            return new String(bytes, 0, length, DEFAULT_PARSE_CHARSET);
        } catch (IOException e) {
            throw new HttpReadException(e);
        }
    }


    public String readLimitSize(int size) throws HttpReadException {
        try {
            byte[] bytes = readByteArrayLimitSize(size);
            return new String(bytes, DEFAULT_PARSE_CHARSET);
        } catch (IOException e) {
            throw new HttpReadException(e);
        }
    }

    public InputStream readFileInputStream(int size) throws HttpReadException {
        try {
            byte[] bytes = readByteArrayLimitSize(size);
            return new ByteArrayInputStream(bytes);
        } catch (IOException e) {
            throw new HttpReadException(e);
        }
    }

    protected abstract byte[] readByteArrayLimitSize(int size) throws IOException;
}
