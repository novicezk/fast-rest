package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;
import com.zhukai.framework.fast.rest.log.Log;
import com.zhukai.framework.fast.rest.log.LogFactory;

/**
 * Created by homolo on 17-8-3.
 */
public abstract class Server {
    private static final Log logger = LogFactory.getLog(Server.class);

    public Server(ServerConfig config) {
        try {
            init(config);
            logger.info("Create {} server success, port: {}", getName(), config.getPort());
        } catch (Exception e) {
            logger.error("Create server error", e);
        }
    }

    protected abstract void init(ServerConfig config) throws Exception;

    public abstract void start() throws Exception;

    protected abstract String getName();

}
