package com.zhukai.framework.spring.integration.annotation.jpa;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String name() default "";

    String[] columns();

    boolean unique() default false;

    boolean isFull() default false;

}
