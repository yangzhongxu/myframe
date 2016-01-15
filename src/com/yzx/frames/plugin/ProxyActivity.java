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

	// ���ProxyActivity��ȫ��ʵ���ļ���
	private static List<ProxyActivity> activityList = new ArrayList<ProxyActivity>();
	// ���ݹ�����Ҫ������ص�class��intent��key,��Ҫ����APKһ��
	public static final String KEY_TO_LOAD_CLASS_NAME = "__kcn__";
	// ��������ô���activity�ķ�����
	private static final String PROXY_METHOD_NAME = "setProxy";
	// Ҫ���ص�APK��·��
	private static String apk_path;

	/**
	 * ��ʼ����
	 * 
	 * @param apk_path
	 *            ���APK��·��
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
		// �ж��Ƿ��д���Ŀ��APK·��
		if (apk_path == null) {
			finish();
			return;
		}
		activityList.add(this);
		changeResource();
		//
		// ��ȡҪ���ص�Ŀ��class,���û��,����ص�һ��activity
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
	 * ��ȡAPK�еĵ�һ��activity,������
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
	 * ����Ŀ��activity
	 */
	private void launchTargetActivity(final String className) {
		try {
			// ��ȡclass����
			Class<?> targetClz = cUtils.getApkClassByClassName(this, apk_path, className);
			// ��ȡʵ������
			mProxyActivity = cUtils.instanceObject(targetClz, new Class[] {}, new Object[] {});
			// ����setProxy����
			cUtils.invokeMethod(mProxyActivity, PROXY_METHOD_NAME, new Class[] { Activity.class }, new Object[] { this });
			// ����onCreate����
			cUtils.invokeMethod(mProxyActivity, "onCreate", new Class[] { Bundle.class }, new Object[] { null });
		} catch (Exception e) {
			onLoadFailed(e);
		}
	}

	/* ����ʧ�ܺ� */
	private void onLoadFailed(Exception e) {
		if (e != null)
			e.printStackTrace();
		finish();
	}

	/*
	 * ͬ�����÷����������ڷ���
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
	 * �滻��Դ������,����ȡ��R��Դ��asset��Դת�Ƶ�Ŀ��APK��
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