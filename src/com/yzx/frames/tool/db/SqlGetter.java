package com.yzx.frames.tool.db;

import java.lang.reflect.Field;

import com.yzx.frames.tool.db.annotation.ID;

public class SqlGetter {

	/**
	 * �������sql
	 * 
	 * @param clz
	 * @return
	 */
	public static String getCreateTable(Class<?> clz) {
		ClassHelper.checkAnnotation(clz);
		//
		String start = "CREATE TABLE  IF NOT EXISTS " + getTableName(clz);
		//
		final StringBuffer sbb = new StringBuffer();
		ClassHelper.callUsefullFields(clz, new FieldCallback() {// id
					public void call(Field fi) {
						if (fi.getType() == Integer.class)
							sbb.append(fi.getName() + " INTEGER PRIMARY KEY AUTOINCREMENT,");
						else if (fi.getType() == String.class)
							sbb.append(fi.getName() + " TEXT PRIMARY KEY ,");
					}
				}, new FieldCallback() {// ��ͨ
					public void call(Field fi) {
						sbb.append(fi.getName() + " TEXT,");
					}
				});
		sbb.deleteCharAt(sbb.length() - 1);
		//
		String sql = new StringBuffer().append(start).append(" (").append(sbb.toString()).append(")").toString();
		return sql;
	}

	/**
	 * ��ȡ ��ȡȫ�����ݵ� sql
	 * 
	 * @param clz
	 * @return
	 */
	public static String getGetSql(Class<?> clz, String where) {
		return "SELECT * FROM " + getTableName(clz) + " WHERE " + where;
	}

	/**
	 * ��ȡ������
	 * 
	 * @param clz
	 * @return
	 */
	public static String getTableName(Class<?> clz) {
		return clz.getName().replaceAll("\\.", "_");
	}

	/**
	 * ��ȡid��������
	 * 
	 * @param clz
	 * @return û�з���null
	 */
	public static String getPrimaryKeyName(Class<?> clz) {
		Field[] fs = clz.getDeclaredFields();
		for (Field f : fs) {
			f.setAccessible(true);
			if (f.getAnnotation(ID.class) != null)
				return f.getName();
		}
		return null;
	}

}
