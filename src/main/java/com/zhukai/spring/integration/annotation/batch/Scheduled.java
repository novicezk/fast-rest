package com.zhukai.spring.integration.annotation.batch;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

    // String cron() default "";

    long fixedRate() default 200;//执行间隔（毫秒）

    long fixedDelay() default 0;//延迟执行时间（毫秒）

}
