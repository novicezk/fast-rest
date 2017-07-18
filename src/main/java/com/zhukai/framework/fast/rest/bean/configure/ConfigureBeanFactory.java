package com.zhukai.framework.fast.rest.bean.configure;

import com.zhukai.framework.fast.rest.config.DataSource;
import com.zhukai.framework.fast.rest.bean.BeanFactory;
import com.zhukai.framework.fast.rest.util.TypeUtil;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfigureBeanFactory implements BeanFactory<ConfigureBean> {

    private static final Logger logger = Logger.getLogger(ConfigureBeanFactory.class);
    private static ConfigureBeanFactory instance = new ConfigureBeanFactory();
    private final Map<String, Object> configureMap = Collections.synchronizedMap(new HashMap<>());

    public static ConfigureBeanFactory getInstance() {
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
            if (properties != null) {
                String prefix = configureBean.getPrefix().equals("") ? "" : configureBean.getPrefix() + ".";
                Field[] fields = configureBean.getBeanClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    Object configValue = properties.get(prefix + field.getName());
                    if (configValue == null) {
                        continue;
                    }
                    try {
                        field.set(object, TypeUtil.convert(configValue, field.getType()));
                    } catch (Exception e) {
                        logger.error("reflect error", e);
                    }
                }
            }
            if (configureBean.getBeanClass().equals(DataSource.class) && object != null && ((DataSource) object).getUrl() == null) {
                return;
            }
            logger.info(object);
            configureMap.put(configureBean.getRegisterName(), object);
            logger.info("Register in configureBeanFactory: " + configureBean.getRegisterName() + " = " + configureBean.getBeanClass().getSimpleName() + ".class");
        }
    }

}
