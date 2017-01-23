package com.zhukai.spring.integration.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by zhukai on 17-1-22.
 */
public class DaoHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("it is a test");
        return null;
    }
}
