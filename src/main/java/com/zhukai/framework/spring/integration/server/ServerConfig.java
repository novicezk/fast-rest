package com.zhukai.framework.spring.integration.server;

/**
 * Created by zhukai on 17-2-14.
 */
public class ServerConfig {

    private long fixedRate = 5;//扫描间隔(分钟)

    private long sessionTimeout = 30;//session过期时间(分钟)

    private int port = 8080;

    private boolean showSQL = false;

    private String fileTmp = "/home/zhukai/tmp";

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
