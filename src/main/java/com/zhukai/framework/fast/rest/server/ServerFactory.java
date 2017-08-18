package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;

public class ServerFactory {

	public static Server buildServer(ServerConfig serverConfig) throws Exception {
		return serverConfig.isUseSSL() ? new HttpServerSSL(serverConfig) : new HttpServer(serverConfig);
	}
}
