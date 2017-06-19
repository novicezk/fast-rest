package com.zhukai.framework.spring.integration.beans.configure;

import com.zhukai.framework.spring.integration.beans.BeanFactory;
import com.zhukai.framework.spring.integration.utils.ParameterUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by zhukai on 17-1-17.
 */
public class ConfigureBeanFactory implements BeanFactory<ConfigureBean> {

    private static final Logger logger = Logger.getLogger(ConfigureBeanFactory.class);
    private static ConfigureBeanFactory instance = new ConfigureBeanFactory();
    private final Map<String, Object> configureMap = Collections.synchronizedMap(new HashMap());

    public static final ConfigureBeanFactory getInstance() {
        return instance;
    }

    private ConfigureBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) {
        return configureMap.get(beanName);
    }

    @Override
    public boolean containsBean(String name) {
        return configureMap.containsKey(name);
    }

    @Override
    public void registerBean(ConfigureBean configureBean) {
        if (!configureMap.containsKey(configureBean.getRegisterName())) {
            Properties properties = configureBean.getProperties();

            Object object = null;
            try {
                object = configureBean.getBeanClass().newInstance();
            } catch (Exception e) {
                logger.error("reflect error", e);
            }
            String prefix = configureBean.getPrefix().equals("") ? "" : configureBean.getPrefix() + ".";
            Field[] fields = configureBean.getBeanClass().getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object configValue = properties.get(prefix + field.getName());
                if (configValue == null) {
                    continue;
                }

                try {
                    field.set(object, ParameterUtil.convert(configValue, field.getType()));
                } catch (Exception e) {
                    System.out.println(field.getName());
                    logger.error("reflect error", e);
                }
            }
            configureMap.put(configureBean.getRegisterName(), object);
        }
    }

}
