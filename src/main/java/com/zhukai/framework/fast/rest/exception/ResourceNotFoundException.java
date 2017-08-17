package com.zhukai.framework.fast.rest.exception;

public class ResourceNotFoundException extends Exception {

	public ResourceNotFoundException() {
		super();
	}

	public ResourceNotFoundException(Throwable cause) {
		super(cause);
	}

	public ResourceNotFoundException(String message) {
		super(message);
	}

	public ResourceNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
