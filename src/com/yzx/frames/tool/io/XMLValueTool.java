package com.yzx.frames.tool.io;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;

public class XMLValueTool {

	private static Context context;
	private static final String COMMON_FILE_NAME = "xml_values";

	public static void init(Context context) {
		XMLValueTool.context = context.getApplicationContext();
	}

	/*
     *
     *
     *
     */

	public static void saveCommonString(String key, String value) {
		SharedPreferences sp = context.getSharedPreferences(COMMON_FILE_NAME, Context.MODE_PRIVATE);
		sp.edit().putString(key, value).commit();
	}

	public static String getCommonString(String key) {
		SharedPreferences sp = context.getSharedPreferences(COMMON_FILE_NAME, Context.MODE_PRIVATE);
		return sp.getString(key, null);
	}

	public static void deleteCommonString(String key) {
		SharedPreferences sp = context.getSharedPreferences(COMMON_FILE_NAME, Context.MODE_PRIVATE);
		sp.edit().putString(key, null);
	}

	/*
     *
     *
     *
     */

	public static void saveSingleString(String fileName, String value) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		sp.edit().putString("value", value).commit();
	}

	public static String getSingleString(String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getString("value", null);
	}

	public static void deleteSingleString(String fileName) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		sp.edit().clear().commit();
	}

	/*
     *
     *
     *
     */

	public static void saveSingleObject(String fileName, Object obj) {
		String toSaveString = null;
		try {
			toSaveString = (obj == null) ? null : JSON.toJSONString(obj);
		} catch (Exception e) {
			toSaveString = null;
		}
		saveSingleString(fileName, toSaveString);
	}

	public static Object getSingleObject(String fileName, Class<?> clz) {
		String stringData = getSingleString(fileName);
		if (stringData == null)
			return null;
		if (!stringData.startsWith("{") && !stringData.startsWith("[")) {
			saveSingleString(fileName, null);
			return null;
		}
		if (stringData.startsWith("{")) {
			try {
				return JSON.parseObject(stringData, clz);
			} catch (Exception e) {
				saveSingleString(fileName, null);
				return null;
			}
		} else {
			try {
				return JSON.parseArray(stringData, clz);
			} catch (Exception e) {
				saveSingleString(fileName, null);
				return null;
			}
		}
	}

}
