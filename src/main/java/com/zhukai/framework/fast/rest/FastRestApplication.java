package com.zhukai.framework.fast.rest;

import com.zhukai.framework.fast.rest.annotation.extend.EnableStaticServer;
import com.zhukai.framework.fast.rest.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.exception.SetupInitException;
import com.zhukai.framework.fast.rest.factory.ExecutorFactory;
import com.zhukai.framework.fast.rest.factory.ServerFactory;
import com.zhukai.framework.fast.rest.http.HttpContext;
import com.zhukai.framework.fast.rest.http.Session;
import com.zhukai.framework.fast.rest.server.Server;
import com.zhukai.framework.fast.rest.util.ReflectUtil;
import com.zhukai.framework.fast.rest.util.Resources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.Map;
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
			if (server != null) {
				checkStaticServer();
				runSessionTimeoutCheck();
				logger.info("Start {} server success, port: {}", server.getServerName(), serverConfig.getPort());
				server.start();
			}
		} catch (SetupInitException e) {
			logger.error("Setup init error", e.getCause());
		} catch (Exception e) {
			ExecutorFactory.clearExecutors();
			logger.error("Run server error", e);
		}
	}

	private static void initLogger() {
		try {
			Resources.getResourceAsStreamByProject("/log4j.properties");
		} catch (FileNotFoundException e) {
			System.setProperty("log4j.configuration", "default/log4j.properties");
		}
		logger = LoggerFactory.getLogger(FastRestApplication.class);
	}

	private static void checkStaticServer() {
		if (ReflectUtil.existAnnotation(runClass, EnableStaticServer.class)) {
			staticPath = EnableStaticServer.class.cast(runClass.getAnnotation(EnableStaticServer.class)).value();
			if (!staticPath.endsWith("/")) {
				staticPath += "/";
			}
			logger.info("Static file server started, you can visit {}://localhost:{}/static/{fileName} to view the file in directory {}", serverConfig.isUseSSL() ? "https" : "http", serverConfig.getPort(),
					staticPath);
		}
	}

	private static void runSessionTimeoutCheck() {
		ExecutorFactory.getScheduledTaskExecutor().scheduleAtFixedRate(() -> {
			Map<String, Session> sessionMap = HttpContext.getSessions();
			HttpContext.getSessions().keySet().removeIf(sessionID -> sessionMap.get(sessionID).getLastAccessedTime() + serverConfig.getSessionTimeout() < System.currentTimeMillis());
		}, Constants.SESSION_CHECK_FIXED_RATE, Constants.SESSION_CHECK_FIXED_RATE, TimeUnit.MILLISECONDS);
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
