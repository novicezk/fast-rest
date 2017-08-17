package com.zhukai.framework.fast.rest.http.reader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhukai.framework.fast.rest.exception.HttpReadException;

public abstract class AbstractHttpReader {
	private static final String DEFAULT_PARSE_CHARSET = "utf-8";
	private static final Logger logger = LoggerFactory.getLogger(AbstractHttpReader.class);

	public String readLine() throws HttpReadException {
		try {
			byte[] bytes = readByteArrayLimitSize(0);
			int length = bytes.length > 0 && bytes[bytes.length - 1] == 13 ? bytes.length - 1 : bytes.length;
			String line = new String(bytes, 0, length, DEFAULT_PARSE_CHARSET);
			logger.debug(line);
			return line;
		} catch (IOException e) {
			throw new HttpReadException(e);
		}
	}

	public String readLimitSize(int size) throws HttpReadException {
		try {
			byte[] bytes = readByteArrayLimitSize(size);
			return new String(bytes, DEFAULT_PARSE_CHARSET);
		} catch (IOException e) {
			throw new HttpReadException(e);
		}
	}

	public InputStream readFileInputStream(int size) throws HttpReadException {
		try {
			byte[] bytes = readByteArrayLimitSize(size);
			return new ByteArrayInputStream(bytes);
		} catch (IOException e) {
			throw new HttpReadException(e);
		}
	}

	protected abstract byte[] readByteArrayLimitSize(int size) throws IOException;
}
