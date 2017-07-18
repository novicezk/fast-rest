package com.zhukai.framework.fast.rest.annotation.core;

import com.zhukai.framework.fast.rest.constant.IntegrationConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configure {

    String value() default "";

    String prefix() default "";

    String properties() default IntegrationConstants.DEFAULT_PROPERTIES;
}
