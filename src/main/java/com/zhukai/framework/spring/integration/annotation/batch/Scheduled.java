package com.zhukai.framework.spring.integration.annotation.batch;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * TODO corn
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    long fixedRate();

    long fixedDelay() default 0;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
