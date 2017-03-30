package com.zhukai.framework.spring.integration.utils;

import com.google.gson.Gson;

/**
 * Created by zhukai on 17-1-26.
 */
public class JsonUtil {
    private static Gson gson = new Gson();

    public static String toJson(Object object) {
        if (object != null && isBasicType(object)) {
            return object.toString();
        }
        return gson.toJson(object);
    }

    public static <T> T convertObj(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    private static boolean isBasicType(Object object) {
        if (object instanceof Integer || object instanceof String
                || object instanceof Float || object instanceof Double
                || object instanceof Byte || object instanceof Long
                || object instanceof Short || object instanceof Boolean) {
            return true;
        }
        return false;
    }
}
