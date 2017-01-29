package com.zhukai.spring.integration.proxy.jdk;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by zhukai on 17-1-27.
 */
public class MapperMethod {

    private Connection conn;
    private Class mapperInterface;
    private Method method;

    public MapperMethod(Connection conn, Class mapperInterface, Method method) {
        this.conn = conn;
        this.mapperInterface = mapperInterface;
        this.method = method;
    }

    public void execute(Connection conn, Object[] args) {

    }

    public MapperMethod() {
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    public Class getMapperInterface() {
        return mapperInterface;
    }

    public void setMapperInterface(Class mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
