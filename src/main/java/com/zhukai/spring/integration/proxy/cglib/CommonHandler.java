package com.zhukai.spring.integration.proxy.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by zhukai on 17-1-22.
 */
public class CommonHandler implements MethodInterceptor {

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        //  System.out.println(method.getName() + "前置代理");
        Object result = proxy.invokeSuper(obj, args);
        //System.out.println(method.getName() + "后置代理");
        return result;
    }

}
