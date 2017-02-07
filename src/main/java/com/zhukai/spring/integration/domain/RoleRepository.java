package com.zhukai.spring.integration.domain;

import com.zhukai.spring.integration.commons.annotation.Repository;
import com.zhukai.spring.integration.domain.entity.RoleBean;
import com.zhukai.spring.integration.domain.entity.UserBean;

import java.util.List;

/**
 * Created by zhukai on 17-1-22.
 */
@Repository
public interface RoleRepository extends CrudRepository<RoleBean, Integer> {

}
