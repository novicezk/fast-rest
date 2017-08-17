package com.zhukai.framework.fast.rest.annotation.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

	String name() default "";

	int length() default 255;

	boolean unique() default false;

	boolean nullable() default true;

}
