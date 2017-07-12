package com.zhukai.framework.spring.integration.annotation.web;

import com.zhukai.framework.spring.integration.constant.RequestType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String[] method() default {RequestType.GET, RequestType.POST};

    String value();

}
