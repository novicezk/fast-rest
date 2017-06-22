package com.zhukai.framework.spring.integration.util;

/**
 * Created by zhukai on 16-12-15.
 */
public class StringUtil {
    public static boolean isBlank(Object value) {
        if (value == null)
            return true;
        String valueStr = value.toString().trim();
        return valueStr.isEmpty();
    }

    //首字母转大写
    public static String letterToUpperCase(String value) {
        char[] chars = value.toCharArray();
        if (chars[0] >= 97 && chars[0] <= 97 + 26)
            chars[0] -= 32;
        return String.valueOf(chars);
    }

    //首字母转小写
    public static String letterToLowerCase(String value) {
        char[] chars = value.toCharArray();
        if (chars[0] >= 65 && chars[0] <= 65 + 26)
            chars[0] += 32;
        return String.valueOf(chars);
    }

    private StringUtil() {
    }
}
