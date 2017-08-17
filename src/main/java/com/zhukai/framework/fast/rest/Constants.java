package com.zhukai.framework.fast.rest;

public interface Constants {
	String DEFAULT_PROPERTIES = "application.properties"; // 默认配置文件名
	String MIMETYPE_PROPERTIES = "mimetype.properties";
	String FAST_REST_SESSION = "FAST_REST_SESSION";
	String HTTP_LINE_SEPARATOR = "\r\n";
	int BUFFER_SIZE = 1024;
	long SESSION_CHECK_FIXED_RATE = 3600000L;// session检测间隔（毫秒）

}
