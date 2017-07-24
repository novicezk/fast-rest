package com.zhukai.framework.fast.rest;

import sun.security.action.GetPropertyAction;

import java.security.AccessController;

public interface Constants {
    String DEFAULT_PROPERTIES = "application.properties";
    String MIMETYPE_PROPERTIES = "mimetype.properties";
    String JSESSIONID = "JSESSIONID";
    int BUFFER_SIZE = 1024;
    String LINE_SEPARATOR = AccessController.doPrivileged(new GetPropertyAction("line.separator"));
}
