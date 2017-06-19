package com.zhukai.framework.spring.integration.beans.component;

import com.zhukai.framework.spring.integration.annotation.core.Repository;
import com.zhukai.framework.spring.integration.annotation.core.Service;
import com.zhukai.framework.spring.integration.beans.BeanFactory;
import com.zhukai.framework.spring.integration.beans.ChildBean;
import com.zhukai.framework.spring.integration.proxy.AopProxy;
import com.zhukai.framework.spring.integration.proxy.RepositoryProxy;
import com.zhukai.framework.spring.integration.utils.ReflectUtil;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhukai on 17-1-17.
 */
public class ComponentBeanFactory implements BeanFactory<ComponentBean> {

    private static final Logger logger = Logger.getLogger(ComponentBeanFactory.class);

    private static ComponentBeanFactory instance = new ComponentBeanFactory();
    private final Map<String, ComponentBean> componentBeanMap = Collections.synchronizedMap(new HashMap());
    private final Map<String, Object> singletonBeanMap = Collections.synchronizedMap(new HashMap());

    public static final ComponentBeanFactory getInstance() {
        return instance;
    }

    private ComponentBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) {
        if (!componentBeanMap.containsKey(beanName)) {
            logger.error("ComponentBeanFactory中不存在" + beanName);
            return null;
        }
        ComponentBean componentBean = componentBeanMap.get(beanName);
        Class beanClass = componentBean.getBeanClass();
        Object object;
        if (componentBean.isSingleton()) {
            if (singletonBeanMap.containsKey(beanName)) {
                return singletonBeanMap.get(beanName);
            } else {
                object = createInstance(beanClass);
                singletonBeanMap.put(beanName, object);
            }
        } else {
            object = createInstance(beanClass);
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
        }
    }

    private Object createInstance(Class clazz) {
        if (clazz.isAnnotationPresent(Repository.class)) {
            return new RepositoryProxy().getProxyInstance(clazz);
        } else if (clazz.isAnnotationPresent(Service.class)) {
            return new AopProxy().getProxyInstance(clazz);
        } else {
            return ReflectUtil.createInstance(clazz, null, null);
        }

    }

}
