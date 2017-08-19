package com.zhukai.framework.fast.rest.http.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class AbstractHttpReader {
	private static final String DEFAULT_PARSE_CHARSET = "utf-8";

	public String readLine() throws IOException {
		byte[] bytes = readByteArrayLimitSize(0);
		int length = bytes.length > 0 && bytes[bytes.length - 1] == 13 ? bytes.length - 1 : bytes.length;
		return new String(bytes, 0, length, DEFAULT_PARSE_CHARSET);
	}

	public String readLimitSize(int size) throws IOException {
		byte[] bytes = readByteArrayLimitSize(size);
		return new String(bytes, DEFAULT_PARSE_CHARSET);
	}

	public InputStream readFileInputStream(int size) throws IOException {
		byte[] bytes = readByteArrayLimitSize(size);
		return new ByteArrayInputStream(bytes);
	}

	protected abstract byte[] readByteArrayLimitSize(int size) throws IOException;
}
