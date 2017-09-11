package com.zhukai.framework.fast.rest.exception;

public class PackageRepeatException extends Exception {

	public PackageRepeatException() {
		super();
	}

	public PackageRepeatException(Throwable cause) {
		super(cause);
	}

	public PackageRepeatException(String message) {
		super(message);
	}

	public PackageRepeatException(String message, Throwable cause) {
		super(message, cause);
	}
}