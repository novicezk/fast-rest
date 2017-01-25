package com.zhukai.spring.integration.web;


import com.zhukai.spring.integration.commons.annotation.Autowired;
import com.zhukai.spring.integration.commons.annotation.RequestMapping;
import com.zhukai.spring.integration.commons.annotation.RestController;
import com.zhukai.spring.integration.domain.UserRepository;
import com.zhukai.spring.integration.service.TestService;

import java.sql.SQLException;

/**
 * Created by zhukai on 17-1-12.
 */
@RestController
@RequestMapping("/home")
public class HelloController {

    @Autowired("test")
    TestService testService;

    @Autowired
    UserRepository userRepository;

    @RequestMapping(value = "/hello")

    public String hello() throws SQLException {
        userRepository.findOne(23);
        return "";
    }

}
