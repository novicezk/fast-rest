package com.zhukai.framework.fast.rest.proxy;

import java.lang.reflect.*;
import java.sql.Connection;

import com.zhukai.framework.fast.rest.http.HttpServletContext;
import com.zhukai.framework.fast.rest.jdbc.data.jpa.MapperMethod;

public class RepositoryProxy implements InvocationHandler {

	private Class mapperInterface;

	public <T> T getProxyInstance(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
		Object object = Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { mapperInterface }, this);
		return mapperInterface.cast(object);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
		Type[] actualTypes = ((ParameterizedType) mapperInterface.getGenericInterfaces()[0]).getActualTypeArguments();
		Class<?> entityClass = (Class<?>) actualTypes[0];
		Connection conn = HttpServletContext.getInstance().getTransaction();
		MapperMethod mapperMethod = new MapperMethod(method, args, entityClass, conn);
		Object result = mapperMethod.execute();
		mapperMethod.release();
		return result;
	}
}
