package com.zhukai.spring.integration.web;


import com.zhukai.spring.integration.commons.annotation.Autowired;
import com.zhukai.spring.integration.commons.annotation.RequestBody;
import com.zhukai.spring.integration.commons.annotation.RequestMapping;
import com.zhukai.spring.integration.commons.annotation.RestController;
import com.zhukai.spring.integration.commons.constant.RequestType;
import com.zhukai.spring.integration.domain.RoleRepository;
import com.zhukai.spring.integration.domain.UserRepository;
import com.zhukai.spring.integration.domain.entity.RoleBean;
import com.zhukai.spring.integration.domain.entity.UserBean;
import com.zhukai.spring.integration.service.TestService;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhukai on 17-1-12.
 */
@RestController
@RequestMapping("/home")
public class HelloController {

    @Autowired
    TestService testService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @RequestMapping(value = "/hello")

    public List<UserBean> hello() throws SQLException {

        RoleBean roleBean = roleRepository.findOne(1);
        UserBean userBean = new UserBean();
        userBean.setRole(roleBean);
        userBean.setUsername("zhukai");
        userBean.setPassword("123");
        userRepository.save(userBean);
        return userRepository.findByUsernameAndPassword("zhukai", "123");
    }

    @RequestMapping(value = "/test", method = RequestType.POST)
    public RoleBean testRequestBody(@RequestBody RoleBean roleBean) {
        return roleBean;
    }
}
