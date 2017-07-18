package com.zhukai.framework.fast.rest.util;

import com.google.gson.Gson;

public class JsonUtil {
    private static final Gson gson = new Gson();

    public static String toJson(Object object) {
        if (object != null && TypeUtil.isBasicType(object)) {
            return object.toString();
        }
        return gson.toJson(object);
    }

    public static <T> T convertObj(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    private JsonUtil() {
    }
}
