package com.zhukai.spring.integration.beans.impl;

import com.zhukai.spring.integration.beans.BeanDefinition;

import java.util.Map;

/**
 * Created by zhukai on 17-1-17.
 */
public class ComponentBean implements BeanDefinition {

    private String beanClassName;

    private String factoryBeanName;

    private String factoryMethodName;

    //beanName->fieldName
    private Map<String, String> childBeanNames;

    private boolean singleton;

    @Override
    public Map<String, String> getChildBeanNames() {
        return childBeanNames;
    }

    @Override
    public void setChildBeanNames(Map<String, String> childBeanNames) {
        this.childBeanNames = childBeanNames;
    }

    @Override
    public String getBeanClassName() {
        return beanClassName;
    }

    @Override
    public void setBeanClassName(String var1) {
        this.beanClassName = var1;
    }

    @Override
    public String getFactoryBeanName() {
        return factoryBeanName;
    }

    @Override
    public void setFactoryBeanName(String var1) {
        factoryBeanName = var1;
    }

    @Override
    public String getFactoryMethodName() {
        return factoryMethodName;
    }

    @Override
    public void setFactoryMethodName(String var1) {
        factoryMethodName = var1;
    }

    @Override
    public boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(boolean singleton) {
        this.singleton = singleton;
    }
}
