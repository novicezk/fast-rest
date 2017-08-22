package com.zhukai.framework.fast.rest.annotation.core;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Around {
	String[] packages() default {};

	Class[] classes() default {};

	Class<? extends Annotation>[] classAnnotations() default {};

	Class<? extends Annotation>[] methodAnnotations() default {};

	String[] methodNames() default {};

	int seq() default 0;
}
