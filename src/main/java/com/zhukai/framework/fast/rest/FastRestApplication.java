package com.zhukai.framework.fast.rest;

import com.zhukai.framework.fast.rest.annotation.extend.EnableStaticServer;
import com.zhukai.framework.fast.rest.annotation.extend.Scheduled;
import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import com.zhukai.framework.fast.rest.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.fast.rest.common.FastRestThreadFactory;
import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.exception.SetupInitException;
import com.zhukai.framework.fast.rest.http.HttpContext;
import com.zhukai.framework.fast.rest.http.Session;
import com.zhukai.framework.fast.rest.server.Server;
import com.zhukai.framework.fast.rest.server.ServerFactory;
import com.zhukai.framework.fast.rest.util.ReflectUtil;
import com.zhukai.framework.fast.rest.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class FastRestApplication {
	private static Logger logger;
	private static Class runClass;
	private static ServerConfig serverConfig;
	private static String staticPath;

	public static void run(Class runClass) {
		FastRestApplication.runClass = runClass;
		try {
			initLogger();
			Setup.init();
			serverConfig = ConfigureBeanFactory.getInstance().getBean(ServerConfig.class);
			Server server = ServerFactory.buildServer(serverConfig);
			server.start();
			logger.info("Start {} server success, port: {}", server.getServerName(), serverConfig.getPort());
			checkStaticServer();
			runSessionTimeoutCheck();
			runBatchSchedule();
		} catch (SetupInitException e) {
			logger.error("Init error", e.getCause());
		} catch (Exception e) {
			logger.error("Start server error", e);
			System.exit(1);
		}
	}

	private static void initLogger() {
		if (Resources.getResourceAsStreamByProject("/log4j.properties") == null) {
			System.setProperty("log4j.configuration", "default/log4j.properties");
		}
		logger = LoggerFactory.getLogger(FastRestApplication.class);
	}

	private static void checkStaticServer() throws UnknownHostException {
		if (ReflectUtil.existAnnotation(runClass, EnableStaticServer.class)) {
			staticPath = EnableStaticServer.class.cast(runClass.getAnnotation(EnableStaticServer.class)).value();
			if (!staticPath.endsWith("/")) {
				staticPath += "/";
			}
			logger.info("Start static server, you can visit {}://{}:{}/static/{fileName} to view the file in directory {}", serverConfig.isUseSSL() ? "https" : "http", InetAddress.getLocalHost().getHostAddress(), serverConfig.getPort(),
					staticPath);
		}
	}

	private static final ScheduledThreadPoolExecutor scheduledTaskExecutor = new ScheduledThreadPoolExecutor(5, new FastRestThreadFactory("schedule-task"));

	private static void runSessionTimeoutCheck() {
		scheduledTaskExecutor.scheduleAtFixedRate(() -> {
			Map<String, Session> sessionMap = HttpContext.getSessions();
			HttpContext.getSessions().keySet().removeIf(sessionID -> sessionMap.get(sessionID).getLastAccessedTime() + serverConfig.getSessionTimeout() < System.currentTimeMillis());
		}, Constants.SESSION_CHECK_FIXED_RATE, Constants.SESSION_CHECK_FIXED_RATE, TimeUnit.MILLISECONDS);
	}

	private static void runBatchSchedule() {
		for (Method method : Setup.getBatchMethods()) {
			Scheduled scheduled = method.getAnnotation(Scheduled.class);
			long fixedRate = scheduled.fixedRate();
			long fixedDelay = scheduled.fixedDelay();
			logger.info("Batch method: {}", method.getName());
			scheduledTaskExecutor.scheduleAtFixedRate(() -> {
				try {
					method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()));
				} catch (Exception e) {
					logger.error("Batch method execute error");
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

	public static String getStaticPath() {
		return staticPath;
	}
}
