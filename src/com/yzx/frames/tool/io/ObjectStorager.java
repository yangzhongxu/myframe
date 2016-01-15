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
	 * ��ȡ�洢��public���ļ�
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return ����ļ�������,����null
	 */
	public static Object getPublicObject(String fileName) {
		File file = new File(Tool.getUsefullExternalDir(getPublicDirName()), fileName);
		if (Tool.isUsefullFile(file))
			return getObject(file);
		return null;
	}

	/**
	 * ����public���͵��ļ�
	 * 
	 * @param fileName
	 *            �ļ�����
	 * @param obj
	 *            Ҫ�����Object
	 */
	public static void savePublicObject(String fileName, Object obj) {
		if (!(obj instanceof Serializable))
			throw new IllegalStateException("�洢�ļ��������л�");
		File file = new File(Tool.getUsefullExternalDir(getPublicDirName()), fileName);
		saveObject(obj, file);
	}

	/**
	 * ��ȡ�洢��private���ļ�
	 * 
	 * @param fileName
	 *            �ļ���
	 * @return ����ļ�������,����null
	 */
	public static Object getPrivateObject(String privateDirName, String fileName) {
		File file = new File(Tool.getUsefullExternalDir(privateDirName), fileName);
		if (Tool.isUsefullFile(file))
			return getObject(file);
		return null;
	}

	/**
	 * ����private���͵��ļ�
	 * 
	 * @param fileName
	 *            �ļ�����
	 * @param obj
	 *            Ҫ�����Object
	 */
	public static void savePrivateObject(String privateDirName, String fileName, Object obj) {
		if (!(obj instanceof Serializable))
			throw new IllegalStateException("�洢�ļ��������л�");
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
	 * ����object�ļ�������
	 * 
	 * @param obj
	 *            �ļ�
	 * @param file
	 *            Ŀ��file
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
	 * ��ȡ���ش洢��object�ļ�
	 * 
	 * @param file
	 *            Ŀ��file
	 * @return ����ļ�������,����null
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
