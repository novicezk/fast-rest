package com.zhukai.spring.integration.jdbc;

import com.zhukai.spring.integration.commons.annotation.Entity;
import com.zhukai.spring.integration.commons.annotation.GeneratedValue;
import com.zhukai.spring.integration.commons.utils.JpaUtil;
import com.zhukai.spring.integration.commons.utils.ReflectUtil;
import com.zhukai.spring.integration.commons.utils.StringUtil;
import com.zhukai.spring.integration.logger.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhukai on 17-1-22.
 */
public class MapperMethod<T> {
    private Connection conn;
    private Method method;
    private Object[] args;
    private ResultSet resultSet;
    private Class<T> entityClass;

    public MapperMethod(Method method, Object[] args, Class<T> entityClass, Connection conn) {
        this.entityClass = entityClass;
        this.method = method;
        this.args = args;
        this.conn = conn;
    }

    public Object execute() throws Exception {
        if (conn == null) {
            conn = DBConnectionPool.getConnection();
        }
        String methodName = method.getName();
        if (methodName.equals("findOne")) {
            return getBean(args[0]);
        } else if (methodName.equals("exists")) {
            return MapperMethod.exists(entityClass.getClass(), args[0]);
        } else if (methodName.equals("findAll")) {
            return MapperMethod.getBeans(entityClass.getClass(), null);
        } else if (methodName.equals("delete")) {
            MapperMethod.delete(entityClass.getClass(), args[0]);
            return null;
        } else if (methodName.equals("save")) {
            if (args[0] instanceof List) {
                return saveBeans((List) args[0]);
            } else {
                return saveBean(args[0]);
            }
        } else if (methodName.startsWith("findBy")) {
            StringBuilder propertiesSql = new StringBuilder();
            String propertiesString = methodName.substring(6);
            String[] arr = propertiesString.split("And|Or");
            for (int i = 0; i < arr.length; i++) {
                if (i == 0) {
                    propertiesSql.append(" WHERE ");
                }
                propertiesSql.append(JpaUtil.getColumnName(entityClass.getClass(), StringUtil.letterToLowerCase(arr[i])))
                        .append("=").append(JpaUtil.convertToColumnValue(args[i]));
                String afterString = propertiesString.substring(propertiesString.indexOf(arr[i]) + arr[i].length());
                if (afterString.startsWith("And")) {
                    propertiesSql.append(" AND ");
                } else if (afterString.startsWith("Or")) {
                    propertiesSql.append(" OR ");
                }
            }
            return MapperMethod.getEntityList(MapperMethod.getSelectSqlWithoutProperties(entityClass.getClass()).append(propertiesSql), entityClass.getClass());
        }
        //TODO
        return null;
    }

    private boolean saveBeans(List beans) {
        Connection conn = DBConnectionPool.getConnection();
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (Object bean : beans) {
            if (!saveBean(bean)) {
                return false;
            }
        }
        return true;
    }

    private boolean saveBean(Object bean) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        String tableName = JpaUtil.getTableName(bean.getClass());
        sql.append(tableName);
        StringBuilder cloumns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (Field field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(GeneratedValue.class)) {
                continue;
            }
            String columnName = JpaUtil.getColumnName(field);
            cloumns.append(columnName).append(",");

