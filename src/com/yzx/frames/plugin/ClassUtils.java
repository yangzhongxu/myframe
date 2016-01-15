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
	 * 根据路径,获取APK的全部ActivityInfo的信息
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
	 * 根据插件APK的path和目标class的名称,返回目标class的class对象
	 * 
	 * @param apk_path
	 *            APK路径
	 * @param className
	 *            class 的全名
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
	 * 根据class ,反射new一个对象
	 * 
	 * @param clz
	 *            目标对象的class
	 * @param paramsType
	 *            参数类型数组
	 * @param params
	 *            参数数组
	 * @return
	 * @throws Exception
	 */
	public Object instanceObject(Class<?> clz, Class<?>[] paramsType, Object[] params) throws Exception {
		Constructor<?> c = clz.getConstructor(paramsType);
		Object obj = c.newInstance(params);
		return obj;
	}

	/**
	 * 反射调用方法
	 * 
	 * @param target
	 *            目标对象
	 * @param methodName
	 *            方法名称
	 * @param paramsType
	 *            参数类型数组
	 * @param params
	 *            参数数组
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
	 * 获取对应资源(APK)的资源管理类,一般用来重写activity中的getResources和getAssert,替换掉返回值
	 * 
	 * @param superResources
	 *            原始父类的Resource
	 * @param apk_path
	 *            目标APK路径
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
