package com.zhukai.framework.fast.rest.annotation.aop;

import com.zhukai.framework.fast.rest.annotation.core.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Aspect {
	String value() default "";
}
