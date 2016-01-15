package com.yzx.frames.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射Help类
 */
public class ReflectUtil {

	/**
	 * 反射获取属性值
	 * 
	 * @param who
	 *            目标对象
	 * @param name
	 *            属性名称
	 * @return null if error
	 */
	public static Object getFieldValue(Object who, String name) {
		Field field = getField(who, name);
		if (field == null)
			return null;
		try {
			return field.get(who);
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * 反射设置属性值
	 * 
	 * @param who
	 *            目标对象
	 * @param name
	 *            属性名称
	 * @param value
	 *            属性值
	 * @return true or false
	 */
	public static boolean setFieldValue(Object who, String name, Object value) {
		Field field = getField(who, name);
		if (field == null)
			return false;
		try {
			field.set(who, value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 反射调用方法
	 * 
	 * @param target
	 *            目标对象
	 * @param methodName
	 *            方法名称
	 * @param paramsType
	 *            参数类型数组
	 * @param params
	 *            参数数组
	 * @throws Exception
	 */
	public static boolean invokeMethod(Object target, String methodName, Class<?>[] paramsType, Object[] params) {
		Method method = null;
		try {
			method = target.getClass().getDeclaredMethod(methodName, paramsType);
		} catch (Exception e) {
			try {
				method = target.getClass().getMethod(methodName, paramsType);
			} catch (Exception e2) {
			}
		}
		if (method != null) {
			try {
				method.setAccessible(true);
				method.invoke(target, params);
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	/**
	 * 根据class ,反射new一个对象
	 * 
	 * @param clz
	 *            目标对象的class
	 * @param paramsType
	 *            参数类型数组
	 * @param params
	 *            参数数组
	 * @return null if error
	 */
	public static Object generateObject(Class<?> clz, Class<?>[] paramsType, Object[] params) {
		try {
			Constructor<?> c = clz.getConstructor(paramsType);
			return c.newInstance(params);
		} catch (Exception e) {
			return null;
		}
	}

	/***************************
	 * 
	 * 获取对象的Field
	 * 
	 * @return 如果失败 返回null
	 * 
	 ***************************/
	private static Field getField(Object who, String name) {
		Field field = null;
		try {
			field = who.getClass().getDeclaredField(name);
		} catch (Exception e) {
		}
		if (field == null)
			try {
				field = who.getClass().getField(name);
			} catch (Exception e2) {
			}
		if (field != null)
			field.setAccessible(true);
		return field;
	}

}
