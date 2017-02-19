package com.zhukai.spring.integration.server;

/**
 * Created by zhukai on 17-2-14.
 */
public class ServerConfig {

    private long fixedRate = 300000;//5分钟,扫描间隔

    private long sessionTimeout = 1800000;//默认30分钟，session过期时间

    private int port = 8080;

    private boolean showSQL = false;

    private String fileTmp = "/tmp";

    private boolean useNio = true;

    public ServerConfig() {
    }

    public long getFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(long fixedRate) {
        this.fixedRate = fixedRate;
    }

    public long getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
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

    public String getFileTmp() {
        return fileTmp;
    }

    public void setFileTmp(String fileTmp) {
        this.fileTmp = fileTmp;
    }

    public boolean isUseNio() {
        return useNio;
    }

    public void setUseNio(boolean useNio) {
        this.useNio = useNio;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "fixedRate=" + fixedRate +
                ", sessionTimeout=" + sessionTimeout +
                ", port=" + port +
                ", showSQL=" + showSQL +
                ", fileTmp='" + fileTmp + '\'' +
                ", useNio=" + useNio +
                '}';
    }
}
