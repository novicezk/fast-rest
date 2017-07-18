package com.zhukai.framework.fast.rest.annotation.web;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestHeader {
    String value();
}
