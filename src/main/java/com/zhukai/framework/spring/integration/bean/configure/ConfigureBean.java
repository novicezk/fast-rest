package com.zhukai.framework.spring.integration.bean.configure;

import com.zhukai.framework.spring.integration.bean.BaseBean;

import java.util.Properties;

public class ConfigureBean extends BaseBean {
    private Properties properties;
    private String prefix;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
