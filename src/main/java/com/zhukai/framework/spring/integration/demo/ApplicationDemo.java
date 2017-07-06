package com.zhukai.framework.spring.integration.demo;


import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;

/**
 * Created by zhukai on 17-1-12.
 */
@RestController
public class ApplicationDemo {

    public static void main(String[] args) {
        SpringIntegration.run(ApplicationDemo.class);
    }

    @RequestMapping("/hello")
    public String hello() {
        return "hello,world";
    }

}
