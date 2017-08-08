package com.zhukai.framework.fast.rest.log;

public interface Log {

    void error(String message, Throwable t, Object... params);

    void error(String message);

    void info(String message, Object... params);

    void warn(String message, Object... params);

}
