package com.yzx.frames.tool.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.yzx.frames.tool.db.annotation.DBIgnore;
import com.yzx.frames.tool.db.annotation.ID;

public class ClassHelper {

	/**
	 * �ж�ʵ�����ע���Ƿ���ȷ
	 * 
	 * @param clz
	 */
	public static void checkAnnotation(Class<?> clz) {
		int idCount = 0;
		int ignoreCount = 0;
		//
		Field[] fs = clz.getDeclaredFields();
		//
		if (fs.length < 1)
			throw new IllegalArgumentException("ʵ����û������");
		//
		for (Field f : fs) {
			f.setAccessible(true);
			if (f.getAnnotation(ID.class) != null) {
				if (f.getType() != Integer.class && f.getType() != String.class)
					throw new IllegalArgumentException("idֻ����int �� String");
				idCount++;
			}
			if (f.getAnnotation(DBIgnore.class) != null)
				ignoreCount++;
		}
		//
		if (idCount == 0)
			throw new IllegalArgumentException("ʵ����û��id");
		if (idCount > 1)
			throw new IllegalArgumentException("ʵ����idֻ����һ��");
		if (fs.length - ignoreCount < 1)
			throw new IllegalArgumentException("ʵ�����޿�������");
	}

	/**
	 * ͬ�������ص�ʵ���������Field
	 * 
	 * @param clz
	 * @param idCallback
	 *            ��id��ʱ��ص�
	 * @param colCallback
	 *            ��ͨ�ֶλص�
	 */
	public static void callUsefullFields(Class<?> clz, FieldCallback idCallback, FieldCallback colCallback) {
		Field[] fs = clz.getDeclaredFields();
		for (Field f : fs) {
			f.setAccessible(true);
			if (f.getAnnotation(DBIgnore.class) != null)
				continue;
			if (f.getAnnotation(ID.class) != null)
				idCallback.call(f);
			else
				colCallback.call(f);
		}
	}

	/**
	 * ��ȡ������ ���Ժ�ֵ��ӳ��map
	 * 
	 * @param obj
	 * @param containsNull
	 *            �Ƿ����ֵΪnull���ֶ�
	 * @return
	 */
	public static Map<String, Object> getFieldValueMap(final Object obj, final boolean containsNull) {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		callUsefullFields(obj.getClass(), new FieldCallback() {// id
					public void call(Field f) {
						try {
							Object value = f.get(obj);
							if (!containsNull) {
								if (value != null)
									map.put(f.getName(), value);
							} else
								map.put(f.getName(), value);
						} catch (Exception e) {
						}
					}
				}, new FieldCallback() {// ��ͨ
					public void call(Field f) {
						try {
							Object value = f.get(obj);
							if (!containsNull) {
								if (value != null)
									map.put(f.getName(), value);
							} else
								map.put(f.getName(), value);
						} catch (Exception e) {
						}
					}
				});
		return map;
	}

	/**
	 * ��ȡ����������ֵ
	 * 
	 * @return
	 */
	public static String getPrimaryKeyValue(Object obj) {
		Field[] fs = obj.getClass().getDeclaredFields();
		for (Field f : fs) {
			f.setAccessible(true);
			if (f.getAnnotation(ID.class) != null)
				try {
					return f.get(obj).toString();
				} catch (Exception e) {
				}
		}
		return null;
	}

}
