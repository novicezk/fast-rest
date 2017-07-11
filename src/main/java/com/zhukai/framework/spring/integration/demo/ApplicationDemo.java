package com.zhukai.framework.spring.integration.demo;

import com.zhukai.framework.spring.integration.SpringIntegration;
import com.zhukai.framework.spring.integration.annotation.web.RequestMapping;
import com.zhukai.framework.spring.integration.annotation.web.RestController;

/**
 * Created by homolo on 17-7-11.
 */
@RestController
public class ApplicationDemo {
    public static void main(String[] args) {
        SpringIntegration.run(ApplicationDemo.class);
    }

    @RequestMapping("/hello")
    public void hello() {
        System.out.println("hello");
    }
}
