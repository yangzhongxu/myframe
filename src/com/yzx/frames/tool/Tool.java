package com.yzx.frames.tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import com.yzx.frames.tool.func.ThreadTool;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Tool {

	//
	public static int screenWidth;
	public static int screenHeight;

	//
	private static Application context;
	private static String publicDirName = "dir_public";
	private static String publicSharedPreferencesName = "sp_default";

	/**
	 * 初始化
	 * 
	 * @param application
	 * @param publicDirName
	 *            public类型文件的存储文件夹名称
	 */
	public static void init(Application application, String publicDirName, String publicSharedPreferencesName) {
		Tool.context = application;
		if (!Tool.isEmptyStr(publicDirName))
			Tool.publicDirName = publicDirName;
		if (!Tool.isEmptyStr(publicSharedPreferencesName))
			Tool.publicSharedPreferencesName = publicSharedPreferencesName;
		screenHeight = application.getResources().getDisplayMetrics().heightPixels;
		screenWidth = application.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 获取Application对象
	 */
	public static Application getApplication() {
		return context;
	}

	/**
	 * 获取public类型的文件存储的文件夹路径
	 */
	public static String getPublicDirName() {
		return publicDirName;
	}

	/**
	 * 获取SharedPreserences文件名
	 */
	public static String getPublicSharedPreferencesName() {
		return publicSharedPreferencesName;
	}

	/**
	 * 主线程执行
	 */
	public static void runOnMain(Runnable run) {
		new Handler(getApplication().getMainLooper()).post(run);
	}

	/*
	 * ==
	 * 
	 * ↓↓↓↓function ↓↓↓↓
	 * 
	 * ==
	 */

	/**
	 * 判断是否是一个存在的可用的file
	 */
	public static boolean isUsefullFile(File file) {
		return file != null && file.isFile() && file.exists();
	}

	/**
	 * 判断字符串是否有除了空白字符以外内容
	 */
	public static boolean isEmptyStr(String str) {
		return str == null || TextUtils.isEmpty(str.trim());
	}

	/**
	 * 判断是不是有数据的list
	 */
	public static boolean isEmptyList(List<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * 获取sd卡的本地存储路径文件夹file
	 * 
	 * @param folderName
	 *            文件夹名
	 * @return 如果无sd卡,返回手机内部存储的路径file
	 */
	public static File getUsefullExternalDir(String folderName) {
		File sdDir = getApplication().getExternalFilesDir(folderName);
		if (sdDir == null) {
			sdDir = new File(getApplication().getFilesDir(), folderName);
			sdDir.mkdirs();
		}
		return sdDir;
	}

	/**
	 * md5算法
	 * 
	 */
	public static String md5(String text) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte[] bs = md.digest();
			int temp;
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < bs.length; i++) {
				temp = bs[i];
				if (temp < 0)
					temp += 256;
				if (temp < 16) {
					sb.append("0");
				}
				sb.append(Integer.toHexString(temp) + "");
			}
			return sb.toString();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 弹出输入法
	 */
	public static void showSoftInput(final EditText et) {
		ThreadTool.executeLow(new Runnable() {
			public void run() {
				SystemClock.sleep(200);
				et.post(new Runnable() {
					public void run() {
						InputMethodManager is = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
						is.showSoftInput(et, 0);
						et.setSelection(et.getText().toString().length());
					}
				});
			}
		});
	}

	/**
	 * 重新计算view的大小
	 * 
	 * @param target
	 *            目标view
	 * @param newWidth
	 *            新的宽度,如果不变传入null
	 * @param newHeight
	 *            新的高度,如果不变传入null
	 */
	public static void reLayoutParams(View target, Integer newWidth, Integer newHeight) {
		LayoutParams lp = target.getLayoutParams();
		if (lp == null)
			throw new IllegalStateException("没有layoutparams参数,大爷的");
		if (newWidth != null)
			lp.width = newWidth;
		if (newHeight != null)
			lp.height = newHeight;
		target.setLayoutParams(lp);
	}

	/**
	 * 获取通知栏高度
	 * 
	 */
	public static int getNotificationBarHeight() {
		Resources sr = Resources.getSystem();
		int bh = sr.getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
		return bh;
	}

	/**
	 * 获取错误的详细描述
	 * 
	 */
	public static String getErrorInfo(Throwable arg1) {
		Writer writer = new StringWriter();
		PrintWriter pw = new PrintWriter(writer);
		arg1.printStackTrace(pw);
		pw.close();
		String error = writer.toString();
		try {
			writer.close();
		} catch (IOException e) {
		}
		return error;
	}

	/**
	 * 获取应用签名信息
	 * 
	 */
	public static String getSignature(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_SIGNATURES);
		Iterator<PackageInfo> iter = apps.iterator();
		while (iter.hasNext()) {
			PackageInfo packageinfo = iter.next();
			if (packageinfo.packageName.equals(context.getPackageName()))
				return packageinfo.signatures[0].toCharsString();
		}
		return null;
	}

	/**
	 * 改变app语言设置
	 * 
	 * @param lo
	 */
	public static void changeLanguage(Locale lo) {
		Configuration config = getApplication().getResources().getConfiguration();
		config.locale = lo;
		getApplication().getResources().updateConfiguration(config, getApplication().getResources().getDisplayMetrics());
	}

}
