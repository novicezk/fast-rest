package com.zhukai.framework.fast.rest.event;

import com.zhukai.framework.fast.rest.annotation.extend.EventHandle;
import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ListenerTrigger {
	private static final Logger logger = LoggerFactory.getLogger(ApplicationListener.class);
	private static final Map<String, ApplicationListener> listeners = Collections.synchronizedMap(new HashMap<>());
	private final static ExecutorService service = Executors.newCachedThreadPool();

	public static void registerListener(String eventType, Method listenerMethod) {
		listeners.putIfAbsent(eventType, new ApplicationListener());
		listeners.get(eventType).attach(eventType, listenerMethod);
	}

	public static List<Future> publishEvent(ApplicationEvent event) {
		ApplicationListener listener = listeners.get(event.getEventType());
		if (listener == null) {
			logger.warn("Have no listener which event type is {}", event.getEventType());
			return Collections.emptyList();
		}
		return listener.asyncNotify(event);
	}

	public static List<Object> publishEventSync(ApplicationEvent event) throws Throwable {
		ApplicationListener listener = listeners.get(event.getEventType());
		if (listener == null) {
			logger.warn("Have no listener which event type is {}", event.getEventType());
			return Collections.emptyList();
		}
		return listener.syncNotify(event);
	}

	private static class ApplicationListener implements EventListener {
		private static final Comparator<Method> comparator = (method1, method2) -> {
			int method1Seq = method1.getAnnotation(EventHandle.class).seq();
			int method2Seq = method2.getAnnotation(EventHandle.class).seq();
			if (method1Seq == method2Seq)
				return 0;
			return method1Seq > method2Seq ? 1 : -1;
		};
		private List<Method> observers = Collections.synchronizedList(new ArrayList<>());

		private void attach(String eventType, Method observer) {
			if (!observers.contains(observer)) {
				observers.add(observer);
				logger.info("Add event listener, {} : {}", eventType, observer);
				observers.sort(comparator);
			}
		}

		private List<Future> asyncNotify(ApplicationEvent event) {
			List<Future> futures = new ArrayList<>();
			observers.forEach(method ->
					futures.add(service.submit(() ->
							method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()), event))));
			return futures;
		}

		private List<Object> syncNotify(ApplicationEvent event) throws Throwable {
			try {
				List<Object> results = new ArrayList<>();
				for (Method method : observers) {
					results.add(method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()), event));
				}
				return results;
			} catch (InvocationTargetException ite) {
				throw ite.getTargetException();
			}
		}
	}

}
