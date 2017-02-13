package com.zhukai.spring.integration.commons.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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

    //size为0时，读取一行
    public static String readInputStreamLimitSize(InputStream inputStream, int size) throws IOException {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            int total = 0;
            if (size != 0) {
                while (total < size) {
                    out.write(inputStream.read());
                    total++;
                }
            } else {
                int i;
                while ((i = inputStream.read()) != 10 && i != -1) {
                    out.write(i);
                }
            }

            String result = new String(out.toByteArray(), "utf-8");
            if (result.length() > 1 && result.charAt(result.length() - 1) == 13) {
                result = result.substring(0, result.length() - 1);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null)
                out.close();
        }
    }

    public static String readLine(InputStream inputStream) throws IOException {
        return readInputStreamLimitSize(inputStream, 0);
    }

}
