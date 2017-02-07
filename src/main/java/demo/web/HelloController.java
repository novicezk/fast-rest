package demo.web;


import com.zhukai.spring.integration.commons.annotation.*;
import com.zhukai.spring.integration.commons.constant.RequestType;
import demo.domain.RoleRepository;
import demo.domain.UserRepository;
import demo.domain.entity.RoleBean;
import demo.domain.entity.UserBean;
import demo.service.TestService;

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

    @Transactional
    public List<UserBean> hello() throws SQLException {
        roleRepository.delete(3);
        userRepository.delete(2);
//        RoleBean roleBean = roleRepository.findOne(1);
//        UserBean userBean = new UserBean();
//        userBean.setRole(roleBean);
//        userBean.setUsername("zhangsan");
//        userBean.setPassword("123");
//        userRepository.save(userBean);
        return userRepository.findByUsernameAndPassword("zhukai", "123");
    }

    @RequestMapping(value = "/test", method = RequestType.POST)
    public RoleBean testRequestBody(@RequestBody RoleBean roleBean) {
        return roleBean;
    }
}
