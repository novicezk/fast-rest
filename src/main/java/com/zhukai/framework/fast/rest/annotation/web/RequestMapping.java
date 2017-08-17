package com.zhukai.framework.fast.rest.annotation.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zhukai.framework.fast.rest.common.RequestType;

@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {

	String[] method() default { RequestType.GET, RequestType.POST };

	String value();

}
