package com.zhukai.framework.fast.rest.annotation.extend;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

	long fixedRate() default -1;

	long fixedDelay() default -1;

	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

	String cron() default "";

}
