package com.zhukai.spring.integration.proxy.jdk;

import com.zhukai.spring.integration.jdbc.MapperMethod;

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
        //return new MapperMethod(method, args, entityClass, null).execute();
        return null;
    }
}
