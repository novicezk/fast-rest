package com.zhukai.spring.integration.annotation.jpa;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {

    String name() default "";

    int length() default 255;

    boolean unique() default false;

    boolean nullable() default true;

}
