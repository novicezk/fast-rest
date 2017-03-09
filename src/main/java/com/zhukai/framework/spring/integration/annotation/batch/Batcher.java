package com.zhukai.framework.spring.integration.annotation.batch;

import com.zhukai.framework.spring.integration.annotation.core.Component;
import com.zhukai.framework.spring.integration.annotation.core.Singleton;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
@Singleton
public @interface Batcher {
    String value() default "";
}
