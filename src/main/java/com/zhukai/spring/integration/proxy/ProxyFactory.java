package com.zhukai.spring.integration.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * Created by zhukai on 17-1-22.
 */
public class ProxyFactory {

    //创建一个clazz的继承类，额外增加intercept()方法，该clazz可以不是接口的实现类
    public static <T> T getCglibProxyInstance(Class<T> clazz, MethodInterceptor interceptor) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(interceptor);
        return (T) enhancer.create();
    }

    //该clazz可以是接口，针对@Repository注解
    public static <T> T getJdkProxyInstance(Class<T> clazz, InvocationHandler handler) {
        return (T) Proxy.newProxyInstance(handler.getClass().getClassLoader(),
                new Class[]{clazz}, handler);
    }
}
