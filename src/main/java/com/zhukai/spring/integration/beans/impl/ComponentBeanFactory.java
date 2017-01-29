package com.zhukai.spring.integration.beans.impl;

import com.zhukai.spring.integration.beans.BeanDefinition;
import com.zhukai.spring.integration.beans.BeanFactory;
import com.zhukai.spring.integration.commons.annotation.Repository;
import com.zhukai.spring.integration.commons.utils.ReflectUtil;
import com.zhukai.spring.integration.proxy.*;
import com.zhukai.spring.integration.logger.Logger;
import com.zhukai.spring.integration.proxy.cglib.CommonProxy;
import com.zhukai.spring.integration.proxy.jdk.MapperProxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhukai on 17-1-17.
 */
public class ComponentBeanFactory implements BeanFactory {

    private static ComponentBeanFactory instance = new ComponentBeanFactory();

    private final Map<String, BeanDefinition> beanDefinitionMap = Collections.synchronizedMap(new HashMap());
    private final Map<String, Object> singletonBeanMap = Collections.synchronizedMap(new HashMap());

    public static ComponentBeanFactory getInstance() {
        return instance;
    }

    private ComponentBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) {
        try {
            return autowiredBean(null, null, beanName);
        } catch (Exception e) {
            Logger.error();
            e.printStackTrace();
        }
        return null;
    }


    /**
     * @param object            创建的bean
     * @param fieldName         bean的属性名
     * @param autowiredBeanName bean的field对应的注册别名
     * @return autowiredBeanName的实例
     * @throws Exception
     */
    private Object autowiredBean(Object object, String fieldName, String autowiredBeanName) throws Exception {
        if (!beanDefinitionMap.containsKey(autowiredBeanName)) {
            throw new Exception("BeanFactory中不存在" + autowiredBeanName);
        }
        BeanDefinition beanDefinition = beanDefinitionMap.get(autowiredBeanName);

        Object autowired;
        if (beanDefinition.isSingleton()) {
            autowired = singletonBeanMap.get(autowiredBeanName);
            if (autowired == null) {
                autowired = createProxyInstance(beanDefinition.getBeanClassName());
                singletonBeanMap.put(autowiredBeanName, autowired);
            }
        } else {
            autowired = createProxyInstance(beanDefinition.getBeanClassName());
            for (String childBeanName : beanDefinition.getChildBeanNames().keySet()) {
                autowiredBean(autowired, beanDefinition.getChildBeanNames().get(childBeanName), childBeanName);
            }
        }
        if (object != null && fieldName != null) {
            ReflectUtil.setFieldValue(object, fieldName, autowired);
            return object;
        }
        return autowired;
    }

    private Object createProxyInstance(String className) throws ClassNotFoundException {
        Class clazz = Class.forName(className);
        if (clazz.isAnnotationPresent(Repository.class)) {
            return new MapperProxy().getProxyInstance(clazz);
        }
        return new CommonProxy().getProxyInstance(clazz);
    }

    @Override
    public <T> T getBean(String beanName, T requiredType) {
        return (T) getBean(beanName);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        String beanName = ReflectUtil.getBeanRegisterName(requiredType);
        beanName = beanName.equals("") ? requiredType.getName() : beanName;
        return (T) getBean(beanName);
    }

    @Override
    public boolean containsBean(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws ClassNotFoundException {
        if (!beanDefinitionMap.containsKey(beanName)) {
            beanDefinitionMap.put(beanName, beanDefinition);
            if (beanDefinition.isSingleton()) {
                singletonBeanMap.put(beanName, getBean(beanName));
            }
        }
    }

}
