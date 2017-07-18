package com.zhukai.framework.spring.integration.bean;

import com.zhukai.framework.spring.integration.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;

public interface BeanFactory<Bean> {

    Object getBean(String beanName);

    boolean containsBean(String name);

    void registerBean(Bean baseBean);

    default <T> T getBean(String beanName, Class<T> requiredType) {
        return requiredType.cast(getBean(beanName));
    }

    default <T> T getBean(Class<T> requiredType) {
        String beanName = ReflectUtil.getComponentValue(requiredType);
        beanName = StringUtils.isBlank(beanName) ? requiredType.getName() : beanName;
        return requiredType.cast(getBean(beanName));
    }
}
