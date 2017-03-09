package com.zhukai.framework.spring.integration.beans;

/**
 * Created by zhukai on 17-1-17.
 */
public interface BeanFactory {

    Object getBean(String beanName);

    <T> T getBean(String beanName, T requiredType);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);
}
