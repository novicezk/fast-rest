package com.zhukai.spring.integration.jdbc;

import com.zhukai.spring.integration.commons.annotation.Entity;
import com.zhukai.spring.integration.commons.annotation.GeneratedValue;
import com.zhukai.spring.integration.commons.annotation.Id;
import com.zhukai.spring.integration.commons.utils.JpaUtil;
import com.zhukai.spring.integration.commons.utils.ReflectUtil;
import com.zhukai.spring.integration.logger.Logger;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhukai on 17-1-22.
 */
public class DBUtil {

    public static void save(Object object) {
        if (object instanceof List) {
            List beans = (List) object;
            for (Object bean : beans) {
                saveBean(bean);
            }
        } else {
            saveBean(object);
        }

    }

    private static void saveBean(Object bean) {
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
        executeSQL(sql.toString());
    }

    public static void delete(Class clazz, Object id) {
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


    public static <T, ID> T getBean(Class<T> clazz, ID id) throws Exception {
        Field idField = JpaUtil.getIdField(clazz);
        String idName = JpaUtil.getColumnName(idField);
        List<T> beans = getBeans(clazz, new Object[]{idName, id});
        if (beans != null && !beans.isEmpty()) {
            return beans.get(0);
        }
        return null;
    }

    private static void executeSQL(Connection conn, String sql) {
        try {
            conn.prepareStatement(sql.toString()).execute();
        } catch (SQLException e) {
            Logger.error();
            e.printStackTrace();
        } finally {
            DBConnectionPool.freeConnection(conn);
        }
    }

    private static void executeSQL(String sql) {
        System.out.println(sql);
        Connection conn = DBConnectionPool.getConnection();
        executeSQL(conn, sql);
    }

    private static ResultSet executeQuery(String sql) throws SQLException {
        System.out.println(sql);
        Connection conn = DBConnectionPool.getConnection();
        return conn.prepareStatement(sql).executeQuery();
    }

    public static <T, ID> boolean exists(Class<T> clazz, ID id) throws Exception {
        Field idField = JpaUtil.getIdField(clazz);
        String idName = JpaUtil.getColumnName(idField);
        ResultSet resultSet = getSelectResultSet(clazz, new Object[]{idName, id});
        if (resultSet.next()) {
            return true;
        }
        return false;
    }

    public static <T> List<T> getBeans(Class<T> entityClass, Object[] properties) throws Exception {
        return getEntityList(getSelectResultSet(entityClass, properties), entityClass);
    }

    private static <T> ResultSet getSelectResultSet(Class<T> entityClass, Object[] properties) throws Exception {
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
                sql.append(columnTableName).append(".").append(arr[arr.length - 1]);
                sql.append("=").append(JpaUtil.convertToColumnValue(properties[i + 1]));
            }
        }
        ResultSet resultSet = executeQuery(sql.toString());
        return resultSet;
    }

    private static <T> List<T> getEntityList(ResultSet resultSet, Class<T> convertClazz) throws Exception {
        List<T> entityList = new ArrayList();
        while (resultSet.next()) {
            entityList.add(convertToEntity(resultSet, convertClazz));
        }
        return entityList;
    }

    private static StringBuilder getSelectSqlWithoutProperties(Class entityClass) {
        return getSelectMainSql(entityClass).append(getJoinSql(entityClass));
    }

    private static <T> T convertToEntity(ResultSet resultSet, Class<T> convertClazz) throws Exception {
        String mainTableName = JpaUtil.getTableName(convertClazz);
        T object = ReflectUtil.createInstance(convertClazz, null);
        for (Field field : convertClazz.getDeclaredFields()) {
            Object columnValue;
            if (field.getType().isAnnotationPresent(Entity.class)) {
                columnValue = convertToEntity(resultSet, field.getType());
            } else {
                columnValue = resultSet.getObject(mainTableName + "." + JpaUtil.getColumnName(field));
            }
            ReflectUtil.setFieldValue(object, field.getName(), columnValue);
        }
        return object;
    }

    private static StringBuilder getSelectMainSql(Class entityClass) {
        StringBuilder sql = new StringBuilder();
        String mainTableName = JpaUtil.getTableName(entityClass);
        sql.append("SELECT * FROM ").append(mainTableName).append(" ");
        return sql;
    }

    private static StringBuilder getJoinSql(Class entityClass) {
        StringBuilder sql = new StringBuilder();
        List<Field> joinFields = JpaUtil.getJoinFields(entityClass);
        if (joinFields == null || joinFields.isEmpty()) {
            return sql;
        }
        String mainTableName = JpaUtil.getTableName(entityClass);
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
