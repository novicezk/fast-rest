package com.zhukai.framework.spring.integration.annotation.batch;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    // String cron() default "";

    long fixedRate();//执行间隔，单位timeUnit

    long fixedDelay() default 0;//延迟执行时间，单位timeUnit

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
