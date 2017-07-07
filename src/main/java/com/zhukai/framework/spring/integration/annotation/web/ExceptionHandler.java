package com.zhukai.framework.spring.integration.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExceptionHandler {

    Class<? extends Throwable>[] value();

    int catchSeq() default 0;
}
