package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;

public abstract class Server extends Thread {

	public Server(ServerConfig config) throws Exception {
		init(config);
	}

	protected abstract void init(ServerConfig config) throws Exception;

	public abstract String getServerName();

}
