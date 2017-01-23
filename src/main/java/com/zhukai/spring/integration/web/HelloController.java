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
//        Connection conn = DBConnectionPool.getConnection();
//        try {
//            PreparedStatement statement = conn.prepareStatement("SELECT * from user");
//            ResultSet resultSet = statement.executeQuery();
//            while (resultSet.next()) {
//                System.out.println(resultSet.getString("login_name"));
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        singletonTest.test();
//        RoleBean roleBean = new RoleBean();
//        roleBean.setId(2);
//        roleBean.setRoleName("Tester");
//        roleBean.setLevel(2);
//        UserBean userBean = new UserBean();
//        userBean.setRole(roleBean);
//        userBean.setUsername("zhukai");
//        userBean.setPassword("123");
//        DBUtil.save(userBean);
        //DBUtil.delete(UserBean.class, 1);
        //    UserBean roleBean = DBUtil.getBean(UserBean.class, 2);
        userRepository.findOne(23);
        // return roleBean == null ? "null" : roleBean.toString();
        return "";
    }

}
