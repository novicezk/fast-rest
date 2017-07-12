package com.zhukai.framework.spring.integration.annotation.core;

import com.zhukai.framework.spring.integration.constant.IntegrationConstants;

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
