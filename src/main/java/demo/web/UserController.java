package demo.web;

import com.zhukai.spring.integration.commons.Request;
import com.zhukai.spring.integration.commons.Session;
import com.zhukai.spring.integration.commons.annotation.*;
import com.zhukai.spring.integration.commons.constant.RequestType;
import com.zhukai.spring.integration.commons.response.Response;
import com.zhukai.spring.integration.commons.response.ResponseBuilder;
import com.zhukai.spring.integration.commons.response.ResponseCodeEnums;
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

    @RequestMapping(value = "/login", method = RequestType.POST)
    public Response login(@RequestAttribute("username") String username, @RequestAttribute("password") String password) {
        List<UserBean> userBeans = userRepository.findByUsernameAndPassword(username, password);
        if (userBeans != null && !userBeans.isEmpty()) {
            WebContext.getSession().setAttribute("loginUser", userBeans.get(0));
            System.out.println(WebContext.getSession().getSessionId() + "----login");
            return ResponseBuilder.build(ResponseCodeEnums.SUCCESS);
        } else {
            return ResponseBuilder.build(ResponseCodeEnums.LOGIN_ERROR);
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

    @RequestMapping("/getRoleUser")
    public List<UserBean> getAllAdminUsers(@RequestParam("roleName") String roleName) {
        return userRepository.findAll(new Object[]{"role.roleName", roleName});
    }

    @RequestMapping("/getLoginName")
    public String getLoginName(Request request) {
        System.out.println(request.getPath());
        Session session = WebContext.getSession();
        System.out.println(session.getSessionId() + "----home");
        UserBean loginUser = (UserBean) session.getAttribute("loginUser");
        return loginUser.getUsername();
    }

}
