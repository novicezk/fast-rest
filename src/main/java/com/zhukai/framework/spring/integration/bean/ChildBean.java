package com.zhukai.framework.spring.integration.bean;

/**
 * Created by zhukai on 17-1-17.
 */
public class ChildBean extends BaseBean {


    private BeanFactory beanFactory;

    private String fieldName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

}