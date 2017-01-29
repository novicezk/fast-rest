package com.zhukai.spring.integration.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by zhukai on 17-1-22.
 */
public class CommonProxy implements MethodInterceptor {

    //创建一个clazz的继承类，额外增加intercept()方法，该clazz可以不是接口的实现类
    public <T> T getProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //  System.out.println(method.getName() + "前置代理");
        Object result = proxy.invokeSuper(obj, args);
        //System.out.println(method.getName() + "后置代理");
        return result;
    }

}
