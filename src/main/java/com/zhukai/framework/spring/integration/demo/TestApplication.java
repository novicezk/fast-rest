package com.zhukai.framework.spring.integration.demo;


import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;
import com.zhukai.framework.spring.integration.server.SpringIntegration;

/**
 * Created by zhukai on 17-1-12.
 */
@RestController
public class TestApplication {

    public static void main(String[] args) {
        SpringIntegration.run(TestApplication.class);
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello,world";
    }
}
