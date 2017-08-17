package com.zhukai.framework.fast.rest.bean.configure;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhukai.framework.fast.rest.bean.BeanFactory;
import com.zhukai.framework.fast.rest.config.DataSource;
import com.zhukai.framework.fast.rest.util.JsonUtil;
import com.zhukai.framework.fast.rest.util.TypeUtil;

public class ConfigureBeanFactory implements BeanFactory<ConfigureBean> {

	private static final Logger logger = LoggerFactory.getLogger(ConfigureBeanFactory.class);
	private static ConfigureBeanFactory instance = new ConfigureBeanFactory();

	public static ConfigureBeanFactory getInstance() {
		return instance;
	}

	private final Map<String, Object> configureMap = Collections.synchronizedMap(new HashMap<>());

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
		if (containsBean(configureBean.getRegisterName())) {
			return;
		}
		Properties properties = configureBean.getProperties();
		try {
			Object object = configureBean.getBeanClass().newInstance();
			if (properties != null) {
				String prefix = configureBean.getPrefix().equals("") ? "" : configureBean.getPrefix() + ".";
				Field[] fields = configureBean.getBeanClass().getDeclaredFields();
				for (Field field : fields) {
					field.setAccessible(true);
					Object configValue = properties.get(prefix + field.getName());
					if (configValue == null) {
						continue;
					}
					field.set(object, TypeUtil.convert(configValue, field.getType()));
				}
			}
			if (configureBean.getBeanClass().equals(DataSource.class) && object != null && ((DataSource) object).getUrl() == null) {
				return;
			}
			logger.info("{} = {}", configureBean.getBeanClass().getSimpleName(), JsonUtil.toJson(object));
			configureMap.put(configureBean.getRegisterName(), object);
			logger.info("Register in configureBeanFactory: {} = {}.class", configureBean.getRegisterName(), configureBean.getBeanClass().getSimpleName());
		} catch (Exception e) {
			logger.error("Register bean fail, registerName: {}", configureBean.getRegisterName(), e);
		}
	}

}
