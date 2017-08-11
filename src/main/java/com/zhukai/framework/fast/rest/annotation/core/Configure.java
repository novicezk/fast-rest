package com.zhukai.framework.fast.rest.annotation.core;

import com.zhukai.framework.fast.rest.Constants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Singleton
public @interface Configure {

    String value() default "";

    String prefix() default "";

    String properties() default Constants.DEFAULT_PROPERTIES;
}
