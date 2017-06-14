package com.zhukai.framework.spring.integration.annotation.web;

import com.zhukai.framework.spring.integration.annotation.core.Component;
import com.zhukai.framework.spring.integration.annotation.core.Singleton;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Singleton
public @interface RestController {
    String value() default "";
}
