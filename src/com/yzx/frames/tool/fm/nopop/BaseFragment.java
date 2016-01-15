package com.yzx.frames.tool.fm.nopop;

import java.lang.reflect.Field;

import com.yzx.frames.tool.fm.Fid;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

public abstract class BaseFragment extends Fragment {

	// ====================protected value================

	protected FragmentActivity activity;

	//
	// ====================set and function======================
	//

	protected abstract int getRootViewRes();

	protected abstract void doMain();

	protected void release() {
	}

	protected void init() {
	}

	protected int getWidth() {
		return -1;
	}

	protected int getHeight() {
		return -1;
	}

	protected boolean isAnnotationUseing() {
		return false;
	}

	protected View findView(int id) {
		return getRootView().findViewById(id);
	}

	protected View findView(String tag) {
		return getRootView().findViewWithTag(tag);
	}

	protected void runOnMain(Runnable run) {
		activity.runOnUiThread(run);
	}

	//
	// =====================life circle===================
	//

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FragmentActivity) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		init();
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return rootView = inflater.inflate(getRootViewRes(), null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (isAnnotationUseing())
			initAnnotationView();
		reLayoutParams();
		doMain();
	}

	@Override
	public void onDestroy() {
		release();
		super.onDestroy();
	}

	//
	// ================private==================
	//

	private View rootView;

	private void reLayoutParams() {
		LayoutParams lp = rootView.getLayoutParams();
		if (lp == null) {
			try {
				lp = new ViewGroup.LayoutParams(getWidth(), getHeight());
				rootView.setLayoutParams(lp);
			} catch (Exception e) {
			}
		} else {
			lp.height = getHeight();
			lp.width = getWidth();
			rootView.setLayoutParams(lp);
		}
	}

	private void initAnnotationView() {
		Field[] fs = getClass().getDeclaredFields();
		for (Field field : fs) {
			field.setAccessible(true);
			Fid fid = field.getAnnotation(Fid.class);
			if (fid != null) {
				int id = fid.id();
				if (id != Fid.EMPTY_ID)
					try {
						field.set(this, findView(id));
					} catch (Exception e) {
					}
			}
		}
	}

	//
	// =======================public========================
	//

	public View getRootView() {
		return rootView;
	}

}
