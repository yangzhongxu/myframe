package com.yzx.frames.tool.fm.second;

import java.util.HashMap;

import android.support.v4.app.FragmentActivity;

public class SingleManager {

	public static final String TAG = "single_manager_tag";
	private static final HashMap<String, BaseSingleDialogFragment> fmap = new HashMap<String, BaseSingleDialogFragment>();

	/** show fragment, not repeat */
	public static void show(BaseSingleDialogFragment f, FragmentActivity activity) {
		if (!fmap.containsKey(f.getClass().getSimpleName()))
			f.show(activity.getSupportFragmentManager(), TAG);
	}

	/** add showing */
	public static void add(BaseSingleDialogFragment f) {
		fmap.put(f.getClass().getSimpleName(), f);
	}

	/** remove showing */
	public static void remove(Class<?> clz) {
		fmap.remove(clz.getSimpleName());
	}

	/** get fragment */
	public static BaseSingleDialogFragment get(Class<?> clz) {
		return fmap.get(clz.getSimpleName());
	}

}
