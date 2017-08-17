package com.zhukai.framework.fast.rest;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhukai.framework.fast.rest.annotation.batch.Scheduled;
import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import com.zhukai.framework.fast.rest.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.exception.SetupInitException;
import com.zhukai.framework.fast.rest.http.HttpServletContext;
import com.zhukai.framework.fast.rest.http.Session;
import com.zhukai.framework.fast.rest.server.ServerFactory;

public class FastRestApplication {
	private static final Logger logger = LoggerFactory.getLogger(FastRestApplication.class);
	private static Class runClass;
	private static ServerConfig serverConfig;

	public static void run(Class runClass) {
		FastRestApplication.runClass = runClass;
		try {
			Setup.init();
			serverConfig = ConfigureBeanFactory.getInstance().getBean(ServerConfig.class);
			runSessionTimeoutCheck();
			runBatchSchedule();
			ServerFactory.buildServer(serverConfig).start();
		} catch (SetupInitException e) {
			logger.error("Init error", e);
		} catch (Exception e) {
			logger.error("Start server error", e);
			scheduledExecutor.shutdownNow();
		}
	}

	private static final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(5);

	private static void runSessionTimeoutCheck() {
		Map<String, Session> sessionMap = HttpServletContext.getInstance().getSessions();
		scheduledExecutor.scheduleAtFixedRate(() -> sessionMap.keySet().removeIf(sessionID -> sessionMap.get(sessionID).getLastAccessedTime() + serverConfig.getSessionTimeout() < System.currentTimeMillis()),
				Constants.SESSION_CHECK_FIXED_RATE, Constants.SESSION_CHECK_FIXED_RATE, TimeUnit.MILLISECONDS);
	}

	private static void runBatchSchedule() {
		for (Method method : Setup.getBatchMethods()) {
			Scheduled scheduled = method.getAnnotation(Scheduled.class);
			long fixedRate = scheduled.fixedRate();
			long fixedDelay = scheduled.fixedDelay();
			logger.info("Batch method: {}", method.getName());
			scheduledExecutor.scheduleAtFixedRate(() -> {
				try {
					method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()));
				} catch (Exception e) {
					logger.error("Batch method execute error", e);
				}
			}, fixedDelay, fixedRate, scheduled.timeUnit());
		}
	}

	public static Class getRunClass() {
		return runClass;
	}

	public static ServerConfig getServerConfig() {
		return serverConfig;
	}
}
