package com.zhukai.framework.fast.rest.event;

import java.util.EventObject;

public class ApplicationEvent extends EventObject {
	private final long timestamp = System.currentTimeMillis();
	private String eventType;


	public ApplicationEvent(String eventType, Object source) {
		super(source);
		this.eventType = eventType;
	}

	public String getEventType() {
		return eventType;
	}

	public final long getTimestamp() {
		return this.timestamp;
	}
}
