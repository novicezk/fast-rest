package com.zhukai.framework.fast.rest.http;

import java.util.HashMap;
import java.util.Map;

public class Session {
	private String sessionId;
	private long lastAccessedTime;
	private long creationTime;
	private Map<String, Object> attributes;

	public Session(String sessionId) {
		this.sessionId = sessionId;
		creationTime = System.currentTimeMillis();
		attributes = new HashMap<>();
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getId() {
		return sessionId;
	}

	public long getLastAccessedTime() {
		return lastAccessedTime;
	}

	public void setLastAccessedTime(long lastAccessedTime) {
		this.lastAccessedTime = lastAccessedTime;
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

	public void invalidate() {
		HttpContext.getSessions().remove(sessionId);
	}
}
