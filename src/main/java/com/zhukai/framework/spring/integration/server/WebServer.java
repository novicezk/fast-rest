package com.zhukai.framework.spring.integration.server;

import com.zhukai.framework.spring.integration.config.ServerConfig;
import com.zhukai.framework.spring.integration.handle.ActionHandle;
import org.apache.log4j.Logger;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {
    private static final Logger logger = Logger.getLogger(WebServer.class);
    private static final ExecutorService service = Executors.newCachedThreadPool();

    public static void start(ServerConfig serverConfig) {
        try {
            ServerSocket serverSocket = new ServerSocket(serverConfig.getPort());
            logger.info("Server start on port: " + serverConfig.getPort());
            while (true) {
                Socket socket = serverSocket.accept();
                service.execute(new ActionHandle(socket));
            }
        } catch (Exception e) {
            logger.error(e);
        }
    }
}
