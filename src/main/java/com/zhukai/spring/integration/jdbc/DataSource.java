package com.zhukai.spring.integration.jdbc;

/**
 * Created by zhukai on 17-1-18.
 */
public class DataSource {
    private String username;
    private String password;
    private String url;
    private String driverClass;
    //最小数量
    private int minConn = 2;
    //最大连接数
    private int maxConn = 100;
    private long timeout = 100;

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

    public int getMinConn() {
        return minConn;
    }

    public void setMinConn(int minConn) {
        this.minConn = minConn;
    }

    public int getMaxConn() {
        return maxConn;
    }

    public void setMaxConn(int maxConn) {
        this.maxConn = maxConn;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long waitTime) {
        this.timeout = waitTime;
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
