package com.yzx.frames.plugin;

import dalvik.system.DexClassLoader;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

public class PluginUtil {

	/**
	 * ����·��,��ȡAPK��ȫ��ActivityInfo����Ϣ
	 * 
	 * @param apk_path
	 *            apk����·��
	 * @return null if error or empty.
	 */
	public ActivityInfo[] getAPKActivityInfo(Context context, String apk_path) {
		PackageInfo packageInfo = context.getPackageManager().getPackageArchiveInfo(apk_path, PackageManager.GET_ACTIVITIES);
		if (packageInfo == null)
			return null;
		if ((packageInfo.activities != null) && (packageInfo.activities.length > 0))
			return packageInfo.activities;
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
	public Class<?> getApkClassByClassName(Context context, String apk_path, String className) {
		final String dexOutputPath = context.getDir("dex", 0).getAbsolutePath();
		ClassLoader localClassLoader = ClassLoader.getSystemClassLoader();
		DexClassLoader dexClassLoader = new DexClassLoader(apk_path, dexOutputPath, null, localClassLoader);
		try {
			Class<?> clz = dexClassLoader.loadClass(className);
			return clz;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ��ȡ��Ӧ��Դ(APK)����Դ������,һ��������дactivity�е�getResources��getAssert,�滻������ֵ
	 * 
	 * @param superResources
	 *            ԭʼ�����Resource
	 * @param apk_path
	 *            Ŀ��APK·��
	 * @return [0] : AssetManager -----[1] : Resources ; ERROR : null
	 */
	public Object[] getMyAssertAndResource(Resources superResources, String apk_path) {
		AssetManager mAssetManager = null;
		Resources mResources = null;
		try {
			mAssetManager = AssetManager.class.newInstance();
			if (!ReflectUtil.invokeMethod(mAssetManager, "addAssetPath", new Class[] { String.class }, new Object[] { apk_path }))
				throw new Exception();
		} catch (Exception e) {
			return null;
		}
		mResources = new Resources(mAssetManager, superResources.getDisplayMetrics(), superResources.getConfiguration());
		return new Object[] { mAssetManager, mResources };
	}

}
