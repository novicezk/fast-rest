package com.zhukai.framework.spring.integration.beans.configure;

import com.zhukai.framework.spring.integration.beans.BaseBean;

import java.util.Properties;

/**
 * Created by zhukai on 17-1-17.
 */
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
