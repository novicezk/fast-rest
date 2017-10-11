package com.zhukai.framework.fast.rest.annotation.extend;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * TODO cron
 */
@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Scheduled {

	long fixedRate();

	long fixedDelay() default -1;

	TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
