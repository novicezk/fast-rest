package demo.web;

import com.zhukai.spring.integration.commons.annotation.*;
import com.zhukai.spring.integration.commons.constant.RequestType;
import com.zhukai.spring.integration.context.WebContext;
import demo.domain.UserRepository;
import demo.domain.entity.UserBean;

import java.util.List;

/**
 * Created by zhukai on 17-2-8.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserRepository userRepository;

    @RequestMapping("/login")
    public boolean login(@RequestParam("username") String username, @RequestParam("password") String password) {
        List<UserBean> userBeans = userRepository.findByUsernameAndPassword(username, password);
        if (userBeans != null && !userBeans.isEmpty()) {
            WebContext.getSession().setAttribute("loginUser", userBeans.get(0));
            return true;
        } else {
            return false;
        }
    }

    @RequestMapping(value = "/register", method = RequestType.POST)
    public boolean register(@RequestBody UserBean userBean) {
        return userRepository.save(userBean);
    }

    @RequestMapping("/getAll")
    public List<UserBean> getAllUsersExcludeAdmin() {
        return userRepository.getAllUsersExcludeAdmin(0);
    }

}
