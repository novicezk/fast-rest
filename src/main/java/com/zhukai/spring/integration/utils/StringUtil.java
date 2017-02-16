package com.zhukai.spring.integration.utils;

import com.zhukai.spring.integration.server.SpringIntegration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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

    public static String readLine(SocketChannel channel, ByteBuffer buf) throws IOException {
        return readInputStreamLimitSize(channel, buf, 0);
    }

    public static String readInputStreamLimitSize(SocketChannel channel, ByteBuffer buf, int size) throws IOException {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            int total = 0;
            if (size != 0) {
                while (channel.read(buf) != -1 && total < size) {
                    buf.flip();
                    while (buf.hasRemaining() && total < size) {
                        out.write(buf.get());
                        total++;
                    }
                    buf.compact();
                }
            } else {
                int i = 0;
                while (channel.read(buf) != -1 && i != 10) {
                    buf.flip();
                    while (buf.hasRemaining() && (i = buf.get()) != 10) {
                        out.write(i);
                    }
                    buf.compact();
                }
            }
            String result = new String(out.toByteArray(), SpringIntegration.CHARSET);
            if (result.length() > 0 && result.charAt(result.length() - 1) == 13) {
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


}
