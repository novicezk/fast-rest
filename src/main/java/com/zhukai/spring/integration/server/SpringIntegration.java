package com.zhukai.spring.integration.server;


import com.zhukai.spring.integration.annotation.batch.Scheduled;
import com.zhukai.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.spring.integration.client.ClientAction;
import com.zhukai.spring.integration.common.Session;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.logger.Logger;

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhukai on 17-1-12.
 */
public class SpringIntegration {

    private static ServerSocket serverSocket;

    private static ServerConfig serverConfig;

    public static List<Method> batchMethods = new ArrayList<>();

    public static Class runClass;

    private static Timer timer = new Timer();

    public static void run(Class runClass) {
        try {
            SpringIntegration.runClass = runClass;
            SpringCore.init();
            runSessionTimeoutCheck();
            runBatchSchedule();
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void startServer() throws Exception {
        serverSocket = new ServerSocket(serverConfig.getPort());
        Logger.info("Application is start on " + serverConfig.getPort());
        ExecutorService service = Executors.newCachedThreadPool();
        while (true) {
            Socket client = serverSocket.accept();
            service.execute(new ClientAction(client));
        }
    }

    private static void runSessionTimeoutCheck() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                WebContext.getSessions().keySet().removeIf(sessionID -> {
                    Session session = WebContext.getSessions().get(sessionID);
                    LocalDateTime lastConnectionTime = session.getLastConnectionTime();
                    if (lastConnectionTime.plusSeconds(serverConfig.getTimeout() / 1000).isBefore(LocalDateTime.now())) {
                        System.out.println("sessionID: " + sessionID + "已过期");
                        return true;
                    }
                    return false;
                });
            }
        }, serverConfig.getFixedRate(), serverConfig.getFixedRate());
    }

    private static void runBatchSchedule() {
        for (Method method : batchMethods) {
            long fixedRate = method.getAnnotation(Scheduled.class).fixedRate();
            long fixedDelay = method.getAnnotation(Scheduled.class).fixedDelay();
            fixedDelay = fixedDelay == 0 ? fixedRate : fixedDelay;
            Logger.info("Batch method: " + method.getName());
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()), null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, fixedDelay, fixedRate);
        }
    }

    public static ServerConfig getServerConfig() {
        return serverConfig;
    }

    public static void setServerConfig(ServerConfig serverConfig) {
        SpringIntegration.serverConfig = serverConfig;
    }
}
