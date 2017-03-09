package com.zhukai.framework.spring.integration.annotation.core;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    String value();

    String fileName() default "";
}
