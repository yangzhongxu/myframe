package com.yzx.frames.tool.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.yzx.frames.tool.Tool;

public class ObjectStorager extends Tool {

	/**
	 * 获取存储的public的文件
	 * 
	 * @param fileName
	 *            文件名
	 * @return 如果文件不存在,返回null
	 */
	public static Object getPublicObject(String fileName) {
		File file = new File(Tool.getUsefullExternalDir(getPublicDirName()), fileName);
		if (Tool.isUsefullFile(file))
			return getObject(file);
		return null;
	}

	/**
	 * 保存public类型的文件
	 * 
	 * @param fileName
	 *            文件名称
	 * @param obj
	 *            要保存的Object
	 */
	public static void savePublicObject(String fileName, Object obj) {
		if (!(obj instanceof Serializable))
			throw new IllegalStateException("存储文件不可序列化");
		File file = new File(Tool.getUsefullExternalDir(getPublicDirName()), fileName);
		saveObject(obj, file);
	}

	/**
	 * 获取存储的private的文件
	 * 
	 * @param fileName
	 *            文件名
	 * @return 如果文件不存在,返回null
	 */
	public static Object getPrivateObject(String privateDirName, String fileName) {
		File file = new File(Tool.getUsefullExternalDir(privateDirName), fileName);
		if (Tool.isUsefullFile(file))
			return getObject(file);
		return null;
	}

	/**
	 * 保存private类型的文件
	 * 
	 * @param fileName
	 *            文件名称
	 * @param obj
	 *            要保存的Object
	 */
	public static void savePrivateObject(String privateDirName, String fileName, Object obj) {
		if (!(obj instanceof Serializable))
			throw new IllegalStateException("存储文件不可序列化");
		File file = new File(Tool.getUsefullExternalDir(privateDirName), fileName);
		saveObject(obj, file);
	}

	//
	//
	// ===========================================================================
	//
	// ===========================================================================
	//
	//

	/**
	 * 保存object文件到本地
	 * 
	 * @param obj
	 *            文件
	 * @param file
	 *            目标file
	 */
	public static void saveObject(Object obj, File file) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(obj);
			out.flush();
		} catch (Exception e) {
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
				}
		}
	}

	/**
	 * 获取本地存储的object文件
	 * 
	 * @param file
	 *            目标file
	 * @return 如果文件不存在,返回null
	 */
	public static Object getObject(File file) {
		ObjectInputStream in = null;
		try {
			in = new ObjectInputStream(new FileInputStream(file));
			return in.readObject();
		} catch (Exception e) {
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
				}
		}
		return null;
	}

}
