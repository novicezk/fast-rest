package com.zhukai.framework.fast.rest.exception;

public class RequestNotAllowException extends Exception {

	public RequestNotAllowException() {
		super();
	}

	public RequestNotAllowException(Throwable cause) {
		super(cause);
	}

	public RequestNotAllowException(String message) {
		super(message);
	}

	public RequestNotAllowException(String message, Throwable cause) {
		super(message, cause);
	}
}
