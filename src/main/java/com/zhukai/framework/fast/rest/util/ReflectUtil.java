package com.zhukai.framework.fast.rest.util;

import com.zhukai.framework.fast.rest.annotation.core.Component;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ReflectUtil {
	private static final Logger logger = LoggerFactory.getLogger(ReflectUtil.class);

	public static Object getFieldValue(Object object, String fieldName) {
		Field field = getDeclaredField(object.getClass(), fieldName);
		field.setAccessible(true);
		try {
			return field.get(object);
		} catch (IllegalAccessException e) {
			logger.error("Reflect error", e);
			return null;
		}
	}

	public static void setFieldValue(Object obj, String fieldName, Object value) {
		Field field = getDeclaredField(obj.getClass(), fieldName);
		field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			logger.error("Reflect error", e);
		}
	}

	/**
	 * 根据fieldName获得entityClass的属性，也可以是private或父类继承的
	 */
	public static Field getDeclaredField(Class entityClass, String fieldName) {
		if (StringUtils.isBlank(fieldName) || entityClass == null) {
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

	public static Object invokeMethod(Object obj, String methodName, Class<?>[] paramsClass, Object[] params) throws Throwable {
		Object returnValue = null;
		try {
			Method method = obj.getClass().getMethod(methodName, paramsClass);
			returnValue = method.invoke(obj, params);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		return returnValue;
	}

	public static Object invokeMethod(Object obj, String methodName) throws Throwable {
		return invokeMethod(obj, methodName, null, null);
	}

	public static <T> T createInstance(Class<T> objClass, Class<?>[] paramsClass, Object... params) {
		Object obj = null;
		try {
			Constructor constructor = objClass.getConstructor(paramsClass);
			obj = constructor.newInstance(params);
		} catch (Exception e) {
			logger.error("Reflect error", e);
		}
		return objClass.cast(obj);
	}

	public static <T> T createInstance(Class<T> objClass) {
		try {
			return objClass.newInstance();
		} catch (ReflectiveOperationException e) {
			logger.error("Reflect error", e);
			return null;
		}
	}

	/**
	 * @param objectClass
	 * @param annotationClass
	 * @return 递归检测objectClass是否被annotationClass注解标记
	 */
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

	public static boolean existWholeAnnotations(Class objectClass, Class<? extends Annotation>... annotationClasses) {
		for (Class<? extends Annotation> annotation : annotationClasses) {
			if (!existAnnotation(objectClass, annotation)) {
				return false;
			}
		}
		return true;
	}

	public static boolean existAnyoneAnnotations(Class objectClass, Class<? extends Annotation>... annotationClasses) {
		for (Class<? extends Annotation> annotation : annotationClasses) {
			if (existAnnotation(objectClass, annotation)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param objectClass
	 * @return 若objectClass存在Component注解，则返回Component的value，否则返回null
	 */
	public static String getComponentValue(Class objectClass) throws Throwable {
		if (objectClass.isAnnotation()) {
			return null;
		}
		if (objectClass.isAnnotationPresent(Component.class)) {
			return Component.class.cast(objectClass.getAnnotation(Component.class)).value();
		}
		Annotation[] annotations = objectClass.getAnnotations();
		for (Annotation annotation : annotations) {
			if (existAnnotation(annotation.annotationType(), Component.class)) {
				return invokeMethod(annotation, "value").toString();
			}
		}
		return null;
	}

	private ReflectUtil() {
	}
}
