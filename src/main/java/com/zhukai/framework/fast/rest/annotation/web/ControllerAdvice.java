package com.zhukai.framework.fast.rest.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zhukai.framework.fast.rest.annotation.core.Component;
import com.zhukai.framework.fast.rest.annotation.core.Singleton;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
@Singleton
public @interface ControllerAdvice {
	String value() default "";
}
