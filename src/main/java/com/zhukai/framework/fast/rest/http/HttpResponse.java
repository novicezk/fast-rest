package com.zhukai.framework.fast.rest.http;

import com.zhukai.framework.fast.rest.FastRestApplication;
import com.zhukai.framework.fast.rest.common.HttpHeaderType;
import com.zhukai.framework.fast.rest.common.HttpStatus;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

public class HttpResponse implements HttpServletResponse {
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

	@Override
	public void addCookie(Cookie cookie) {
		this.cookies.add(cookie);
	}

	@Override
	public boolean containsHeader(String key) {
		return headers.containsKey(key);
	}

	@Override
	public String encodeURL(String url) {
		try {
			return URLEncoder.encode(url, charset);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public String encodeRedirectURL(String url) {
		try {
			return URLEncoder.encode(url, charset);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}

	@Override
	public void sendError(int code, String msg) throws IOException {
		statusCode = code;
		statusCodeStr = msg;
	}

	@Override
	public void sendError(int code) throws IOException {
		statusCode = code;
	}

	@Override
	public void setDateHeader(String s, long l) {
		headers.put(s, String.valueOf(l));
	}

	@Override
	public void addDateHeader(String s, long l) {
		headers.put(s, String.valueOf(l));
	}

	@Override
	public void setHeader(String s, String v) {
		headers.put(s, v);
	}

	@Override
	public void addHeader(String s, String v) {
		headers.put(s, v);
	}

	@Override
	public void setIntHeader(String s, int i) {
		headers.put(s, String.valueOf(i));
	}

	public String getHeader(String s) {
		return headers.get(s);
	}

	@Override
	public void addIntHeader(String s, int i) {
		headers.put(s, String.valueOf(i));
	}

	@Override
	public void setStatus(int code) {
		statusCode = code;
	}

	@Override
	public void setStatus(int code, String msg) {
		statusCode = code;
		statusCodeStr = msg;
	}

	@Override
	public String getCharacterEncoding() {
		return charset;
	}

	@Override
	public String getContentType() {
		return headers.get(HttpHeaderType.CONTENT_TYPE);
	}

	@Override
	public void setCharacterEncoding(String s) {
		charset = s;
	}

	@Override
	public void setContentLength(int length) {
		setIntHeader(HttpHeaderType.CONTENT_LENGTH, length);
	}

	@Override
	public void setContentType(String contentType) {
		headers.put(HttpHeaderType.CONTENT_TYPE, contentType);
	}

	@Override
	@Deprecated
	public void sendRedirect(String s) throws IOException {

	}

	@Override
	@Deprecated
	public void setLocale(Locale locale) {

	}

	@Override
	@Deprecated
	public Locale getLocale() {
		return null;
	}

	@Override
	@Deprecated
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	@Override
	@Deprecated
	public PrintWriter getWriter() throws IOException {
		return null;
	}

	@Override
	@Deprecated
	public void setBufferSize(int i) {

	}

	@Override
	@Deprecated
	public int getBufferSize() {
		return 0;
	}

	@Override
	@Deprecated
	public void flushBuffer() throws IOException {

	}

	@Override
	@Deprecated
	public void resetBuffer() {

	}

	@Override
	@Deprecated
	public boolean isCommitted() {
		return false;
	}

	@Override
	@Deprecated
	public void reset() {

	}

	@Override
	@Deprecated
	public String encodeUrl(String s) {
		return null;
	}

	@Override
	@Deprecated
	public String encodeRedirectUrl(String s) {
		return null;
	}
}
