package com.zhukai.framework.spring.integration.bean;

/**
 * Created by zhukai on 17-1-17.
 */
public class BaseBean {

    private Class beanClass;

    private String registerName;


    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }

    public String getRegisterName() {
        return registerName;
    }

    public void setRegisterName(String registerName) {
        this.registerName = registerName;
    }
}
