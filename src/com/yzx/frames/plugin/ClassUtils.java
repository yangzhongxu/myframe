package com.yzx.frames.plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import dalvik.system.DexClassLoader;

public class ClassUtils {

	/**
	 * ����·��,��ȡAPK��ȫ��ActivityInfo����Ϣ
	 * 
	 * @param apk_path
	 * @return
	 */
	public ActivityInfo[] getAPKActivityInfo(Context context, String apk_path) {
		PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apk_path, 1);
		if (packageInfo == null)
			return null;
		if ((packageInfo.activities != null) && (packageInfo.activities.length > 0)) {
			return packageInfo.activities;
		} else
			return null;
	}

	/**
	 * ���ݲ��APK��path��Ŀ��class������,����Ŀ��class��class����
	 * 
	 * @param apk_path
	 *            APK·��
	 * @param className
	 *            class ��ȫ��
	 * @return
	 * @throws ClassNotFoundException
	 */
	public Class<?> getApkClassByClassName(Context context, String apk_path, String className) throws ClassNotFoundException {
		final String dexOutputPath = context.getDir("dex", 0).getAbsolutePath();
		ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
		DexClassLoader dexClassLoader = new DexClassLoader(apk_path, dexOutputPath, null, localClassLoader);
		Class<?> clz = dexClassLoader.loadClass(className);
		return clz;
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
	 * @return
	 * @throws Exception
	 */
	public Object instanceObject(Class<?> clz, Class<?>[] paramsType, Object[] params) throws Exception {
		Constructor<?> c = clz.getConstructor(paramsType);
		Object obj = c.newInstance(params);
		return obj;
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
	public void invokeMethod(Object target, String methodName, Class<?>[] paramsType, Object[] params) throws Exception {
		Method method;
		try {
			method = target.getClass().getDeclaredMethod(methodName, paramsType);
		} catch (Exception e) {
			method = target.getClass().getMethod(methodName, paramsType);
		}
		method.setAccessible(true);
		method.invoke(target, params);
	}

	/**
	 * ��ȡ��Ӧ��Դ(APK)����Դ������,һ��������дactivity�е�getResources��getAssert,�滻������ֵ
	 * 
	 * @param superResources
	 *            ԭʼ�����Resource
	 * @param apk_path
	 *            Ŀ��APK·��
	 * @return [0] : AssetManager -----[1] : Resources
	 */
	public Object[] getMyAssertAndResource(Resources superResources, String apk_path) {
		AssetManager mAssetManager = null;
		Resources mResources = null;
		try {
			mAssetManager = AssetManager.class.newInstance();
			invokeMethod(mAssetManager, "addAssetPath", new Class[] { String.class }, new Object[] { apk_path });
		} catch (Exception e) {
		}
		mResources = new Resources(mAssetManager, superResources.getDisplayMetrics(), superResources.getConfiguration());
		return new Object[] { mAssetManager, mResources };
	}
}
