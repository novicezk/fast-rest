package com.zhukai.framework.spring.integration.bean;

import com.zhukai.framework.spring.integration.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by zhukai on 17-1-17.
 */
public interface BeanFactory<Bean> {

    Object getBean(String beanName);

    boolean containsBean(String name);

    void registerBean(Bean baseBean);

    default <T> T getBean(String beanName, T requiredType) {
        return (T) getBean(beanName);
    }

    default <T> T getBean(Class<T> requiredType) {
        String beanName = ReflectUtil.getBeanRegisterName(requiredType);
        beanName = StringUtils.isBlank(beanName) ? requiredType.getName() : beanName;
        return (T) getBean(beanName);
    }
}
