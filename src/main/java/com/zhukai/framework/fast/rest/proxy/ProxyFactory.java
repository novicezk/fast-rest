package com.zhukai.framework.fast.rest.proxy;

import com.zhukai.framework.fast.rest.Setup;
import com.zhukai.framework.fast.rest.annotation.jpa.Repository;
import com.zhukai.framework.fast.rest.annotation.aop.Transactional;
import com.zhukai.framework.fast.rest.util.ReflectUtil;

import java.lang.reflect.Method;

public class ProxyFactory {

	public static Object createInstance(Class clazz) {
		if (clazz.isAnnotationPresent(Repository.class)) {
			return new RepositoryProxy().getProxyInstance(clazz);
		}
		Method[] methods = clazz.getMethods();
		for (Method method : methods) {
			if (method.isAnnotationPresent(Transactional.class) || Setup.getMethodInterceptors().get(method) != null) {
				System.out.println(method);
				return new AopProxy().getProxyInstance(clazz);
			}
		}
		return ReflectUtil.createInstance(clazz);
	}
}
