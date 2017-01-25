package com.zhukai.spring.integration.web;


import com.zhukai.spring.integration.commons.annotation.RequestMapping;
import com.zhukai.spring.integration.commons.annotation.RequestParam;
import com.zhukai.spring.integration.commons.annotation.RestController;

/**
 * Created by zhukai on 17-1-12.
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @RequestMapping(value = "/param")
    public Integer testRequestParam(@RequestParam("age") Integer age) {
        return age;
    }
//TODO
//    @RequestMapping(value = "/path/{name}")
//    public String testPath(@PathVariable String name) {
//        return name;
//    }
}
