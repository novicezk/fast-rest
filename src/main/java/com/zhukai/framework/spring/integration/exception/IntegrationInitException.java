package com.zhukai.framework.spring.integration.exception;

public class IntegrationInitException extends Exception {

    public IntegrationInitException() {
        super();
    }

    public IntegrationInitException(Throwable cause) {
        super(cause);
    }

    public IntegrationInitException(String message) {
        super(message);
    }

    public IntegrationInitException(String message, Throwable cause) {
        super(message, cause);
    }
}
