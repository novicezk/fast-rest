package com.zhukai.framework.spring.integration;

import com.zhukai.framework.spring.integration.annotation.batch.Scheduled;
import com.zhukai.framework.spring.integration.bean.component.ComponentBeanFactory;
import com.zhukai.framework.spring.integration.bean.configure.ConfigureBeanFactory;
import com.zhukai.framework.spring.integration.config.ServerConfig;
import com.zhukai.framework.spring.integration.handle.ActionHandle;
import com.zhukai.framework.spring.integration.handle.ActionHandleNIO;
import com.zhukai.framework.spring.integration.http.HttpParser;
import com.zhukai.framework.spring.integration.http.Session;
import com.zhukai.framework.spring.integration.http.request.HttpRequest;
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

    private static final Logger logger = Logger.getLogger(SpringIntegration.class);
    private static Class runClass;
    private static ServerConfig serverConfig;
    private static Selector selector;

    public static void run(Class runClass) {
        SpringIntegration.runClass = runClass;
        try {
            Setup.init();
        } catch (Exception e) {
            logger.error("init error", e);
        }
        serverConfig = ConfigureBeanFactory.getInstance().getBean(ServerConfig.class);
        runSessionTimeoutCheck();
        runBatchSchedule();
        try {
            if (serverConfig.isUseNio()) {
                startNIOServer();
                return;
            }
            startServer();
        } catch (Exception e) {
            logger.error("start server error", e);
        }
    }


    private static final ExecutorService service = Executors.newCachedThreadPool();

    private static void startServer() throws Exception {
        ServerSocket serverSocket = new ServerSocket(serverConfig.getPort());
        logger.info("Server start on port: " + serverConfig.getPort());
        while (true) {
            Socket socket = serverSocket.accept();
            HttpRequest request = HttpParser.createRequest(socket);
            service.execute(new ActionHandle(socket, request));
        }
    }

    private static void startNIOServer() throws Exception {
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
                    HttpRequest request = HttpParser.createRequest(channel);
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

    private static final ScheduledThreadPoolExecutor scheduledExecutor = new ScheduledThreadPoolExecutor(5);

    private static void runSessionTimeoutCheck() {
        scheduledExecutor.scheduleAtFixedRate(() ->
                WebContext.getSessions().keySet().removeIf(sessionID -> {
                    Session session = WebContext.getSessions().get(sessionID);
                    long lastConnectionTime = session.getLastConnectionTime();
                    if (lastConnectionTime + serverConfig.getSessionTimeout() < System.currentTimeMillis()) {
                        logger.warn("sessionID: " + sessionID + "已过期");
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

    public static Selector getSelector() {
        return selector;
    }

}
