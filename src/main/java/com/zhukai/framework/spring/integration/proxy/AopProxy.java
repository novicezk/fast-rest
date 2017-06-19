package com.zhukai.framework.spring.integration.proxy;

import com.zhukai.framework.spring.integration.annotation.core.Transactional;
import com.zhukai.framework.spring.integration.context.WebContext;
import com.zhukai.framework.spring.integration.jdbc.DBConnectionPool;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by zhukai on 17-1-22.
 */
public class AopProxy implements MethodInterceptor {
    private static Logger logger = Logger.getLogger(AopProxy.class);

    //该clazz可以不是接口的实现类,用来service类
    public <T> T getProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return (T) enhancer.create();
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Connection connection = null;
        if (method.isAnnotationPresent(Transactional.class)) {
            logger.info("Transactional begin...");
            connection = DBConnectionPool.getInstance().getConnection();
            connection.setAutoCommit(false);
            WebContext.setTransaction(connection);
        }
        Object result = proxy.invokeSuper(obj, args);
        if (connection != null) {
            DBConnectionPool.commit(connection);
        }
        return result;
    }

}
