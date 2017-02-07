package com.zhukai.spring.integration.logger;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by zhukai on 17-1-17.
 */
public class Logger {

    private static DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");

    public static void info(Object object) {
        println("info", object);
    }

    public static void warn(Object object) {
        println("warn", object);
    }

    public static void error(Object object) {
        println("error", object);
    }

    public static void error() {
        StringBuffer value = getLocaleInfo("error");
        System.err.print(value.toString());
    }

    private static void println(String printType, Object object) {
        StringBuffer value = getLocaleInfo(printType);
        if (object == null) {
            value.append("null");
        } else {
            value.append(object.toString());
        }
        if (printType.equals("error")) {
            System.err.println(value.toString());
        } else {
            System.out.println(value.toString());
        }
    }

    private static StringBuffer getLocaleInfo(String printType) {
        StringBuffer value = new StringBuffer();
        value.append("[");
        value.append(Thread.currentThread().getName());
        value.append("]-");
        value.append(LocalDateTime.now().format(format));
        value.append("-");
        value.append(printType);
        value.append(": ");
        return value;
    }
}
