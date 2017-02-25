package com.zhukai.spring.integration.jdbc;

import com.zhukai.spring.integration.annotation.jpa.ExecuteUpdate;
import com.zhukai.spring.integration.annotation.jpa.QueryCondition;
import com.zhukai.spring.integration.utils.ReflectUtil;
import com.zhukai.spring.integration.utils.StringUtil;
import com.zhukai.spring.integration.server.SpringIntegration;
import org.apache.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhukai on 17-1-22.
 */
public class MapperMethod<T> {
    private static Logger logger = Logger.getLogger(MapperMethod.class);

    private Connection conn;
    private Method method;
    private Object[] args;
    private ResultSet resultSet;
    private Class<T> entityClass;
    private boolean isTransactional = true;

    public MapperMethod(Method method, Object[] args, Class<T> entityClass, Connection conn) {
        this.entityClass = entityClass;
        this.method = method;
        this.args = args;
        this.conn = conn;
    }

    public void release() throws SQLException {
        if (conn != null && !isTransactional) {
            DBConnectionPool.getInstance().freeConnection(conn);
        }
        if (resultSet != null) {
            resultSet.close();
        }
    }

    public Object execute() throws Exception {
        if (conn == null) {
            isTransactional = false;
            conn = DBConnectionPool.getInstance().getConnection();
        }
        String methodName = method.getName();
        if (method.isAnnotationPresent(ExecuteUpdate.class)) {
            String sql = method.getAnnotation(ExecuteUpdate.class).value();
            return executeUpdate(sql, args);
        } else if (method.isAnnotationPresent(QueryCondition.class)) {
            String queryCondition = method.getAnnotation(QueryCondition.class).value();
            String sql = JpaUtil.getSelectSqlWithoutProperties(entityClass).append(" WHERE ").append(queryCondition).toString();
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return getEntityList(sql, args);
            } else {
                return getEntity(sql + " LIMIT 1 ", args);
            }
        } else if (methodName.equals("findOne")) {
            return getBean(args[0]);
        } else if (methodName.equals("exists")) {
            if (args[0] instanceof Object[]) {
                return existsByProperties((Object[]) args[0]);
            }
            return exists(args[0]);
        } else if (methodName.equals("findAll")) {
            if (args != null) {
                if (args[0] instanceof Object[]) {
                    return getBeans((Object[]) args[0]);
                } else if (args[0] instanceof List) {
                    return getBeansIn((List) args[0]);
                }
            }
            return getBeans(null);
        } else if (methodName.equals("delete")) {
            return delete(args[0]);
        } else if (methodName.equals("save")) {
            if (args[0] instanceof List) {
                return saveBeans((List<T>) args[0]);
            } else {
                return saveBean((T) args[0]);
            }
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
            String selectSQL = JpaUtil.getSelectSqlWithoutProperties(entityClass).append(propertiesSql).toString();
            if (List.class.isAssignableFrom(method.getReturnType())) {
                return getEntityList(selectSQL);
            } else {
                return getEntity(selectSQL + " LIMIT 1 ");
            }
        }
        //TODO
        return null;
    }

    private boolean saveBeans(List<T> beans) throws Exception {
        for (T bean : beans) {
            if (!saveBean(bean)) {
                return false;
            }
        }
        return true;
    }

    private boolean saveBean(T bean) throws Exception {
        Field idField = JpaUtil.getIdField(entityClass);
        Object id = ReflectUtil.getFieldValue(bean, idField.getName());
        String sql;
        if (!exists(id)) {
            sql = JpaUtil.getSaveSQL(bean);
        } else {
            sql = JpaUtil.getUpdateSQL(bean);
        }
        return executeUpdate(sql);
    }

    private <ID> boolean delete(ID id) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(JpaUtil.getTableName(entityClass)).append(" ");
        sql.append("WHERE ");
        Field idField = JpaUtil.getIdField(entityClass);
        String idFieldName = JpaUtil.getColumnName(idField);
        sql.append(idFieldName).append("=")
                .append(JpaUtil.convertToColumnValue(id));
        return executeUpdate(sql.toString());
    }


    private <ID> T getBean(ID id) throws Exception {
        Field idField = JpaUtil.getIdField(entityClass);
        String idName = JpaUtil.getColumnName(idField);
        List<T> beans = getBeans(new Object[]{idName, id});
        if (beans != null && !beans.isEmpty()) {
            return beans.get(0);
        }
        return null;
    }


    private <ID> boolean exists(ID ID) throws Exception {
        Field idField = JpaUtil.getIdField(entityClass);
        String sql = JpaUtil.getSelectSQL(entityClass, new Object[]{idField.getName(), ID});
        resultSet = executeQuery(sql);
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    private boolean existsByProperties(Object[] properties) throws Exception {
        String sql = JpaUtil.getSelectSQL(entityClass, properties);
        resultSet = executeQuery(sql + " LIMIT 1");
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    private List<T> getBeans(Object[] properties) throws Exception {
        String sql = JpaUtil.getSelectSQL(entityClass, properties);
        return getEntityList(sql);
    }

    private <ID> List<T> getBeansIn(List<ID> ids) throws Exception {
        List<T> beans = new ArrayList();
        for (ID id : ids) {
            beans.add(getBean(id));
        }
        return beans;
    }

    private T getEntity(String sql) throws Exception {
        return getEntity(sql, null);
    }

    private T getEntity(String sql, Object[] properties) throws Exception {
        List<T> entityList = getEntityList(sql, properties);
        if (entityList != null && !entityList.isEmpty()) {
            return entityList.get(0);
        }
        return null;
    }

    private List<T> getEntityList(String sql) throws Exception {
        return getEntityList(sql, null);
    }

    private List<T> getEntityList(String sql, Object[] properties) throws Exception {
        if (properties != null) {
            resultSet = executeQuery(sql, properties);
        } else {
            resultSet = executeQuery(sql);
        }
        List<T> entityList = new ArrayList();
        while (resultSet.next()) {
            entityList.add(JpaUtil.convertToEntity(entityClass, resultSet));
        }
        return entityList;
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        if (SpringIntegration.getServerConfig().isShowSQL()) {
            logger.info(sql);
        }
        return conn.prepareStatement(sql).executeQuery();
    }

    private ResultSet executeQuery(String sql, Object[] properties) throws SQLException {
        return fillStatement(sql, properties).executeQuery();
    }

    private boolean executeUpdate(String sql) throws SQLException {
        if (SpringIntegration.getServerConfig().isShowSQL()) {
            logger.info(sql);
        }
        return conn.prepareStatement(sql).executeUpdate() >= 1;
    }

    private boolean executeUpdate(String sql, Object[] properties) throws SQLException {
        return fillStatement(sql, properties).executeUpdate() >= 1;
    }

    private PreparedStatement fillStatement(String sql, Object[] properties) throws SQLException {
        if (SpringIntegration.getServerConfig().isShowSQL()) {
            logger.info(sql);
            logger.info("parameters：" + Arrays.toString(properties));
        }
        PreparedStatement statement = conn.prepareStatement(sql);
        for (int i = 0; i < properties.length; i++) {
            statement.setObject(i + 1, properties[i]);
        }
        return statement;
    }
}
