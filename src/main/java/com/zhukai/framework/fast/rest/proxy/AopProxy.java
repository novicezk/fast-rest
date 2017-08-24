package com.zhukai.framework.fast.rest.proxy;

import com.zhukai.framework.fast.rest.Setup;
import com.zhukai.framework.fast.rest.annotation.aop.Transactional;
import com.zhukai.framework.fast.rest.bean.component.ComponentBeanFactory;
import com.zhukai.framework.fast.rest.http.HttpContext;
import com.zhukai.framework.fast.rest.jdbc.DBConnectionPool;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
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
		try {
			if (method.isAnnotationPresent(Transactional.class)) {
				connection = DBConnectionPool.getConnection();
				connection.setAutoCommit(false);
				HttpContext.getInstance().setTransaction(connection);
			}
			if (Setup.getMethodInterceptors().containsKey(method)) {
				InterceptorPoint.MethodSignature signature = new InterceptorPoint.MethodSignature(method, obj, args, proxy);
				InterceptorPoint firstPoint = null;
				InterceptorPoint eachPoint = null;
				for (Method interceptorMethod : Setup.getMethodInterceptors().get(method)) {
					if (firstPoint == null) {
						firstPoint = new InterceptorPoint(signature, interceptorMethod);
						eachPoint = firstPoint;
					} else {
						eachPoint = eachPoint.setNext(new InterceptorPoint(signature, interceptorMethod));
					}
				}
				return firstPoint.getAround().invoke(ComponentBeanFactory.getInstance().getBean(firstPoint.getAround().getDeclaringClass()), firstPoint);
			}
			return proxy.invokeSuper(obj, args);
		} catch (InvocationTargetException ite) {
			throw ite.getTargetException();
		} finally {
			if (connection != null) {
				DBConnectionPool.commit(connection);
			}
		}
	}

}
