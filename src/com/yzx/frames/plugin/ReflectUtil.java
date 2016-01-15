package com.yzx.frames.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * ����Help��
 */
public class ReflectUtil {

	/**
	 * �����ȡ����ֵ
	 * 
	 * @param who
	 *            Ŀ�����
	 * @param name
	 *            ��������
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
	 * ������������ֵ
	 * 
	 * @param who
	 *            Ŀ�����
	 * @param name
	 *            ��������
	 * @param value
	 *            ����ֵ
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
	 * ������÷���
	 * 
	 * @param target
	 *            Ŀ�����
	 * @param methodName
	 *            ��������
	 * @param paramsType
	 *            ������������
	 * @param params
	 *            ��������
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
	 * ����class ,����newһ������
	 * 
	 * @param clz
	 *            Ŀ������class
	 * @param paramsType
	 *            ������������
	 * @param params
	 *            ��������
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
	 * ��ȡ�����Field
	 * 
	 * @return ���ʧ�� ����null
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
