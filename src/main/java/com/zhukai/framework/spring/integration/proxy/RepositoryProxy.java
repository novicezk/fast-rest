package com.zhukai.framework.spring.integration.proxy;

import com.zhukai.framework.spring.integration.HttpServletContext;
import com.zhukai.framework.spring.integration.jdbc.data.jpa.MapperMethod;

import java.lang.reflect.*;

public class RepositoryProxy implements InvocationHandler {

    private Class mapperInterface;

    public <T> T getProxyInstance(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{mapperInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        Type[] actualTypes = ((ParameterizedType) mapperInterface.getGenericInterfaces()[0]).getActualTypeArguments();
        Class entityClass = (Class) actualTypes[0];
        MapperMethod mapperMethod = new MapperMethod(method, args, entityClass, HttpServletContext.getInstance().getTransaction());
        Object result = mapperMethod.execute();
        mapperMethod.release();
        return result;
    }
}
