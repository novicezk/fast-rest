package com.zhukai.framework.fast.rest.exception;

public class RequestPathNotFoundException extends Exception {

    public RequestPathNotFoundException() {
        super();
    }

    public RequestPathNotFoundException(Throwable cause) {
        super(cause);
    }

    public RequestPathNotFoundException(String message) {
        super(message);
    }

    public RequestPathNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
