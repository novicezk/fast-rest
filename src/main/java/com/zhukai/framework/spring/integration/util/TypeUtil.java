package com.zhukai.framework.spring.integration.util;

import java.lang.reflect.Method;

/**
 * Created by zhukai on 17-1-16.
 */
public class TypeUtil {

    public static <T> T convert(Object preValue, Class<T> convertTo) throws Exception {
        if (StringUtil.isBlank(preValue)) {
            return null;
        }
        if (convertTo.equals(String.class)) {
            return (T) preValue.toString();
        } else if (isBasicType(convertTo)) {
            String simpleName = convertTo.getSimpleName();
            simpleName = simpleName.equals("Integer") ? "Int" : simpleName;
            Method method = convertTo.getMethod("parse" + simpleName, String.class);
            return (T) method.invoke(null, preValue.toString());
        }
        return (T) preValue;
    }

    public static boolean isBasicType(Object object) {
        return object == null ? false : isBasicType(object.getClass());
    }

    public static boolean isBasicType(Class clazz) {
        if (Integer.class.equals(clazz) || Float.class.equals(clazz) || Double.class.equals(clazz) ||
                Byte.class.equals(clazz) || Long.class.equals(clazz) || Short.class.equals(clazz) ||
                Boolean.class.equals(clazz) || String.class.equals(clazz)) {
            return true;
        }
        return false;
    }

    private TypeUtil() {
    }
}
