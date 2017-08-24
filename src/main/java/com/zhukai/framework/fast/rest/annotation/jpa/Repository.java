package com.zhukai.framework.fast.rest.annotation.jpa;

import com.zhukai.framework.fast.rest.annotation.core.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface Repository {
	String value() default "";
}
