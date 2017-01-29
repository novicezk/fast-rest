package com.zhukai.spring.integration.proxy.jdk;

import com.zhukai.spring.integration.jdbc.DBUtil;

import java.lang.reflect.*;

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
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Type[] actualTypes = ((ParameterizedType) mapperInterface.getGenericInterfaces()[0]).getActualTypeArguments();
        Class entityClass = (Class) actualTypes[0];
        if (method.getName().equals("findOne")) {
            return DBUtil.getBean(entityClass, args[0]);
        } else if (method.getName().equals("exists")) {
            return DBUtil.exists(entityClass, args[0]);
        } else if (method.getName().equals("findAll")) {
            return DBUtil.getBeans(entityClass, null);
        } else if (method.getName().equals("delete")) {
            DBUtil.delete(entityClass, args[0]);
            return null;
        } else if (method.getName().equals("save")) {
            DBUtil.save(args[0]);
            return null;
        }
        return method.invoke(proxy, args);
    }
}
