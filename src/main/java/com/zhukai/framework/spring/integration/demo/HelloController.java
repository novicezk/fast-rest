package com.zhukai.framework.spring.integration.demo;

import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;

/**
 * Created by zhukai on 17-3-8.
 */
@RestController
public class HelloController {
    @RequestMapping("/hello")
    public String hello() {
        return "hello,world";
    }
}
