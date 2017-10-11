package com.zhukai.framework.fast.rest.factory;

import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.server.HttpServer;
import com.zhukai.framework.fast.rest.server.HttpServerSSL;
import com.zhukai.framework.fast.rest.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerFactory {
	private static final Logger logger = LoggerFactory.getLogger(ServerFactory.class);

	public static Server buildServer(ServerConfig serverConfig) {
		try {
			return serverConfig.isUseSSL() ? new HttpServerSSL(serverConfig) : new HttpServer(serverConfig);
		} catch (Exception e) {
			logger.error("Init server error", e);
			return null;
		}
	}
}