            if (JpaUtil.getSqlType(field.getType()).equals("VARCHAR")) {
                values.append("'").append(JpaUtil.getColumnValueByField(bean, field)).append("'");
            } else {
                values.append(JpaUtil.getColumnValueByField(bean, field));
            }
            values.append(",");
        }
        cloumns.deleteCharAt(cloumns.length() - 1);
        cloumns.append(")");
        values.deleteCharAt(values.length() - 1);
        values.append(")");
        sql.append(cloumns).append(" VALUES ").append(values).append(";");
        Logger.info(sql);
        return executeSQL(sql.toString());
    }

    public void delete(Class clazz, Object id) {
        StringBuilder sql = new StringBuilder();
        sql.append("DELETE FROM ").append(JpaUtil.getTableName(clazz)).append(" ");
        sql.append("WHERE ");
        Field idField = JpaUtil.getIdField(clazz);
        String idFieldName = JpaUtil.getColumnName(idField);
        sql.append(idFieldName).append("=");
        if (JpaUtil.getSqlType(id.getClass()).equals("VARCHAR")) {
            sql.append("'").append(JpaUtil.convertToColumnValue(id)).append("'");
        } else {
            sql.append(JpaUtil.convertToColumnValue(id));
        }
        sql.append(";");
        Logger.info(sql);
        executeSQL(sql.toString());
    }


    public <T, ID> T getBean(ID id) throws Exception {
        Field idField = JpaUtil.getIdField(entityClass);
        String idName = JpaUtil.getColumnName(idField);
        List<T> beans = getBeans(new Object[]{idName, id});
        if (beans != null && !beans.isEmpty()) {
            return beans.get(0);
        }
        return null;
    }

    private boolean executeSQL(String sql) {
        Connection conn = DBConnectionPool.getConnection();
        return executeSQL(conn, sql);
    }

    private boolean executeSQL(Connection conn, String sql) {
        try {
            System.out.println(sql);
            return conn.prepareStatement(sql.toString()).execute();
        } catch (SQLException e) {
            Logger.error();
            e.printStackTrace();
            return false;
        } finally {
            DBConnectionPool.freeConnection(conn);
        }
    }

    private ResultSet executeQuery(String sql) throws SQLException {
        System.out.println(sql);
        Connection conn = DBConnectionPool.getConnection();
        return conn.prepareStatement(sql).executeQuery();
    }

    public <T, ID> boolean exists(Class<T> clazz, ID id) throws Exception {
        Field idField = JpaUtil.getIdField(clazz);
        String idName = JpaUtil.getColumnName(idField);
        ResultSet resultSet = getSelectResultSet(clazz, new Object[]{idName, id});
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    public <T> List<T> getBeans(Object[] properties) throws Exception {
        return getEntityList(getSelectResultSet(properties), entityClass);
    }

    private <T> ResultSet getSelectResultSet(Object[] properties) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(getSelectSqlWithoutProperties(entityClass));
        if (properties != null) {
            sql.append(" WHERE ");
            for (int i = 0; i < properties.length; i += 2) {
                String columnName = properties[i].toString();
                String[] arr = columnName.split("\\.");
                Class fieldClass = entityClass;
                String columnTableName = JpaUtil.getTableName(entityClass);
                for (int j = 0; j < arr.length - 1; j++) {
                    fieldClass = ReflectUtil.getDeclaredField(fieldClass, arr[i]).getType();
                    columnTableName = JpaUtil.getTableName(fieldClass);
                }
                sql.append(columnTableName).append(".").append(JpaUtil.getColumnName(fieldClass, arr[arr.length - 1]));
                sql.append("=").append(JpaUtil.convertToColumnValue(properties[i + 1]))
                        .append(" AND ");
            }
            sql.delete(sql.length() - 4, sql.length() - 1);
        }
        resultSet = executeQuery(sql.toString());
        return resultSet;
    }

    public <T> List<T> getEntityList(StringBuilder sql, Class<T> convertClazz) throws Exception {
        return getEntityList(executeQuery(sql.toString()), convertClazz);
    }


    private <T> List<T> getEntityList() throws Exception {
        List<T> entityList = new ArrayList();
        while (resultSet.next()) {
            entityList.add(convertToEntity(entityClass));
        }
        return entityList;
    }

    public StringBuilder getSelectSqlWithoutProperties() {
        return getSelectMainSql().append(getJoinSql());
    }

    private Object convertToEntity(Class convertClazz) throws Exception {
        String mainTableName = JpaUtil.getTableName(convertClazz);
        Object object = ReflectUtil.createInstance(convertClazz, null);
        for (Field field : convertClazz.getDeclaredFields()) {
            Object columnValue;
            if (field.getType().isAnnotationPresent(Entity.class)) {
                columnValue = convertToEntity(field.getType());
            } else {
                columnValue = resultSet.getObject(mainTableName + "." + JpaUtil.getColumnName(field));
            }
            ReflectUtil.setFieldValue(object, field.getName(), columnValue);
        }
        return object;
    }

    private StringBuilder getSelectMainSql() {
        StringBuilder sql = new StringBuilder();
        String mainTableName = JpaUtil.getTableName(entityClass);
        sql.append("SELECT * FROM ").append(mainTableName).append(" ");
        return sql;
    }

    private StringBuilder getJoinSql() {
        return getJoinSql(entityClass);
    }

    private StringBuilder getJoinSql(Class clazz) {
        StringBuilder sql = new StringBuilder();
        List<Field> joinFields = JpaUtil.getJoinFields(clazz);
        if (joinFields == null || joinFields.isEmpty()) {
            return sql;
        }
        String mainTableName = JpaUtil.getTableName(clazz);
        for (Field joinField : joinFields) {
            String joinTableName = JpaUtil.getTableName(joinField.getType());
            String foreignKeyName = JpaUtil.getColumnName(joinField);
            sql.append(" JOIN ").append(joinTableName)
                    .append(" ON ").append(mainTableName).append(".")
                    .append(foreignKeyName).append("=").append(joinTableName)
                    .append(".").append(JpaUtil.getColumnName(JpaUtil.getIdField(joinField.getType())))
                    .append(" ");
            sql.append(getJoinSql(joinField.getType()));
        }
        return sql;
    }
}
