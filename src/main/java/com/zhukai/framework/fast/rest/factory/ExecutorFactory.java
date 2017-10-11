package com.zhukai.framework.fast.rest.factory;

import com.zhukai.framework.fast.rest.FastRestApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class ExecutorFactory {
	private static ScheduledThreadPoolExecutor scheduledTaskExecutor;
	private static ExecutorService eventExecutor;
	private static ExecutorService handleExecutor;

	public static ScheduledThreadPoolExecutor getScheduledTaskExecutor() {
		if (scheduledTaskExecutor == null) {
			scheduledTaskExecutor = new ScheduledThreadPoolExecutor(5, new PoolNameThreadFactory("schedule-task"));
		}
		return scheduledTaskExecutor;
	}

	public static ExecutorService getEventExecutor() {
		if (eventExecutor == null) {
			eventExecutor = Executors.newCachedThreadPool(new PoolNameThreadFactory("event"));
		}
		return eventExecutor;
	}

	public static ExecutorService getHandleExecutor() {
		if (handleExecutor == null) {
			handleExecutor = Executors.newCachedThreadPool(new PoolNameThreadFactory(FastRestApplication.getServerConfig().isUseSSL() ? "https" : "http" + "-handle"));
		}
		return handleExecutor;
	}

	public static void clearExecutors() {
		if (scheduledTaskExecutor != null) {
			scheduledTaskExecutor.shutdownNow();
		}
		if (eventExecutor != null) {
			eventExecutor.shutdownNow();
		}
		if (handleExecutor != null) {
			handleExecutor.shutdownNow();
		}
	}

}
