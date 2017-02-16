package com.zhukai.spring.integration.server;


import com.zhukai.spring.integration.annotation.batch.Scheduled;
import com.zhukai.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.spring.integration.client.ActionHandle;
import com.zhukai.spring.integration.common.Request;
import com.zhukai.spring.integration.common.RequestBuilder;
import com.zhukai.spring.integration.common.Session;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.logger.Logger;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhukai on 17-1-12.
 */
public class SpringIntegration {

    private static ServerConfig serverConfig;

    public static List<Method> batchMethods = new ArrayList<>();

    public static Class runClass;

    private static Timer timer = new Timer();
    private static ExecutorService service = Executors.newCachedThreadPool();

    private static ServerSocketChannel serverChannel;
    public static Selector selector;
    public static String CHARSET = "utf-8"; //默认编码
    public static int BUFFER_SIZE = 10;

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
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(serverConfig.getPort()));
        Logger.info("Server start on port: " + serverConfig.getPort());
        serverChannel.configureBlocking(false);
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            int readyChannels = selector.selectNow();
            if (readyChannels == 0)
                continue;
            Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = ite.next();
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key
                            .channel();
                    SocketChannel channel = server.accept();
                    if (channel != null) {
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                } else if (key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    Request request = RequestBuilder.build(channel);
                    if (request != null) {
                        service.execute(new ActionHandle(channel, request));
                    } else {
                        channel.shutdownInput();
                        channel.close();
                    }
                } else if (key.isWritable()) {
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    socketChannel.shutdownInput();
                    socketChannel.close();
                }
                ite.remove();
            }
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
                        method.invoke(ComponentBeanFactory.getInstance().getBean(method.getDeclaringClass()), new Object[]{});
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
