package com.zhukai.spring.integration.utils;

import com.zhukai.spring.integration.annotation.core.Component;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhukai on 16-12-15.
 */
public class ReflectUtil {

    public static Object getFieldValue(Object object, String fieldName) {
        Field field = getDeclaredField(object.getClass(), fieldName);
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFieldValue(Object obj, String fieldName, Object value) {
        Field field = getDeclaredField(obj.getClass(), fieldName);
        field.setAccessible(true);
        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //根据fieldName获得entityClass的属性，也可以是private或父类继承的
    public static Field getDeclaredField(Class entityClass, String fieldName) {
        if (StringUtil.isBlank(fieldName) || entityClass == null) {
            return null;
        }
        try {
            return entityClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return getDeclaredField(entityClass.getSuperclass(), fieldName);
        }
    }


    public static List<String> getFieldNames(Class entityClass) {
        Field[] fields = entityClass.getDeclaredFields();
        List<String> fieldNames = new ArrayList();
        for (Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }


    public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramsClass, Object[] params) {
        Object returnValue = null;
        try {
            Method method = obj.getClass().getMethod(methodName, paramsClass);
            returnValue = method.invoke(obj, params);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public static Object invokeMethod(Object obj, String methodName) {
        return invokeMethod(obj, methodName, null, null);
    }

    public static <T> T createInstance(Class<T> objClass, Class<?>[] paramsClass, Object... params) {
        Object obj = null;
        try {
            Constructor constructor = objClass.getConstructor(paramsClass);
            obj = constructor.newInstance(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (T) obj;
    }

    public static boolean existAnnotation(Class objectClass, Class<? extends Annotation> annotationClass) {
        if (objectClass.equals(Target.class) || objectClass.equals(Retention.class) || objectClass.equals(Documented.class)) {
            return false;
        }
        if (objectClass.isAnnotationPresent(annotationClass)) {
            return true;
        } else {
            Annotation[] annotations = objectClass.getAnnotations();
            for (Annotation annotation : annotations) {
                if (existAnnotation(annotation.annotationType(), annotationClass)) {
                    return true;
                }
            }
        }
        return false;
    }

    //返回空表示没有使用@Component注解
    public static String getBeanRegisterName(Class objectClass) {
        if (objectClass.isAnnotation()) {
            return null;
        }
        if (objectClass.isAnnotationPresent(Component.class)) {
            return invokeMethod(objectClass.getAnnotation(Component.class), "value").toString();
        }
        Annotation[] annotations = objectClass.getAnnotations();
        for (Annotation annotation : annotations) {
            if (existAnnotation(annotation.annotationType(), Component.class)) {
                return invokeMethod(annotation, "value").toString();
            }
        }
        return null;
    }

}
