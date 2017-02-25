package com.zhukai.spring.integration.proxy.cglib;

import com.zhukai.spring.integration.annotation.core.Transactional;
import com.zhukai.spring.integration.annotation.core.Value;
import com.zhukai.spring.integration.context.WebContext;
import com.zhukai.spring.integration.jdbc.DBConnectionPool;
import com.zhukai.spring.integration.utils.YmlUtil;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;

/**
 * Created by zhukai on 17-1-22.
 */
public class CommonProxy implements MethodInterceptor {
    private static Logger logger = Logger.getLogger(CommonProxy.class);

    //该clazz可以不是接口的实现类,用来代理除Repository之外的类
    public <T> T getProxyInstance(Class<T> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        T object = (T) enhancer.create();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Value.class)) {
                String valueKey = field.getAnnotation(Value.class).value();
                String fileName = field.getAnnotation(Value.class).fileName();
                Object value = fileName.equals("") ? YmlUtil.getValue(valueKey) : YmlUtil.getValue(fileName, valueKey);
                field.setAccessible(true);
                try {
                    field.set(object, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return object;
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
