package com.yzx.frames.tool.fm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ObjectBox {

	private final HashMap<String, Object> data_map = new HashMap<String, Object>(0);

	private ObjectBox() {
	}

	/*
	 * 
	 * 
	 */

	/** 数据集合 */
	private static HashMap<String, ObjectBox> CLASS_MAP = new HashMap<String, ObjectBox>(0);

	/**
	 * 获取对应class的ObjectBox对象
	 * 
	 * @param clz
	 *            Class
	 * @return ObjectBox对象
	 */
	private static ObjectBox getClassBox(Class<?> clz) {
		ObjectBox box = CLASS_MAP.get(clz.getName());
		if (box == null) {
			box = new ObjectBox();
			CLASS_MAP.put(clz.getName(), box);
		}
		return box;
	}

	/**
	 * 检查输入类型,不能是String和基本类型
	 * 
	 * @param obj
	 *            输入对象
	 */
	private static void checkClass(Object obj) {
		if (obj instanceof String || obj instanceof Integer || obj instanceof Float || obj instanceof Long || obj instanceof Boolean
				|| obj instanceof Double || obj instanceof Byte || obj instanceof Short || obj instanceof Character || obj == null)
			throw new IllegalStateException("no support " + obj.getClass().getName());
	}

	/************************************************************************************
	 * 存放数据
	 * 
	 * @param key
	 *            key
	 * @param obj
	 *            self
	 * @return 如果key重复,返回旧数据
	 *************************************************************************************/
	public static synchronized Object in(String key, Object obj) {
		checkClass(obj);
		ObjectBox box = getClassBox(obj.getClass());
		Object oldobj = box.data_map.get(key);
		box.data_map.put(key, obj);
		return oldobj;
	}

	public static synchronized Object in(Object obj) {
		return in(null, obj);
	}

	/*************************************************************************************
	 * 移除数据
	 * 
	 * @param key
	 *            key
	 * @param clz
	 *            Class
	 * @return 数据(如果存在)
	 ************************************************************************************/
	public static Object out(String key, Class<?> clz) {
		ObjectBox box = getClassBox(clz);
		Object oldData = box.data_map.get(key);
		box.data_map.remove(key);
		return oldData;
	}

	public static Object out(Class<?> clz) {
		return out(null, clz);
	}

	/*************************************************************************************
	 * 获取数据
	 * 
	 * @param key
	 *            key
	 * @param clz
	 *            Class
	 * @return 数据(如果存在)
	 ************************************************************************************/
	public static Object get(String key, Class<?> clz) {
		ObjectBox box = getClassBox(clz);
		return box.data_map.get(key);
	}

	public static Object get(Class<?> clz) {
		return get(null, clz);
	}

	/*************************************************************************************
	 * 获取全部
	 * 
	 * @param clz
	 *            Class
	 * @return list , may empty , not null
	 *************************************************************************************/
	public static List<Object> getAll(Class<?> clz) {
		ObjectBox box = getClassBox(clz);
		ArrayList<Object> list = new ArrayList<Object>(0);
		if (!box.data_map.isEmpty())
			list.addAll(box.data_map.values());
		return list;
	}

	/*************************************************************************************
	 * 清除全部
	 * 
	 * @param clz
	 *            Class
	 *************************************************************************************/
	public static void clearClassBox(Class<?> clz) {
		getClassBox(clz).data_map.clear();
	}

	public static void clearFullBox() {
		CLASS_MAP.clear();
	}

}
