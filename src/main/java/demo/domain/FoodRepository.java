package demo.domain;

import com.zhukai.spring.integration.commons.annotation.Repository;
import com.zhukai.spring.integration.jdbc.CrudRepository;
import demo.domain.entity.FoodBean;

/**
 * Created by zhukai on 17-1-22.
 */
@Repository
public interface FoodRepository extends CrudRepository<FoodBean, Integer> {
    FoodBean findByName(String name);
}
