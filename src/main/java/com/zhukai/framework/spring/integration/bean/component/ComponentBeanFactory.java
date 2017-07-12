package com.zhukai.framework.spring.integration.bean.component;

import com.zhukai.framework.spring.integration.bean.BeanFactory;
import com.zhukai.framework.spring.integration.bean.ChildBean;
import com.zhukai.framework.spring.integration.proxy.ProxyFactory;
import com.zhukai.framework.spring.integration.util.ReflectUtil;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ComponentBeanFactory implements BeanFactory<ComponentBean> {

    private static final Logger logger = Logger.getLogger(ComponentBeanFactory.class);

    private static ComponentBeanFactory instance = new ComponentBeanFactory();
    private final Map<String, ComponentBean> componentBeanMap = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Object> singletonBeanMap = Collections.synchronizedMap(new HashMap<>());

    public static ComponentBeanFactory getInstance() {
        return instance;
    }

    private ComponentBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) {
        if (!componentBeanMap.containsKey(beanName)) {
            logger.warn("ComponentBeanFactory not exits " + beanName);
            return null;
        }
        ComponentBean componentBean = componentBeanMap.get(beanName);
        Class beanClass = componentBean.getBeanClass();
        Object object;
        if (componentBean.isSingleton()) {
            if (singletonBeanMap.containsKey(beanName)) {
                return singletonBeanMap.get(beanName);
            } else {
                object = ProxyFactory.createInstance(beanClass);
                singletonBeanMap.put(beanName, object);
            }
        } else {
            object = ProxyFactory.createInstance(beanClass);
        }
        for (ChildBean childBean : componentBean.getChildren()) {
            ReflectUtil.setFieldValue(object, childBean.getFieldName(), childBean.getBeanFactory().getBean(childBean.getRegisterName()));
        }
        return object;
    }

    @Override
    public boolean containsBean(String name) {
        return componentBeanMap.containsKey(name);
    }

    @Override
    public void registerBean(ComponentBean componentBean) {
        if (!componentBeanMap.containsKey(componentBean.getRegisterName())) {
            componentBeanMap.put(componentBean.getRegisterName(), componentBean);
            if (componentBean.isSingleton()) {
                singletonBeanMap.put(componentBean.getRegisterName(), getBean(componentBean.getRegisterName()));
            }
            logger.info("Register in componentBeanFactory: " + componentBean.getRegisterName() + " = " + componentBean.getBeanClass().getSimpleName() + ".class");
        }
    }

}
