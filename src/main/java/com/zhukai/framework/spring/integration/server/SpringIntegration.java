package com.zhukai.framework.spring.integration.server;


import com.zhukai.framework.spring.integration.annotation.batch.Scheduled;
import com.zhukai.framework.spring.integration.beans.impl.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.client.ActionHandle;
import com.zhukai.framework.spring.integration.client.ActionHandleNIO;
import com.zhukai.framework.spring.integration.common.HttpParser;
import com.zhukai.framework.spring.integration.common.HttpRequest;
import com.zhukai.framework.spring.integration.common.Session;
import com.zhukai.framework.spring.integration.context.WebContext;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhukai on 17-1-12.
 */
public class SpringIntegration {
    public static String CHARSET = "utf-8"; //默认编码
    public static int BUFFER_SIZE = 1024;
    public static Class runClass;

    private static ServerConfig serverConfig;

    private static Logger logger = Logger.getLogger(SpringIntegration.class);

    public static void run(Class runClass) {
        try {
            SpringIntegration.runClass = runClass;
            SpringCore.init();
            runSessionTimeoutCheck();
            runBatchSchedule();
            if (serverConfig.isUseNio()) {
                startServerNIO();
            } else {
                startServer();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static ExecutorService service = Executors.newCachedThreadPool();

    private static void startServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(serverConfig.getPort());
        logger.info("Server start on port: " + serverConfig.getPort());
        while (true) {
            Socket client = serverSocket.accept();
            service.execute(new ActionHandle(client));
        }
    }

    public static Selector selector;

    private static void startServerNIO() throws Exception {
        ServerSocketChannel serverChannel;
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.socket().bind(new InetSocketAddress(serverConfig.getPort()));
        logger.info("Server start on port: " + serverConfig.getPort() + " with nio");
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
                    HttpRequest request = HttpParser.parseRequest(channel);
                    if (request != null) {
                        service.execute(new ActionHandleNIO(channel, request));
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

    private static ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(5);

    private static void runSessionTimeoutCheck() {
        scheduledExecutor.scheduleAtFixedRate(() ->
                WebContext.getSessions().keySet().removeIf(sessionID -> {
                    Session session = WebContext.getSessions().get(sessionID);
                    long lastConnectionTime = session.getLastConnectionTime();
                    if (lastConnectionTime + TimeUnit.MINUTES.toMillis(serverConfig.getSessionTimeout()) < System.currentTimeMillis()) {
                        logger.warn("sessionID: " + sessionID + "已过期");
                        return true;
                    }
                    return false;
                }), serverConfig.getFixedRate(), serverConfig.getFixedRate(), TimeUnit.MINUTES);
    }

    private static void runBatchSchedule() {
        for (Method method : SpringCore.getBatchMethods()) {
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

    public static ServerConfig getServerConfig() {
        return serverConfig;
    }

    public static void setServerConfig(ServerConfig serverConfig) {
        SpringIntegration.serverConfig = serverConfig;
    }
}
