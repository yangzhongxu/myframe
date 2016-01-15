package com.yzx.frames.tool.func;

import android.util.Log;
import android.widget.Toast;

import com.yzx.frames.tool.Tool;

public class OutTool extends Tool {

	private static Toast toast;

	public static void toast(String str, boolean islong) {
		if (toast != null)
			toast.cancel();
		toast = Toast.makeText(getApplication(), str, islong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void toast(int res, boolean islong) {
		String str = getApplication().getResources().getString(res);
		if (str != null)
			toast(str, islong);
	}

	public static void log(String str) {
		// if (BuildConfig.DEBUG)
		Log.e("-------->>", str);
	}

}
