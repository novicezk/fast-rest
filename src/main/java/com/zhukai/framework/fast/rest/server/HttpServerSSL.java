package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.factory.ExecutorFactory;
import com.zhukai.framework.fast.rest.handle.ActionHandle;
import com.zhukai.framework.fast.rest.util.Resources;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public class HttpServerSSL extends Server {
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
	public void start() throws IOException {
		while (true) {
			Socket socket = serverSocket.accept();
			ExecutorFactory.getHandleExecutor().execute(new ActionHandle(socket));
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
