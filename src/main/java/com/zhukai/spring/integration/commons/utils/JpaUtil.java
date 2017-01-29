package com.zhukai.spring.integration.commons.utils;

import com.zhukai.spring.integration.commons.annotation.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhukai on 17-1-19.
 */
public class JpaUtil {
    public static String convertToSqlColumn(Field field) {
        StringBuilder sqlColumn = new StringBuilder();

        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            String columnName = getColumnName(field);
            sqlColumn.append(columnName).append(" ");
            String columnType = getSqlType(field.getType());
            sqlColumn.append(columnType);
            if (columnType.equals("VARCHAR")) {
                sqlColumn.append("(").append(column.length()).append(")");
            }
            if (!column.nullable()) {
                sqlColumn.append(" NOT NULL ");
            }
            if (column.unique()) {
                sqlColumn.append(" UNIQUE ");
            }
        } else {
            sqlColumn.append(field.getName()).append(" ");
            String columnType = getSqlType(field.getType());
            sqlColumn.append(columnType);
            if (columnType.equals("VARCHAR")) {
                sqlColumn.append("(").append(255).append(")");
            }
        }

        if (field.isAnnotationPresent(Id.class)) {
            sqlColumn.append(" PRIMARY KEY ");
        }
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            sqlColumn.append(" AUTO_INCREMENT ");
        }
        sqlColumn.append(",");

        if (field.getType().isAnnotationPresent(Entity.class)) {
            sqlColumn.append("FOREIGN KEY(").append(field.getName())
                    .append(") REFERENCES ");
            String tableName = getTableName(field.getType());
            Field idField = Arrays.stream(field.getType().getDeclaredFields()).filter(e -> e.isAnnotationPresent(Id.class)).findFirst().get();
            String idFieldName = getColumnName(idField);
            sqlColumn.append(tableName).append("(")
                    .append(idFieldName).append(")").append(",");
        }

        return sqlColumn.toString();
    }

    public static String getSqlType(Class typeClass) {
        if (typeClass.isAssignableFrom(Integer.class)) {
            return "INTEGER";
        } else if (typeClass.isAssignableFrom(Long.class)) {
            return "BIGINT";
        } else if (typeClass.isAssignableFrom(String.class)) {
            return "VARCHAR";
        } else if (typeClass.isAssignableFrom(Double.class)) {
            return "DOUBLE";
        } else if (typeClass.isAssignableFrom(Float.class)) {
            return "REAL";
        } else if (typeClass.isAnnotationPresent(Entity.class)) {
            Field idField = getIdField(typeClass);
            return getSqlType(idField.getType());
        }
        return null;
    }


    public static Object getColumnValueByField(Object obj, Field field) {
        Object fieldValue = ReflectUtil.getFieldValue(obj, field.getName());
        if (fieldValue.getClass().isAnnotationPresent(Entity.class)) {
            Field idField = getIdField(fieldValue.getClass());
            return getColumnValueByField(fieldValue, idField);
        } else {
            return fieldValue;
        }
    }

    public static Object convertToColumnValue(Object obj) {
        if (!obj.getClass().isAnnotationPresent(Entity.class)) {
            return obj;
        }
        Field idField = getIdField(obj.getClass());
        return getColumnValueByField(obj, idField);
    }

    public static Field getIdField(Class clazz) {
        return Arrays.stream(clazz.getDeclaredFields()).filter(e -> e.isAnnotationPresent(Id.class)).findFirst().get();
    }

    public static String getTableName(Class clazz) {
        String tableName = "";
        if (clazz.isAnnotationPresent(Entity.class)) {
            tableName = ((Entity) clazz.getAnnotation(Entity.class)).name();
        }
        tableName = tableName.equals("") ? clazz.getSimpleName().toLowerCase() : tableName;
        return tableName;
    }

    public static String getColumnName(Field field) {
        String columnName = "";
        if (field.isAnnotationPresent(Column.class)) {
            columnName = field.getAnnotation(Column.class).name();
        }
        columnName = columnName.equals("") ? field.getName() : columnName;
        return columnName;
    }

    public static List<Field> getJoinFields(Class clazz) {
        List<Field> joinFields = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getType().isAnnotationPresent(Entity.class)) {
                joinFields.add(field);
            }
        }
        return joinFields;
    }

}
