package com.zhukai.framework.fast.rest.proxy;

import com.zhukai.framework.fast.rest.annotation.core.Service;
import com.zhukai.framework.fast.rest.util.ReflectUtil;
import com.zhukai.framework.fast.rest.annotation.core.Repository;

public class ProxyFactory {
    public static Object createInstance(Class clazz) {
        if (clazz.isAnnotationPresent(Repository.class)) {
            return new RepositoryProxy().getProxyInstance(clazz);
        } else if (clazz.isAnnotationPresent(Service.class)) {
            return new AopProxy().getProxyInstance(clazz);
        } else {
            return ReflectUtil.createInstance(clazz);
        }
    }
}
