package com.zhukai.framework.fast.rest.http.request;

import com.zhukai.framework.fast.rest.Constants;
import com.zhukai.framework.fast.rest.common.MultipartFile;
import com.zhukai.framework.fast.rest.http.HttpContext;
import com.zhukai.framework.fast.rest.http.Session;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	private Map<String, Object> attributes;
	private Map<String, String> parameters;
	private Map<String, String> headers;
	private Map<String, Cookie> cookies;
	private Map<String, MultipartFile> multipartFiles;
	private String method;
	private String path;
	private String protocol;
	private String requestContext;


	public HttpRequest() {
		init();
	}

	private void init() {
		attributes = new HashMap<>();
		parameters = new HashMap<>();
		headers = new HashMap<>();
		cookies = new HashMap<>();
		multipartFiles = new HashMap<>();
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public void setPath(String servletPath) {
		this.path = servletPath;
	}

	public String getPath() {
		return path;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getProtocol() {
		return protocol;
	}

	public String getRequestContext() {
		return requestContext;
	}

	public void setRequestContext(String requestContext) {
		this.requestContext = requestContext;
	}

	public void addMultipartFile(MultipartFile multipartFile) {
		multipartFiles.put(multipartFile.getName(), multipartFile);
	}

	public MultipartFile getMultipartFile(String key) {
		return multipartFiles.get(key);
	}

	public MultipartFile[] getAllMultipartFile() {
		MultipartFile[] files = new MultipartFile[0];
		return multipartFiles.values().toArray(files);
	}

	public Cookie[] getCookies() {
		Cookie[] arr = new Cookie[0];
		return cookies.values().toArray(arr);
	}

	public void addCookie(Cookie cookie) {
		cookies.put(cookie.getName(), cookie);
	}

	public String getParameter(String s) {
		return this.parameters.get(s);
	}

	public String[] getParameterValues(String s) {
		String[] arr = new String[0];
		return parameters.values().toArray(arr);
	}

	public Map getParameterMap() {
		return parameters;
	}

	public void putParameter(String s, String o) {
		parameters.put(s, o);
	}


	public Object getAttribute(String s) {
		return attributes.get(s);
	}

	public void setAttribute(String s, Object o) {
		attributes.put(s, o);
	}

	public void removeAttribute(String s) {
		attributes.remove(s);
	}

	public String getHeader(String s) {
		return headers.get(s) == null ? headers.get(s.toLowerCase()) : headers.get(s);
	}

	public void putHeader(String s, String v) {
		headers.put(s, v);
	}

	public Map<String, String> getHeaders() {
		return headers;
	}

	public String getRequestedSessionId() {
		Cookie cookie = cookies.get(Constants.FAST_REST_SESSION);
		if (cookie != null) {
			return cookie.getValue();
		}
		return null;
	}

	public Session getSession() {
		return HttpContext.getSession(getRequestedSessionId());
	}

	public boolean isRequestedSessionIdValid() {
		return HttpContext.getSessions().containsKey(getRequestedSessionId());
	}

	public String getAuthType() {
		return getHeader("Authorization");
	}

	public int getContentLength() {
		String contextLengthStr = getHeader("Content-Length");
		return StringUtils.isBlank(contextLengthStr) ? -1 : Integer.parseInt(contextLengthStr);
	}

	public String getContentType() {
		return getHeader("Content-Type");
	}

}
