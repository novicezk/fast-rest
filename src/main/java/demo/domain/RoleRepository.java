package demo.domain;

import com.zhukai.spring.integration.commons.annotation.Repository;
import demo.domain.entity.RoleBean;
import com.zhukai.spring.integration.jdbc.CrudRepository;

/**
 * Created by zhukai on 17-1-22.
 */
@Repository
public interface RoleRepository extends CrudRepository<RoleBean, Integer> {

}
