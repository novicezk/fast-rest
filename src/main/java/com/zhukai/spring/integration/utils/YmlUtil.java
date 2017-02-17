package com.zhukai.spring.integration.utils;

import com.zhukai.spring.integration.server.SpringIntegration;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

/**
 * Created by zhukai on 17-2-17.
 */
public class YmlUtil {

    public static Object getValue(String key) {
        return getValue("application.yml", key);
    }

    public static Object getValue(String fileName, String key) {
        Object loadObject = new Yaml().load(SpringIntegration.runClass.
                getResourceAsStream("/" + fileName));
        String[] keyArr = key.split("\\.");
        for (int i = 0; i < keyArr.length; i++) {
            if (loadObject instanceof Map) {
                loadObject = ((Map) loadObject).get(keyArr[i]);
            } else {
                return null;
            }
        }
        return loadObject;
    }

}
