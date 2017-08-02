package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.handle.ActionHandle;
import com.zhukai.framework.fast.rest.util.Resources;
import org.apache.log4j.Logger;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SSLServer {
    private static final Logger logger = Logger.getLogger(SSLServer.class);
    private static final ExecutorService service = Executors.newCachedThreadPool();

    public static void start(ServerConfig config) throws Exception {
        SSLContext sslContext = SSLFactory.getSSLContext(Resources.getResourceAsStream("/" + config.getKeyStoreFile()), config.getKeyStorePassword());
        ServerSocketFactory factory = sslContext.getServerSocketFactory();
        ServerSocket serverSocket = factory.createServerSocket(config.getPort());
        logger.info("Https server start on port: " + config.getPort());
        SSLServerSocket.class.cast(serverSocket).setNeedClientAuth(config.isNeedClientAuth());
        while (true) {
            Socket socket = serverSocket.accept();
            service.execute(new ActionHandle(socket));
        }
    }
}
