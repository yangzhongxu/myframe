package com.yzx.frames.tool.db;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.yzx.frames.tool.db.annotation.DBIgnore;
import com.yzx.frames.tool.db.annotation.ID;

public class ClassHelper {

	/**
	 * 判断实体类的注释是否正确
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
			throw new IllegalArgumentException("实体类没有属性");
		//
		for (Field f : fs) {
			f.setAccessible(true);
			if (f.getAnnotation(ID.class) != null) {
				if (f.getType() != Integer.class && f.getType() != String.class)
					throw new IllegalArgumentException("id只能是int 或 String");
				idCount++;
			}
			if (f.getAnnotation(DBIgnore.class) != null)
				ignoreCount++;
		}
		//
		if (idCount == 0)
			throw new IllegalArgumentException("实体类没有id");
		if (idCount > 1)
			throw new IllegalArgumentException("实体类id只能有一个");
		if (fs.length - ignoreCount < 1)
			throw new IllegalArgumentException("实体类无可用属性");
	}

	/**
	 * 同步遍历回调实体类的属性Field
	 * 
	 * @param clz
	 * @param idCallback
	 *            是id的时候回调
	 * @param colCallback
	 *            普通字段回调
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
	 * 获取对象中 属性和值的映射map
	 * 
	 * @param obj
	 * @param containsNull
	 *            是否包含值为null的字段
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
				}, new FieldCallback() {// 普通
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
	 * 获取对象主键的值
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
