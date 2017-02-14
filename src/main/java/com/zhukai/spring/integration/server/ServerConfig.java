package com.zhukai.spring.integration.server;

/**
 * Created by zhukai on 17-2-14.
 */
public class ServerConfig {

    private long fixedRate = 300000;//5分钟,扫描间隔

    private long timeout = 1800000;//默认30分钟，session过期时间

    private int port = 8080;

    private boolean showSQL = false;

    public ServerConfig() {
    }

    public long getFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(long fixedRate) {
        this.fixedRate = fixedRate;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isShowSQL() {
        return showSQL;
    }

    public void setShowSQL(boolean showSQL) {
        this.showSQL = showSQL;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "fixedRate=" + fixedRate +
                ", timeout=" + timeout +
                ", port=" + port +
                ", showSQL=" + showSQL +
                '}';
    }
}
