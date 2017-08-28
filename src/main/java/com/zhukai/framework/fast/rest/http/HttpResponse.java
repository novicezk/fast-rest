package com.zhukai.framework.fast.rest.http;

import com.zhukai.framework.fast.rest.FastRestApplication;
import com.zhukai.framework.fast.rest.common.HttpHeaderType;
import com.zhukai.framework.fast.rest.common.HttpStatus;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpResponse {
	private List<Cookie> cookies;
	private Map<String, String> headers;
	private String charset;
	private int statusCode;
	private String statusCodeStr;
	private String protocol;
	private Object result;

	public HttpResponse() {
		charset = FastRestApplication.getServerConfig().getCharset();
		cookies = new ArrayList<>();
		headers = new HashMap<>();
		statusCode = 200;
		statusCodeStr = "OK";
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public String getStatusCodeStr() {
		return statusCodeStr;
	}

	public void setStatus(HttpStatus status) {
		statusCode = status.getCode();
		statusCodeStr = status.getCodeStr();
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public List<Cookie> getCookies() {
		return cookies;
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public void addCookie(Cookie cookie) {
		this.cookies.add(cookie);
	}

	public boolean containsHeader(String key) {
		return headers.containsKey(key);
	}

	public void addHeader(String s, String v) {
		headers.put(s, v);
	}


	public String getHeader(String s) {
		return headers.get(s);
	}

	public void setStatus(int code, String msg) {
		statusCode = code;
		statusCodeStr = msg;
	}

	public String getCharacterEncoding() {
		return charset;
	}

	public String getContentType() {
		return headers.get(HttpHeaderType.CONTENT_TYPE);
	}

	public void setCharacterEncoding(String s) {
		charset = s;
	}

	public void setContentLength(int length) {
		headers.put(HttpHeaderType.CONTENT_LENGTH, String.valueOf(length));
	}

	public void setContentType(String contentType) {
		headers.put(HttpHeaderType.CONTENT_TYPE, contentType);
	}

}
