package demo.web;

import com.sun.org.apache.xpath.internal.operations.Bool;
import com.zhukai.spring.integration.commons.Session;
import com.zhukai.spring.integration.commons.annotation.Autowired;
import com.zhukai.spring.integration.commons.annotation.RequestMapping;
import com.zhukai.spring.integration.commons.annotation.RequestParam;
import com.zhukai.spring.integration.commons.annotation.RestController;
import demo.domain.entity.UserBean;
import demo.service.DealService;

/**
 * Created by zhukai on 17-2-8.
 */
@RestController
@RequestMapping("/deal")
public class DealController {

    @Autowired
    DealService dealService;

    @RequestMapping("/recharge")
    public Boolean recharge(@RequestParam("money") Float money, Session session) {
        UserBean loginUser = (UserBean) session.getAttribute("loginUser");
        return dealService.recharge(loginUser, money);
    }

    @RequestMapping("/buyFood")
    public String buyFood(@RequestParam("foodName") String foodName, @RequestParam("number") Integer number, Session session) {
        UserBean loginUser = (UserBean) session.getAttribute("loginUser");
        return dealService.buyFood(loginUser, foodName, number);
    }

    @RequestMapping("/init")
    public void init() {
        dealService.init();
    }

}
