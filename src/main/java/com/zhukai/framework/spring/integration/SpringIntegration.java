package com.zhukai.framework.spring.integration;

import com.zhukai.framework.spring.integration.annotation.batch.Scheduled;
import com.zhukai.framework.spring.integration.bean.component.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.spring.integration.config.ServerConfig;
import com.zhukai.framework.spring.integration.http.Session;
import com.zhukai.framework.spring.integration.server.WebServer;
import com.zhukai.framework.spring.integration.server.WebServerNIO;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhukai on 17-1-12.
 */
public class SpringIntegration {

    private static final Logger logger = Logger.getLogger(SpringIntegration.class);
    private static Class runClass;
    private static ServerConfig serverConfig;

    public static void run(Class runClass) {
        SpringIntegration.runClass = runClass;
        Setup.init();
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
        scheduledExecutor.scheduleAtFixedRate(() ->
                WebContext.getSessions().keySet().removeIf(sessionID -> {
                    Session session = WebContext.getSessions().get(sessionID);
                    long lastConnectionTime = session.getLastConnectionTime();
                    if (lastConnectionTime + serverConfig.getSessionTimeout() < System.currentTimeMillis()) {
                        logger.warn("sessionID: " + sessionID + " is timeout");
                        return true;
                    }
                    return false;
                }), serverConfig.getFixedRate(), serverConfig.getFixedRate(), TimeUnit.MILLISECONDS);
    }

    private static void runBatchSchedule() {
        for (Method method : Setup.getBatchMethods()) {
            long fixedRate = method.getAnnotation(Scheduled.class).fixedRate();
            long fixedDelay = method.getAnnotation(Scheduled.class).fixedDelay();
            fixedDelay = fixedDelay == 0 ? fixedRate : fixedDelay;
            logger.info("Batch method: " + method.getName());
            scheduledExecutor.scheduleAtFixedRate(() -> {
                try {
                    method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()), new Object[]{});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, fixedDelay, fixedRate, method.getAnnotation(Scheduled.class).timeUnit());
        }
    }

    public static Class getRunClass() {
        return runClass;
    }

}
