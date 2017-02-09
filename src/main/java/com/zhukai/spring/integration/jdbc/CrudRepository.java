package com.zhukai.spring.integration.jdbc;


import java.io.Serializable;
import java.util.List;

/**
 * Created by zhukai on 17-1-22.
 */
public interface CrudRepository<T, ID extends Serializable> {

    boolean save(T var1);

    boolean save(List<T> var1);

    T findOne(ID var1);

    boolean exists(ID var1);

    List<T> findAll();

    List<T> findAll(Object[] properties);

    List<T> findAll(List<ID> ids);

    //long count();

    void delete(ID var1);

    void delete(T var1);

    //void delete(List<? extends T> var1);

    //void deleteAll();
}
