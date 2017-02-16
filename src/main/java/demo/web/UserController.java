package demo.web;

import com.zhukai.spring.integration.common.HttpRequest;
import com.zhukai.spring.integration.common.Session;
import com.zhukai.spring.integration.annotation.core.Autowired;
import com.zhukai.spring.integration.annotation.web.*;
import com.zhukai.spring.integration.common.constant.RequestType;
import demo.common.Message;
import demo.common.MessageBuilder;
import demo.common.MessageCode;
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

    @RequestMapping(value = "/login", method = RequestType.POST)
    public Message login(@RequestAttribute("username") String username, @RequestAttribute("password") String password, HttpRequest request) {
        UserBean userBean = userRepository.findByUsernameAndPassword(username, password);
        if (userBean != null) {
            request.getSession().setAttribute("loginName", userBean.getUsername());
            return MessageBuilder.build(MessageCode.SUCCESS);
        } else {
            return MessageBuilder.build(MessageCode.LOGIN_ERROR);
        }
    }

    @RequestMapping("/checkUsername")
    public boolean checkUsername(@RequestParam("username") String username) {
        return !userRepository.exists(new Object[]{"username", username});
    }

    @RequestMapping(value = "/register", method = RequestType.POST)
    public Message register(@RequestBody UserBean userBean) {
        if (userRepository.save(userBean)) {
            return MessageBuilder.build(MessageCode.SUCCESS, "注册成功");
        }
        return MessageBuilder.build(MessageCode.DB_ERROR);
    }

    @RequestMapping("/resetMoney")
    public Boolean resetAllUserMoney() {
        return userRepository.resetMoney();
    }

    @RequestMapping("/getAll")
    public List<UserBean> getAllUsersExcludeAdmin() {
        return userRepository.getAllUsersExcludeAdmin(0);
    }

    @RequestMapping("/getRoleUser")
    public List<UserBean> getRoleUsers(@RequestParam("roleName") String roleName) {
        return userRepository.findAll(new Object[]{"role.roleName", roleName});
    }

    @RequestMapping("/getLoginName")
    public String getLoginName(Session session) {
        return (String) session.getAttribute("loginName");
    }

    @RequestMapping("/delete")
    public void getLoginName(@RequestParam("id") Integer id) {
        userRepository.delete(id);
    }
}
