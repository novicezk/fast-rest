package demo.domain;

import com.zhukai.spring.integration.commons.annotation.ExecuteUpdate;
import com.zhukai.spring.integration.commons.annotation.QueryCondition;
import com.zhukai.spring.integration.commons.annotation.Repository;
import demo.domain.entity.UserBean;
import com.zhukai.spring.integration.jdbc.CrudRepository;

import java.util.List;

/**
 * Created by zhukai on 17-1-22.
 */
@Repository
public interface UserRepository extends CrudRepository<UserBean, Integer> {

    List<UserBean> findByUsernameAndPassword(String username, String password);

    @QueryCondition("rolebean.id is null or rolebean.level != ?")
    List<UserBean> getAllUsersExcludeAdmin(Integer adminLevel);

    @ExecuteUpdate("UPDATE userbean SET money=? WHERE userbean.id=?")
    boolean recharge(float money, Integer userId);

}
