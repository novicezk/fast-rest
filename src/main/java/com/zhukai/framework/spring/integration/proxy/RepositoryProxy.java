package com.zhukai.framework.spring.integration.proxy;

import com.zhukai.framework.spring.integration.WebContext;
import com.zhukai.framework.spring.integration.jdbc.data.jpa.MapperMethod;

import java.lang.reflect.*;

/**
 * Created by zhukai on 17-1-22.
 */
//只用来代理Repository类
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
        MapperMethod mapperMethod = new MapperMethod(method, args, entityClass, WebContext.getTransaction());
        Object result = mapperMethod.execute();
        mapperMethod.release();
        return result;
    }
}
