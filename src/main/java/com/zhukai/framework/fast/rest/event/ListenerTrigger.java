package com.zhukai.framework.fast.rest.event;

import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ListenerTrigger {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationListener.class);
	private static final Map<String, ApplicationListener> listeners = Collections.synchronizedMap(new HashMap<>());

	public static void registerListener(String eventType, Method listenerMethod) {
		listeners.putIfAbsent(eventType, new ApplicationListener());
		listeners.get(eventType).attach(eventType, listenerMethod);
	}

	public static void touch(ApplicationEvent event) throws InvocationTargetException, IllegalAccessException {
		ApplicationListener listener = listeners.get(event.getEventType());
		if (listener == null) {
			logger.warn("Have no listener which event type is {}", event.getEventType());
			return;
		}
		listener.notifyObserver(event);
	}

	public static void asyncTouch(ApplicationEvent event) {
		ApplicationListener listener = listeners.get(event.getEventType());
		if (listener == null) {
			logger.warn("Have no listener which event type is {}", event.getEventType());
			return;
		}
		new Thread(() -> {
			try {
				listener.notifyObserver(event);
			} catch (Exception e) {
				logger.error("Async exec event:{} error", event.getClass().getName(), e);
				e.printStackTrace();
			}
		}).start();
	}

	private static class ApplicationListener implements EventListener {
		private List<Method> observers = new ArrayList<>();

		private void attach(String eventType, Method observer) {
			if (!observers.contains(observer)) {
				observers.add(observer);
				logger.info("Add event listener, {} : {}", eventType, observer);
			}
		}

		private void notifyObserver(ApplicationEvent event) throws InvocationTargetException, IllegalAccessException {
			for (Method method : observers) {
				method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()), event);
			}
		}

	}
}
