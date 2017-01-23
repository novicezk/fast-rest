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
import java.util.Arrays;

/**
 * Created by zhukai on 17-1-22.
 */
public class DBUtil {

    public static void save(Object object) {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        String tableName = JpaUtil.getTableName(object.getClass());
        sql.append(tableName);
        StringBuilder cloumns = new StringBuilder("(");
        StringBuilder values = new StringBuilder("(");
        for (Field field : object.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(GeneratedValue.class)) {
                continue;
            }
            String columnName = JpaUtil.getColumnName(field);
            cloumns.append(columnName).append(",");

            if (JpaUtil.getSqlType(field.getType()).equals("VARCHAR")) {
                values.append("'").append(JpaUtil.convertToColumnValue(object, field)).append("'");
            } else {
                values.append(JpaUtil.convertToColumnValue(object, field));
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
        Field idField = Arrays.stream(clazz.getDeclaredFields()).filter(e -> e.isAnnotationPresent(Id.class)).findFirst().get();
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

    public static <T> T getBean(Class<T> clazz, Object id) throws SQLException {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM ").append(JpaUtil.getTableName(clazz)).append(" ");
        sql.append("WHERE ");
        Field idField = Arrays.stream(clazz.getDeclaredFields()).filter(e -> e.isAnnotationPresent(Id.class)).findFirst().get();
        String idFieldName = JpaUtil.getColumnName(idField);
        sql.append(idFieldName).append("=");
        if (JpaUtil.getSqlType(id.getClass()).equals("VARCHAR")) {
            sql.append("'").append(JpaUtil.convertToColumnValue(id)).append("'");
        } else {
            sql.append(JpaUtil.convertToColumnValue(id));
        }
        sql.append(";");
        ResultSet resultSet = executeQuery(sql.toString());
        Field[] fields = clazz.getDeclaredFields();
        if (resultSet.next()) {
            T object = ReflectUtil.createInstance(clazz, null);
            for (int i = 0; i < fields.length; i++) {
                Object columnValue = resultSet.getObject(i + 1);
                if (columnValue != null && fields[i].getType().isAnnotationPresent(Entity.class)) {
                    columnValue = getBean(fields[i].getType(), columnValue);
                }
                ReflectUtil.setFieldValue(object, fields[i].getName(), columnValue);
            }
            return object;
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
        Connection conn = DBConnectionPool.getConnection();
        executeSQL(conn, sql);
    }

    private static ResultSet executeQuery(String sql) throws SQLException {
        Connection conn = DBConnectionPool.getConnection();
        return conn.prepareStatement(sql).executeQuery();
    }

}
