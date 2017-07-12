package com.zhukai.framework.spring.integration.annotation.web;

import com.zhukai.framework.spring.integration.annotation.core.Component;
import com.zhukai.framework.spring.integration.annotation.core.Singleton;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Singleton
public @interface ControllerAdvice {
    String value() default "";
}
