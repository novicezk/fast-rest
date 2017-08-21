package com.zhukai.framework.fast.rest.annotation.web;

import com.zhukai.framework.fast.rest.annotation.core.Component;
import com.zhukai.framework.fast.rest.annotation.core.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Singleton
public @interface Aspect {
    String value() default "";
}
