package demo.domain;

import com.zhukai.spring.integration.annotation.jpa.ExecuteUpdate;
import com.zhukai.spring.integration.annotation.jpa.QueryCondition;
import com.zhukai.spring.integration.annotation.core.Repository;
import demo.domain.entity.UserBean;
import com.zhukai.spring.integration.jdbc.CrudRepository;

import java.util.List;

/**
 * Created by zhukai on 17-1-22.
 */
@Repository
public interface UserRepository extends CrudRepository<UserBean, Integer> {

    UserBean findByUsernameAndPassword(String username, String password);

    UserBean findByUsername(String username);

    @QueryCondition("rolebean.id IS NULL OR rolebean.level != ?")
    List<UserBean> getAllUsersExcludeAdmin(Integer adminLevel);

    @ExecuteUpdate("UPDATE userbean SET money=0")
    boolean resetMoney();

}
