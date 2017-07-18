package com.zhukai.framework.spring.integration;

import com.zhukai.framework.spring.integration.annotation.batch.Scheduled;
import com.zhukai.framework.spring.integration.bean.component.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.spring.integration.config.ServerConfig;
import com.zhukai.framework.spring.integration.exception.IntegrationInitException;
import com.zhukai.framework.spring.integration.http.Session;
import com.zhukai.framework.spring.integration.server.WebServer;
import com.zhukai.framework.spring.integration.server.WebServerNIO;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SpringIntegration {
    private static final Logger logger = Logger.getLogger(SpringIntegration.class);
    private static Class runClass;
    private static ServerConfig serverConfig;

    public static void run(Class runClass) {
        SpringIntegration.runClass = runClass;
        try {
            Setup.init();
        } catch (IntegrationInitException e) {
            logger.error("init error", e);
            return;
        }
        serverConfig = ConfigureBeanFactory.getInstance().getBean(ServerConfig.class);
        runSessionTimeoutCheck();
        runBatchSchedule();
        if (serverConfig.isUseNio()) {
            WebServerNIO.start(serverConfig);
        } else {
            WebServer.start(serverConfig);
        }
    }

    private static final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(5);

    private static void runSessionTimeoutCheck() {
        Map<String, Session> sessionMap = HttpServletContext.getInstance().getSessions();
        scheduledExecutor.scheduleAtFixedRate(() ->
                sessionMap.keySet().removeIf(sessionID -> {
                    Session session = sessionMap.get(sessionID);
                    long lastAccessedTime = session.getLastAccessedTime();
                    if (lastAccessedTime + serverConfig.getSessionTimeout() < System.currentTimeMillis()) {
                        logger.warn("sessionID: " + sessionID + " is timeout");
                        return true;
                    }
                    return false;
                }), serverConfig.getFixedRate(), serverConfig.getFixedRate(), TimeUnit.MILLISECONDS);
    }

    private static void runBatchSchedule() {
        for (Method method : Setup.getBatchMethods()) {
            Scheduled scheduled = method.getAnnotation(Scheduled.class);
            long fixedRate = scheduled.fixedRate();
            long fixedDelay = scheduled.fixedDelay();
            fixedDelay = fixedDelay == 0 ? fixedRate : fixedDelay;
            logger.info("Batcher method: " + method.getName());
            scheduledExecutor.scheduleAtFixedRate(() -> {
                try {
                    method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()));
                } catch (Exception e) {
                    logger.error("Batcher method execute error", e);
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
