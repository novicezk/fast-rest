package com.zhukai.framework.spring.integration.config;

import com.zhukai.framework.spring.integration.annotation.core.Configure;

/**
 * Created by zhukai on 17-1-18.
 */
@Configure(prefix = "datasource")
public class DataSource {
    private String username;
    private String password;
    private String url;
    private String driverClass;
    //最小数量
    private Integer minConn = 2;
    //最大连接数
    private Integer maxConn = 100;
    private Long timeout = 500L;

    public DataSource() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public void setDriverClass(String driverClass) {
        this.driverClass = driverClass;
    }

    public Integer getMinConn() {
        return minConn;
    }

    public void setMinConn(Integer minConn) {
        this.minConn = minConn;
    }

    public Integer getMaxConn() {
        return maxConn;
    }

    public void setMaxConn(Integer maxConn) {
        this.maxConn = maxConn;
    }

    public Long getTimeout() {
        return timeout;
    }

    public void setTimeout(Long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return "DataSource{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", url='" + url + '\'' +
                ", driverClass='" + driverClass + '\'' +
                ", minConn=" + minConn +
                ", maxConn=" + maxConn +
                ", timeout=" + timeout +
                '}';
    }
}
