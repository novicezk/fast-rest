package com.zhukai.framework.spring.integration.exception;

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
