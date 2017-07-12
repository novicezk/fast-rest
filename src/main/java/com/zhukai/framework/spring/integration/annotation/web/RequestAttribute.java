package com.zhukai.framework.spring.integration.annotation.web;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestAttribute {
    String value();
}
