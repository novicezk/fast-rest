package com.zhukai.framework.fast.rest.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.handle.ActionHandle;
import com.zhukai.framework.fast.rest.util.Resources;

public class HttpServerSSL extends Server {
	private static final Logger logger = LoggerFactory.getLogger(HttpServerSSL.class);
	private final ExecutorService service = Executors.newCachedThreadPool();
	private ServerSocket serverSocket;

	public HttpServerSSL(ServerConfig config) throws Exception {
		super(config);
	}

	@Override
	public String getServerName() {
		return "Https";
	}

	@Override
	protected void init(ServerConfig config) throws Exception {
		SSLContext sslContext = getSSLContext(Resources.getResourceAsStreamByProject("/" + config.getKeyStoreFile()), config.getKeyStorePassword());
		ServerSocketFactory factory = sslContext.getServerSocketFactory();
		serverSocket = factory.createServerSocket(config.getPort());
		SSLServerSocket.class.cast(serverSocket).setNeedClientAuth(config.isNeedClientAuth());
	}

	@Override
	public void run() {
		try {
			while (true) {
				Socket socket = serverSocket.accept();
				service.execute(new ActionHandle(socket));
			}
		} catch (Exception e) {
			logger.error("Https server run error", e);
			System.exit(1);
		}
	}

	private SSLContext getSSLContext(InputStream inputStream, String password) throws Exception {
		char[] passphrase = password == null ? null : password.toCharArray();
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(inputStream, passphrase);
		SSLContext sslContext = SSLContext.getInstance("TLS");
		KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
		kmf.init(ks, passphrase);
		sslContext.init(kmf.getKeyManagers(), null, null);
		return sslContext;
	}
}
