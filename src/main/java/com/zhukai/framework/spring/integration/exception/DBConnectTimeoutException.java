package com.zhukai.framework.spring.integration.exception;

/**
 * Created by homolo on 17-7-5.
 */
public class DBConnectTimeoutException extends Exception {

    public DBConnectTimeoutException() {
        super();
    }

    public DBConnectTimeoutException(Throwable cause) {
        super(cause);
    }

    public DBConnectTimeoutException(String message) {
        super(message);
    }

    public DBConnectTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
