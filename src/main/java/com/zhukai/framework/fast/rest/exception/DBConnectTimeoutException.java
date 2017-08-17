package com.zhukai.framework.fast.rest.exception;

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
