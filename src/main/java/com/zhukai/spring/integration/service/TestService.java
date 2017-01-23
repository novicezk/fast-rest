package com.zhukai.spring.integration.service;


import com.zhukai.spring.integration.commons.annotation.Service;

/**
 * Created by zhukai on 17-1-16.
 */
@Service("test")
public class TestService {
    public String test() {
        return "test";
    }
}
