package com.zhukai.spring.integration.annotation.mvc;

import com.zhukai.spring.integration.annotation.core.Component;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestController {
    String value() default "";
}
