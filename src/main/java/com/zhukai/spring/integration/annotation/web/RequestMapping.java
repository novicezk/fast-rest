package com.zhukai.spring.integration.annotation.web;

import com.zhukai.spring.integration.common.constant.RequestType;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {

    String[] method() default {RequestType.GET, RequestType.POST};

    String value() default "";

}
