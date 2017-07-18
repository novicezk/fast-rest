package com.zhukai.framework.fast.rest.bean.configure;

import com.zhukai.framework.fast.rest.bean.BaseBean;

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
