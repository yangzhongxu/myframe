package com.yzx.frames.plugin;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;

public class ProxyActivity extends Activity {

	// 存放ProxyActivity的全部实例的集合
	private static List<ProxyActivity> activityList = new ArrayList<ProxyActivity>();
	// 传递过来的要代理加载的class的intent的key,需要与插件APK一致
	public static final String KEY_TO_LOAD_CLASS_NAME = "__kcn__";
	// 反射的设置代理activity的方法名
	private static final String PROXY_METHOD_NAME = "setProxy";
	// 要加载的APK的路径
	private static String apk_path;

	/**
	 * 开始加载
	 * 
	 * @param apk_path
	 *            插件APK的路径
	 */
	public static void startProxy(Context from, String apk_path) {
		Intent intent = new Intent(from, ProxyActivity.class);
		ProxyActivity.apk_path = apk_path;
		from.startActivity(intent);
	}

	public static void startProxy(Context from, String apk_path, String className) {
		Intent intent = new Intent(from, ProxyActivity.class);
		intent.putExtra(KEY_TO_LOAD_CLASS_NAME, className);
		ProxyActivity.apk_path = apk_path;
		from.startActivity(intent);
	}

	//
	//
	//

	private AssetManager mAssetManager;
	private Resources mResources;
	private Object mProxyActivity;
	private ClassUtils cUtils = new ClassUtils();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否有代理目标APK路径
		if (apk_path == null) {
			finish();
			return;
		}
		activityList.add(this);
		changeResource();
		//
		// 获取要加载的目标class,如果没有,则加载第一个activity
		String target_load_class = getIntent().getStringExtra(KEY_TO_LOAD_CLASS_NAME);
		if (target_load_class == null)
			launchFirstActivity();
		else
			launchTargetActivity(target_load_class);
	}

	@Override
	protected void onDestroy() {
		activityList.remove(this);
		if (activityList.isEmpty())
			apk_path = null;
		syncLifeCircleMethod("onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onStart() {
		super.onStart();
		syncLifeCircleMethod("onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		syncLifeCircleMethod("onResume");
	}

	@Override
	protected void onPause() {
		super.onPause();
		syncLifeCircleMethod("onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		syncLifeCircleMethod("onStop");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			cUtils.invokeMethod(mProxyActivity, "onActivityResult", new Class[] { int.class, int.class, Intent.class }, new Object[] {
					requestCode, resultCode, data });
		} catch (Exception e) {
		}
	}

	@Override
	public AssetManager getAssets() {
		return mAssetManager == null ? super.getAssets() : mAssetManager;
	}

	@Override
	public Resources getResources() {
		return mResources == null ? super.getResources() : mResources;
	}

	//
	//
	//

	/*
	 * 获取APK中的第一个activity,并加载
	 */
	private void launchFirstActivity() {
		ActivityInfo[] infos = cUtils.getAPKActivityInfo(this, apk_path);
		if (infos == null) {
			onLoadFailed(null);
			return;
		}
		String firstActivityName = infos[0].name;
		launchTargetActivity(firstActivityName);
	}

	/*
	 * 加载目标activity
	 */
	private void launchTargetActivity(final String className) {
		try {
			// 获取class对象
			Class<?> targetClz = cUtils.getApkClassByClassName(this, apk_path, className);
			// 获取实例对象
			mProxyActivity = cUtils.instanceObject(targetClz, new Class[] {}, new Object[] {});
			// 调用setProxy方法
			cUtils.invokeMethod(mProxyActivity, PROXY_METHOD_NAME, new Class[] { Activity.class }, new Object[] { this });
			// 调用onCreate方法
			cUtils.invokeMethod(mProxyActivity, "onCreate", new Class[] { Bundle.class }, new Object[] { null });
		} catch (Exception e) {
			onLoadFailed(e);
		}
	}

	/* 加载失败后 */
	private void onLoadFailed(Exception e) {
		if (e != null)
			e.printStackTrace();
		finish();
	}

	/*
	 * 同步调用反射声明周期方法
	 */
	private void syncLifeCircleMethod(String methodName) {
		try {
			cUtils.invokeMethod(mProxyActivity, methodName, new Class[] {}, new Object[] {});
		} catch (Exception e) {
		}
	}

	//
	//
	//

	/*
	 * 替换资源管理类,将获取的R资源和asset资源转移到目标APK中
	 */
	private void changeResource() {
		Object[] result = cUtils.getMyAssertAndResource(super.getResources(), apk_path);
		mAssetManager = (AssetManager) result[0];
		mResources = (Resources) result[1];
	}

	//
	//
	//

}