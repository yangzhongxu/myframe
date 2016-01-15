package com.yzx.frames.plugin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class PluginActivity extends Activity {

	// 和ProxyActivity对应,传递给代理Activity的key,表示要加载的类名
	public static final String KEY_TO_LOAD_CLASS_NAME = "__kcn__";
	private Activity proxyActivty;

	//
	//
	//

	/**
	 * 设置代理activity
	 */
	public void setProxy(Activity activity) {
		proxyActivty = activity;
	}

	//
	//
	//

	@Override
	protected void onCreate(Bundle arg0) {
		if (!isProxy())
			super.onCreate(arg0);
	}

	@Override
	protected void onStart() {
		if (!isProxy())
			super.onStart();
	}

	@Override
	protected void onResume() {
		if (!isProxy())
			super.onResume();
	}

	@Override
	protected void onPause() {
		if (!isProxy())
			super.onPause();
	}

	@Override
	protected void onStop() {
		if (!isProxy())
			super.onStop();
	}

	@Override
	protected void onDestroy() {
		if (!isProxy())
			super.onDestroy();
	}

	@Override
	public void setContentView(View view, LayoutParams params) {
		if (!isProxy())
			super.setContentView(view, params);
		else
			getProxyActivty().setContentView(view, params);
	}

	@Override
	public void setContentView(int layoutResID) {
		if (!isProxy())
			super.setContentView(layoutResID);
		else
			getProxyActivty().setContentView(layoutResID);
	}

	@Override
	public void setContentView(View view) {
		if (!isProxy())
			super.setContentView(view);
		else
			getProxyActivty().setContentView(view);
	}

	@Override
	public void startActivity(Intent intent) {
		if (isProxy()) {
			String className = intent.getComponent().getClassName();
			intent.setClass(getProxyActivty(), getProxyActivty().getClass());
			intent.putExtra(KEY_TO_LOAD_CLASS_NAME, className);
			getProxyActivty().startActivity(intent);
		} else
			super.startActivity(intent);
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		if (isProxy()) {
			String className = intent.getComponent().getClassName();
			intent.setClass(getProxyActivty(), getProxyActivty().getClass());
			intent.putExtra(KEY_TO_LOAD_CLASS_NAME, className);
			getProxyActivty().startActivityForResult(intent, requestCode);
		} else
			super.startActivityForResult(intent, requestCode);
	}

	@Override
	public View findViewById(int id) {
		if (!isProxy())
			return super.findViewById(id);
		else
			return getProxyActivty().findViewById(id);
	}

	//
	//
	//

	/**
	 * 获取代理的activity
	 */
	protected Activity getProxyActivty() {
		return proxyActivty == null ? this : proxyActivty;
	}

	/**
	 * 是不是被代理
	 */
	protected boolean isProxy() {
		return proxyActivty != null;
	}

	//
	//
	//

}
