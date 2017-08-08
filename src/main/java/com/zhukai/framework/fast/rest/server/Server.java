package com.zhukai.framework.fast.rest.server;

import com.zhukai.framework.fast.rest.config.ServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by homolo on 17-8-3.
 */
public abstract class Server {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

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
