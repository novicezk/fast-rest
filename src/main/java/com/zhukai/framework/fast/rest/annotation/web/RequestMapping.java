package com.zhukai.framework.fast.rest.annotation.web;

import com.zhukai.framework.fast.rest.common.RequestType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

    String[] method() default {RequestType.GET, RequestType.POST};

    String value();

}
