package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;

import java.io.IOException;

public abstract class Server {

	public Server(ServerConfig config) throws Exception {
		init(config);
	}

	protected abstract void init(ServerConfig config) throws Exception;

	public abstract String getServerName();


	public abstract void start() throws IOException;

}
