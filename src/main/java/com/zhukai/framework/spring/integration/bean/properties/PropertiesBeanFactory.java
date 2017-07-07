package com.zhukai.framework.spring.integration.bean.properties;

import com.zhukai.framework.spring.integration.constant.IntegrationConstants;
import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.bean.BaseBean;
import com.zhukai.framework.spring.integration.bean.BeanFactory;
import com.zhukai.framework.spring.integration.util.ReflectUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by homolo on 17-6-14.
 */
public class PropertiesBeanFactory implements BeanFactory<BaseBean> {
    private static final Logger logger = Logger.getLogger(PropertiesBeanFactory.class);
    
    private static Map<String, Properties> propertiesMap = Collections.synchronizedMap(new HashMap());
    private static PropertiesBeanFactory instance = new PropertiesBeanFactory();

    private PropertiesBeanFactory() {
    }

    public static PropertiesBeanFactory getInstance() {
        return instance;
    }

    public static Properties getProperties(String key) {
        return propertiesMap.get(key);
    }

    @Override
    public Object getBean(String beanName) {
        return propertiesMap.get(beanName);
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        String beanName = ReflectUtil.getBeanRegisterName(requiredType);
        beanName = StringUtils.isBlank(beanName) ? IntegrationConstants.DEFAULT_PROPERTIES : beanName;
        return (T) getBean(beanName);
    }

    @Override
    public boolean containsBean(String name) {
        return propertiesMap.containsKey(name);
    }

    @Override
    public void registerBean(BaseBean baseBean) {
        if (containsBean(baseBean.getRegisterName())) {
            return;
        }
        InputStream propertiesInputStream = SpringIntegration.getRunClass().getResourceAsStream("/" + baseBean.getRegisterName());
        if (propertiesInputStream == null) {
            logger.warn("Have no " + baseBean.getRegisterName());
            return;
        }
        Properties properties = new Properties();
        try {
            properties.load(propertiesInputStream);
            propertiesMap.put(baseBean.getRegisterName(), properties);
            logger.info("Register in propertiesBeanFactory: " + baseBean.getRegisterName());
        } catch (IOException e) {
            logger.warn(baseBean.getRegisterName() + " load fail", e);
        }
    }
}
