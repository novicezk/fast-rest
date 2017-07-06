package com.zhukai.framework.spring.integration.exception;

/**
 * Created by homolo on 17-7-5.
 */
public class RequestNotSupportException extends Exception {

    public RequestNotSupportException() {
        super();
    }

    public RequestNotSupportException(Throwable cause) {
        super(cause);
    }

    public RequestNotSupportException(String message) {
        super(message);
    }

    public RequestNotSupportException(String message, Throwable cause) {
        super(message, cause);
    }
}
