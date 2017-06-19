package com.zhukai.framework.spring.integration.annotation.core;

import com.zhukai.framework.spring.integration.SpringIntegration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Configure {
    String value() default "";

    String prefix() default "";

    String properties() default SpringIntegration.DEFAULT_PROPERTIES;
}
