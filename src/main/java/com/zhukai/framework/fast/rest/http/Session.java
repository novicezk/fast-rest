package com.zhukai.framework.fast.rest.http;

import java.util.*;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

public class Session implements HttpSession {
	private String sessionId;
	private long lastAccessedTime;
	private long creationTime;
	private Map<String, Object> attributes;

	public Session(String sessionId) {
		this.sessionId = sessionId;
		creationTime = System.currentTimeMillis();
		attributes = new HashMap<>();
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}

	@Override
	public String getId() {
		return sessionId;
	}

	@Override
	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
	}

	@Override
	public HttpServletContext getServletContext() {
		return HttpServletContext.getInstance();
	}

	@Override
	public Object getAttribute(String s) {
		return attributes.get(s);
	}

	@Override
	public void setAttribute(String s, Object o) {
		attributes.put(s, o);
	}

	@Override
	public void removeAttribute(String s) {
		attributes.remove(s);
	}

	@Override
	public void invalidate() {
		HttpServletContext.getInstance().getSessions().remove(sessionId);
	}

	@Override
	public Enumeration getAttributeNames() {
		Set<String> names = new HashSet<>();
		names.addAll(this.attributes.keySet());
		return Collections.enumeration(names);
	}

	@Override
	@Deprecated
	public boolean isNew() {
		return false;
	}

	@Override
	@Deprecated
	public void setMaxInactiveInterval(int i) {

	}

	@Override
	@Deprecated
	public int getMaxInactiveInterval() {
		return 0;
	}

	@Override
	@Deprecated
	public Object getValue(String s) {
		return null;
	}

	@Override
	@Deprecated
	public HttpSessionContext getSessionContext() {
		return null;
	}

	@Override
	@Deprecated
	public String[] getValueNames() {
		return new String[0];
	}

	@Override
	@Deprecated
	public void removeValue(String s) {

	}

	@Override
	@Deprecated
	public void putValue(String s, Object o) {

	}
}
