package com.zhukai.framework.fast.rest.proxy;

import com.zhukai.framework.fast.rest.http.HttpServletContext;
import com.zhukai.framework.fast.rest.annotation.core.Transactional;
import com.zhukai.framework.fast.rest.jdbc.DBConnectionPool;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.sql.Connection;

public class AopProxy implements MethodInterceptor {

    public <T> T getProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return clazz.cast(enhancer.create());
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Connection connection = null;
        if (method.isAnnotationPresent(Transactional.class)) {
            connection = DBConnectionPool.getConnection();
            connection.setAutoCommit(false);
            HttpServletContext.getInstance().setTransaction(connection);
        }
        Object result = proxy.invokeSuper(obj, args);
        if (connection != null) {
            DBConnectionPool.commit(connection);
        }
        return result;
    }

}
