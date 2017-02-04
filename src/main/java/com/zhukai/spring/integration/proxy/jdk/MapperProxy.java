package com.zhukai.spring.integration.proxy.jdk;

import com.zhukai.spring.integration.commons.utils.JpaUtil;
import com.zhukai.spring.integration.commons.utils.StringUtil;
import com.zhukai.spring.integration.jdbc.DBUtil;

import java.lang.reflect.*;

/**
 * Created by zhukai on 17-1-22.
 */
//针对@Repository注解
public class MapperProxy implements InvocationHandler {

    private Class mapperInterface;

    public <T> T getProxyInstance(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(),
                new Class[]{mapperInterface}, this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Type[] actualTypes = ((ParameterizedType) mapperInterface.getGenericInterfaces()[0]).getActualTypeArguments();
        Class entityClass = (Class) actualTypes[0];
        String methodName = method.getName();
        if (methodName.equals("findOne")) {
            return DBUtil.getBean(entityClass, args[0]);
        } else if (methodName.equals("exists")) {
            return DBUtil.exists(entityClass, args[0]);
        } else if (methodName.equals("findAll")) {
            return DBUtil.getBeans(entityClass, null);
        } else if (methodName.equals("delete")) {
            DBUtil.delete(entityClass, args[0]);
            return null;
        } else if (methodName.equals("save")) {
            DBUtil.save(args[0]);
            return null;
        } else if (methodName.startsWith("findBy")) {
            StringBuilder propertiesSql = new StringBuilder();
            String propertiesString = methodName.substring(6);
            String[] arr = propertiesString.split("And|Or");
            for (int i = 0; i < arr.length; i++) {
                if (i == 0) {
                    propertiesSql.append(" WHERE ");
                }
                propertiesSql.append(JpaUtil.getColumnName(entityClass, StringUtil.letterToLowerCase(arr[i])))
                        .append("=").append(JpaUtil.convertToColumnValue(args[i]));
                String afterString = propertiesString.substring(propertiesString.indexOf(arr[i]) + arr[i].length());
                if (afterString.startsWith("And")) {
                    propertiesSql.append(" AND ");
                } else if (afterString.startsWith("Or")) {
                    propertiesSql.append(" OR ");
                }
            }
            return DBUtil.getEntityList(DBUtil.getSelectSqlWithoutProperties(entityClass).append(propertiesSql), entityClass);
        }
        //TODO
        return null;
    }
}
