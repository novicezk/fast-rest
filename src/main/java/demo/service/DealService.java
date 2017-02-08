package demo.service;


import com.zhukai.spring.integration.commons.annotation.Autowired;
import com.zhukai.spring.integration.commons.annotation.Service;
import com.zhukai.spring.integration.commons.annotation.Transactional;
import demo.domain.FoodRepository;
import demo.domain.RoleRepository;
import demo.domain.UserRepository;
import demo.domain.entity.FoodBean;
import demo.domain.entity.RoleBean;
import demo.domain.entity.UserBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhukai on 17-1-16.
 */
@Service
public class DealService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    FoodRepository foodRepository;

    public boolean recharge(UserBean userBean, float money) {
        userBean.setMoney(userBean.getMoney() + money);
        return userRepository.save(userBean);
    }

    @Transactional
    public void init() {
        List<RoleBean> roleBeanList = new ArrayList<>();
        roleBeanList.add(new RoleBean("Admin", 0));
        roleBeanList.add(new RoleBean("Tester", 1));
        roleRepository.save(roleBeanList);
        List<FoodBean> foods = new ArrayList<>();
        foods.add(new FoodBean("apple", 12.0f, 8));
        foods.add(new FoodBean("orange", 41.0f, 18));
        foodRepository.save(foods);
    }

    @Transactional
    public String buyFood(UserBean userBean, String foodName, Integer number) {
        FoodBean foodBean = foodRepository.findOne(foodName);
        Float price = foodBean.getPrice() * number;
        if (userBean.getMoney() < price) {
            return "你的钱不够啦";
        } else if (foodBean.getQuantity() < number) {
            return "数量不足";
        } else {
            foodBean.setQuantity(foodBean.getQuantity() - number);
            foodRepository.save(foodBean);
            userBean.setMoney(userBean.getMoney() - price);
            userRepository.save(userBean);
            return "购买成功";
        }
    }
}
