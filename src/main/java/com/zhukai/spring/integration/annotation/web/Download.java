package com.zhukai.spring.integration.annotation.web;

import java.lang.annotation.*;

/**
 * Created by zhukai on 17-1-12.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Download {
    boolean attachment() default false;// 强制弹出下载对话框
}
