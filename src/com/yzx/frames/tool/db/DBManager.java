package com.yzx.frames.tool.db;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBManager {

	private DBManager() {
	}

	private SQLiteDatabase db;

	public boolean save(Object obj) {
		Map<String, Object> map = ClassHelper.getFieldValueMap(obj, false);
		ContentValues cv = new ContentValues();
		for (Map.Entry<String, Object> en : map.entrySet())
			cv.put(en.getKey(), en.getValue().toString());
		long c = db.insert(SqlGetter.getTableName(obj.getClass()), null, cv);
		return c > 0;
	}

	public void save(List<Object> list) {
		db.beginTransaction();
		for (Object obj : list)
			save(obj);
		db.setTransactionSuccessful();
	}

	public boolean deleteById(Class<?> clz, String id) {
		String table = SqlGetter.getTableName(clz);
		String idName = SqlGetter.getPrimaryKeyName(clz);
		int c = db.delete(table, idName + " = '" + id + "'", null);
		return c > 0;
	}

	public boolean deleteByWhere(Class<?> clz, String where) {
		String table = SqlGetter.getTableName(clz);
		int c = db.delete(table, where, null);
		return c > 0;
	}

	public boolean update(Object obj, String idValue) {
		String tableName = SqlGetter.getTableName(obj.getClass());
		String idName = SqlGetter.getPrimaryKeyName(obj.getClass());
		Map<String, Object> map = ClassHelper.getFieldValueMap(obj, false);
		ContentValues cv = new ContentValues();
		for (Map.Entry<String, Object> en : map.entrySet())
			cv.put(en.getKey(), en.getValue().toString());
		int c = db.update(tableName, cv, idName + " = '" + idValue + "'", null);
		return c > 0;
	}

	public List<Object> getByWhere(Class<?> clz, String where) {
		final Cursor cur = db.rawQuery(SqlGetter.getGetSql(clz, where), null);
		ArrayList<Object> list = new ArrayList<Object>(0);
		if (cur != null && cur.getCount() > 0)
			while (cur.moveToNext()) {
				try {
					final Object ins = Class.forName(clz.getName()).newInstance();
					ClassHelper.callUsefullFields(ins.getClass(), new FieldCallback() {// id
								public void call(Field f) {
									setFieldValue(f, ins, cur.getString(cur.getColumnIndex(f.getName())));
								}
							}, new FieldCallback() {// 普通
								public void call(Field f) {
									setFieldValue(f, ins, cur.getString(cur.getColumnIndex(f.getName())));
								}
							});
					list.add(ins);
				} catch (Exception e) {
				}
			}
		closeCursor(cur);
		return list;
	}

	public List<Object> getAll(Class<?> clz) {
		return getByWhere(clz, "1 = 1");
	}

	public void close() {
		if (db != null)
			db.close();
		db = null;
	}

	//
	// ------------------------------------- priavte ↓-----------------------------------
	//

	private void setFieldValue(Field f, Object obj, Object value) {
		try {
			f.set(obj, value);
		} catch (Exception e) {
		}
	}

	private void closeCursor(Cursor c) {
		if (c != null)
			c.close();
	}

	//
	// ------------------------------------- static ↓ -------------------------------------
	//

	@SuppressWarnings("unused")
	private static Application context;
	private static File dbDir;

	/**
	 * 初始化
	 * 
	 * @param context
	 * @param dbDir
	 *            存储数据库的文件夹路径
	 */
	public static void init(Application context, String dbDir) {
		DBManager.context = context;
		DBManager.dbDir = new File(dbDir);
		DBManager.dbDir.mkdirs();
	}

	/**
	 * 打开数据库
	 * 
	 * @param dbName
	 *            数据库名称
	 * @param clz
	 *            对应的实体类的class
	 * @return
	 * 
	 */
	public static DBManager open(String dbName, Class<?> clz) {
		DBManager dbm = new DBManager();
		{
			SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(new File(dbDir, dbName + ".db"), null);
			db.execSQL(SqlGetter.getCreateTable(clz));
			dbm.db = db;
		}
		return dbm;
	}

}
