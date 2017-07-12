package com.zhukai.framework.spring.integration.proxy;

import com.zhukai.framework.spring.integration.annotation.core.Repository;
import com.zhukai.framework.spring.integration.annotation.core.Service;
import com.zhukai.framework.spring.integration.util.ReflectUtil;

public class ProxyFactory {
    public static Object createInstance(Class clazz) {
        if (clazz.isAnnotationPresent(Repository.class)) {
            return new RepositoryProxy().getProxyInstance(clazz);
        } else if (clazz.isAnnotationPresent(Service.class)) {
            return new AopProxy().getProxyInstance(clazz);
        } else {
            return ReflectUtil.createInstance(clazz, new Class[]{}, new Object[]{});
        }
    }
}
