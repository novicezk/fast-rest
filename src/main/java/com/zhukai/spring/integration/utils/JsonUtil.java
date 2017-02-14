package com.zhukai.spring.integration.utils;

import com.google.gson.Gson;

/**
 * Created by zhukai on 17-1-26.
 */
public class JsonUtil {
    private static Gson gson = new Gson();

    public static String toJson(Object object) {
        return gson.toJson(object);
    }

    public static <T> T convertObj(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
}
