package com.zhukai.framework.fast.rest.annotation.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

	String name() default "";

	String[] columns();

	boolean unique() default false;

	boolean isFull() default false;

}
