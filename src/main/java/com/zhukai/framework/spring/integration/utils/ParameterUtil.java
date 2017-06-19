package com.zhukai.framework.spring.integration.utils;

/**
 * Created by zhukai on 17-1-16.
 */
public class ParameterUtil {
    public static Object convert(Object preValue, Class convertTo) {
        if (preValue == null)
            return null;
        Object convertValue = null;
        if (convertTo.isAssignableFrom(Integer.class)) {
            convertValue = Integer.parseInt(preValue.toString());
        } else if (convertTo.isAssignableFrom(Float.class)) {
            convertValue = Float.parseFloat(preValue.toString());
        } else if (convertTo.isAssignableFrom(Double.class)) {
            convertValue = Double.parseDouble(preValue.toString());
        } else if (convertTo.isAssignableFrom(Short.class)) {
            convertValue = Short.parseShort(preValue.toString());
        } else if (convertTo.isAssignableFrom(Long.class)) {
            convertValue = Long.parseLong(preValue.toString());
        } else if (convertTo.isAssignableFrom(String.class)) {
            convertValue = preValue;
        } else if (convertTo.isAssignableFrom(Byte.class)) {
            convertValue = Byte.parseByte(preValue.toString());
        } else if (convertTo.isAssignableFrom(Boolean.class)) {
            convertValue = Boolean.parseBoolean(preValue.toString());
        } else if (convertTo.isAssignableFrom(String.class)) {
            convertValue = preValue.toString();
        }
        return convertValue == null ? preValue : convertValue;
    }

}
