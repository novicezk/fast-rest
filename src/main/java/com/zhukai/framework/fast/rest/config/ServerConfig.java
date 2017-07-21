package com.zhukai.framework.fast.rest.config;

import com.zhukai.framework.fast.rest.annotation.core.Configure;

@Configure(prefix = "server")
public class ServerConfig {

    private Long fixedRate = 300000L;
    private Long sessionTimeout = 1800000L;
    private Integer port = 8080;
    private Boolean showSQL = false;
    private String fileTmp = "/tmp/";
    private String charset = "utf-8";
    private Boolean useSSL = false;
    private Boolean needClientAuth = false;
    private String keyStoreFile;
    private String keyStorePassword;

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

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public Boolean isUseSSL() {
        return useSSL;
    }

    public void setUseSSL(Boolean useSSL) {
        this.useSSL = useSSL;
    }

    public Boolean isNeedClientAuth() {
        return needClientAuth;
    }

    public void setNeedClientAuth(Boolean needClientAuth) {
        this.needClientAuth = needClientAuth;
    }

    public String getKeyStoreFile() {
        return keyStoreFile;
    }

    public void setKeyStoreFile(String keyStoreFile) {
        this.keyStoreFile = keyStoreFile;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public String toString() {
        return "ServerConfig{" +
                "fixedRate=" + fixedRate +
                ", sessionTimeout=" + sessionTimeout +
                ", port=" + port +
                ", showSQL=" + showSQL +
                ", fileTmp='" + fileTmp + '\'' +
                ", charset='" + charset + '\'' +
                ", useSSL=" + useSSL +
                ", needClientAuth=" + needClientAuth +
                ", keyStoreFile='" + keyStoreFile + '\'' +
                ", keyStorePassword='" + keyStorePassword + '\'' +
                '}';
    }
}
