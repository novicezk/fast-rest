package com.zhukai.framework.fast.rest.exception;

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
