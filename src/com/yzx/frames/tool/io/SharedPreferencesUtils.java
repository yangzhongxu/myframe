package com.yzx.frames.tool.io;

import com.alibaba.fastjson.JSON;
import com.yzx.frames.tool.Tool;

import android.app.Application;
import android.content.SharedPreferences;

public class SharedPreferencesUtils extends Tool {

	private static SharedPreferences sp;

	private static void init() {
		if (sp == null)
			sp = getApplication().getSharedPreferences(getPublicSharedPreferencesName(), Application.MODE_PRIVATE);
	}

	private static void release() {
		sp = null;
	}

	/**
	 * �洢�ַ�,����public�ļ�xml
	 * 
	 * @param key
	 * @param value
	 */
	public static void saveString(String key, String value) {
		init();
		sp.edit().putString(key, value).commit();
		release();
	}

	/**
	 * ��ȡ�ַ�,����public�ļ�xml
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		init();
		String result = sp.getString(key, null);
		release();
		return result;
	}

	/**
	 * �洢object ,ʹ�õ�һ�ļ�xml
	 * 
	 * @param fileName
	 *            �ļ���
	 * @param obj
	 *            Ŀ�����
	 */
	public static void saveObject(String fileName, Object obj) {
		String toSaveString = (obj == null) ? null : JSON.toJSONString(obj);
		getApplication().getSharedPreferences(fileName, Application.MODE_PRIVATE).edit().putString("value", toSaveString).commit();
	}

	/**
	 * �洢����object
	 * 
	 * @param fileName
	 * @param clz
	 * @return
	 */
	public static Object getSingleObject(String fileName, Class<?> clz) {
		String json = getApplication().getSharedPreferences(fileName, Application.MODE_PRIVATE).getString("value", null);
		return json == null ? null : JSON.parseObject(json, clz);
	}

	/**
	 * �洢List
	 * 
	 * @param fileName
	 * @param clz
	 * @return
	 */
	public static Object getListObject(String fileName, Class<?> clz) {
		String json = getApplication().getSharedPreferences(fileName, Application.MODE_PRIVATE).getString("value", null);
		return json == null ? null : JSON.parseArray(json, clz);
	}
}
