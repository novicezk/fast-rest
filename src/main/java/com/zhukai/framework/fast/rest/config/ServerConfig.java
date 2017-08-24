package com.zhukai.framework.fast.rest.config;

import com.zhukai.framework.fast.rest.annotation.core.Configure;

@Configure(prefix = "server")
public class ServerConfig {

	private Long sessionTimeout = 7200000L;
	private Integer port = 8080;
	private String fileTmp = "/tmp/";
	private String charset = "utf-8";
	private String indexPage = "index.html";
	private Boolean useSSL = false;
	private Boolean needClientAuth = false;
	private String keyStoreFile;
	private String keyStorePassword;

	public ServerConfig() {
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

	public String getIndexPage() {
		return indexPage;
	}

	public void setIndexPage(String indexPage) {
		this.indexPage = indexPage;
	}

	@Override
	public String toString() {
		return "ServerConfig{" + "sessionTimeout=" + sessionTimeout + ", port=" + port + ", fileTmp='" + fileTmp + '\'' + ", charset='" + charset + '\'' + ", indexPage='" + indexPage + '\'' + ", useSSL=" + useSSL + ", needClientAuth="
				+ needClientAuth + ", keyStoreFile='" + keyStoreFile + '\'' + ", keyStorePassword='" + keyStorePassword + '\'' + '}';
	}
}
