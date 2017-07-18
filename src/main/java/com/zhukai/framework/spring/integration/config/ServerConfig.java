package com.zhukai.framework.spring.integration.config;

import com.zhukai.framework.spring.integration.annotation.core.Configure;

@Configure(prefix = "server")
public class ServerConfig {

    private Long fixedRate = 300000L;
    private Long sessionTimeout = 1800000L;
    private Integer port = 8080;
    private Boolean showSQL = false;
    private String fileTmp = "/tmp/";
    private Boolean useNio = true;
    private String charset = "utf-8";

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

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
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
                ", charset='" + charset + '\'' +
                '}';
    }
}
