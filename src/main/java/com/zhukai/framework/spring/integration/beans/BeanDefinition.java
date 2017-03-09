package com.zhukai.framework.spring.integration.beans;

import java.util.Map;

/**
 * Created by zhukai on 17-1-17.
 */
public interface BeanDefinition {

    String getBeanClassName();

    void setBeanClassName(String var1);

    String getFactoryBeanName();

    void setFactoryBeanName(String var1);

    String getFactoryMethodName();

    void setFactoryMethodName(String var1);

    boolean isSingleton();

    Map<String, String> getChildBeanNames();

    void setChildBeanNames(Map<String, String> childBeanNameMap);
}
