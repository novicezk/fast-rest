package com.zhukai.framework.spring.integration.exception;

/**
 * Created by homolo on 17-7-5.
 */
public class HttpReadException extends Exception {

    public HttpReadException() {
        super();
    }

    public HttpReadException(Throwable cause) {
        super(cause);
    }

    public HttpReadException(String message) {
        super(message);
    }

    public HttpReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
