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
	 * 根据路径,获取APK的全部ActivityInfo的信息
	 * 
	 * @param apk_path
	 *            apk绝对路径
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
	 * 根据插件APK的path和目标class的名称,返回目标class的class对象
	 * 
	 * @param apk_path
	 *            APK路径
	 * @param className
	 *            class 的全名
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
	 * 获取对应资源(APK)的资源管理类,一般用来重写activity中的getResources和getAssert,替换掉返回值
	 * 
	 * @param superResources
	 *            原始父类的Resource
	 * @param apk_path
	 *            目标APK路径
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
