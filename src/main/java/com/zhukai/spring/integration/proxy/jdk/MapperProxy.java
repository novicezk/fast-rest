package com.zhukai.spring.integration.proxy.jdk;

import com.zhukai.spring.integration.jdbc.DBConnectionPool;
import com.zhukai.spring.integration.jdbc.MapperMethod;
import com.zhukai.spring.integration.logger.Logger;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by zhukai on 17-1-22.
 */
//针对@Repository注解
public class MapperProxy implements InvocationHandler {

    private Class mapperInterface;

    public <T> T getProxyInstance(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{mapperInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws SQLException {
        Type[] actualTypes = ((ParameterizedType) mapperInterface.getGenericInterfaces()[0]).getActualTypeArguments();
        Class entityClass = (Class) actualTypes[0];
        Connection conn = DBConnectionPool.getConnection();
        MapperMethod mapperMethod = new MapperMethod(method, args, entityClass, conn);
        try {
            conn.setAutoCommit(false);
            Object result = mapperMethod.execute();
            conn.commit();
            return result;
        } catch (Exception e) {
            conn.rollback();
            Logger.error();
            e.printStackTrace();
            return null;
        } finally {
            mapperMethod.freeResource();
        }
    }
}
