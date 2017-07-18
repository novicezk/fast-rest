package com.zhukai.framework.fast.rest.jdbc.data.jpa;


import java.io.Serializable;
import java.util.List;

public interface CrudRepository<T, ID extends Serializable> {

    boolean save(T bean);

    boolean save(List<T> beanList);

    T findOne(ID id);

    boolean exists(ID id);

    boolean exists(T bean);

    boolean exists(Object[] properties);

    List<T> findAll();

    List<T> findAll(Object[] properties);

    List<T> findAll(List<ID> ids);

    long count();

    void delete(ID id);

    void delete(T bean);

}
