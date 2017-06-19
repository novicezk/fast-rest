package com.zhukai.framework.spring.integration.config;

import com.zhukai.framework.spring.integration.annotation.core.Configure;

/**
 * Created by zhukai on 17-2-14.
 */
@Configure(prefix = "server")
public class ServerConfig {

    private Long fixedRate = 5L;//扫描间隔(分钟)

    private Long sessionTimeout = 30L;//session过期时间(分钟)

    private Integer port = 8080;

    private Boolean showSQL = false;

    private String fileTmp = "/tmp";

    private Boolean useNio = true;

    public ServerConfig() {
    }

    public Long getFixedRate() {
        return fixedRate;
    }

    public void setFixedRate(Long fixedRate) {
        this.fixedRate = fixedRate;
    }

    public Long getSessionTimeout() {
        return sessionTimeout;
    }

    public void setSessionTimeout(Long sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Boolean isShowSQL() {
        return showSQL;
    }

    public void setShowSQL(Boolean showSQL) {
        this.showSQL = showSQL;
    }

    public String getFileTmp() {
        return fileTmp;
    }

    public void setFileTmp(String fileTmp) {
        this.fileTmp = fileTmp;
    }

    public Boolean isUseNio() {
        return useNio;
    }

    public void setUseNio(Boolean useNio) {
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
