package com.zhukai.framework.fast.rest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import com.zhukai.framework.fast.rest.FastRestApplication;

public class Resources {

	public static InputStream getResourceAsStreamByProject(String filePath) {
		return FastRestApplication.getRunClass().getResourceAsStream(filePath);
	}

	public static File getResource(String filePath) throws FileNotFoundException {
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(filePath);
		}
		return file;
	}

	public static InputStream getResourceAsStream(String filePath) throws FileNotFoundException {
		File file = getResource(filePath);
		return new FileInputStream(file);
	}

	public static InputStream getResourceAsStreamByStatic(String filePath) throws FileNotFoundException {
		return getResourceAsStream(FastRestApplication.getStaticPath() + filePath);
	}

	public static File getResourceByStatic(String filePath) throws FileNotFoundException {
		return getResource(FastRestApplication.getStaticPath() + filePath);
	}

	public static InputStream getResourceAsStreamByTmp(String filePath) throws FileNotFoundException {
		return getResourceAsStream(FastRestApplication.getServerConfig().getFileTmp() + filePath);
	}

	public static File getResourceByTmp(String filePath) throws FileNotFoundException {
		return getResource(FastRestApplication.getServerConfig().getFileTmp() + filePath);
	}

	private Resources() {
	}
}
