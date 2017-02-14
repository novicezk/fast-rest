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

    @QueryCondition("ROLE.ID IS NULL OR ROLE.LEVEL != ?")
    List<UserBean> getAllUsersExcludeAdmin(Integer adminLevel);

    @ExecuteUpdate("UPDATE USER SET MONEY=0")
    boolean resetMoney();

}
