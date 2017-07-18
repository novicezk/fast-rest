package com.zhukai.framework.fast.rest.annotation.jpa;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    String name() default "";
    Index[] indexes() default {};
}
