package com.zhukai.framework.spring.integration.http.reader;

/**
 * Created by homolo on 17-6-20.
 */
public interface HttpReaderFactory {

    default String readLine() {
        return readLimitSize(0);
    }

    String readLimitSize(int size);
}
