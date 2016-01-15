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
	 * ��ʼ��
	 * 
	 * @param application
	 * @param publicDirName
	 *            public�����ļ��Ĵ洢�ļ�������
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
	 * ��ȡApplication����
	 */
	public static Application getApplication() {
		return context;
	}

	/**
	 * ��ȡpublic���͵��ļ��洢���ļ���·��
	 */
	public static String getPublicDirName() {
		return publicDirName;
	}

	/**
	 * ��ȡSharedPreserences�ļ���
	 */
	public static String getPublicSharedPreferencesName() {
		return publicSharedPreferencesName;
	}

	/**
	 * ���߳�ִ��
	 */
	public static void runOnMain(Runnable run) {
		new Handler(getApplication().getMainLooper()).post(run);
	}

	/*
	 * ==
	 * 
	 * ��������function ��������
	 * 
	 * ==
	 */

	/**
	 * �ж��Ƿ���һ�����ڵĿ��õ�file
	 */
	public static boolean isUsefullFile(File file) {
		return file != null && file.isFile() && file.exists();
	}

	/**
	 * �ж��ַ����Ƿ��г��˿հ��ַ���������
	 */
	public static boolean isEmptyStr(String str) {
		return str == null || TextUtils.isEmpty(str.trim());
	}

	/**
	 * �ж��ǲ��������ݵ�list
	 */
	public static boolean isEmptyList(List<?> list) {
		return list == null || list.isEmpty();
	}

	/**
	 * ��ȡsd���ı��ش洢·���ļ���file
	 * 
	 * @param folderName
	 *            �ļ�����
	 * @return �����sd��,�����ֻ��ڲ��洢��·��file
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
	 * md5�㷨
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
	 * �������뷨
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
	 * ���¼���view�Ĵ�С
	 * 
	 * @param target
	 *            Ŀ��view
	 * @param newWidth
	 *            �µĿ��,������䴫��null
	 * @param newHeight
	 *            �µĸ߶�,������䴫��null
	 */
	public static void reLayoutParams(View target, Integer newWidth, Integer newHeight) {
		LayoutParams lp = target.getLayoutParams();
		if (lp == null)
			throw new IllegalStateException("û��layoutparams����,��ү��");
		if (newWidth != null)
			lp.width = newWidth;
		if (newHeight != null)
			lp.height = newHeight;
		target.setLayoutParams(lp);
	}

	/**
	 * ��ȡ֪ͨ���߶�
	 * 
	 */
	public static int getNotificationBarHeight() {
		Resources sr = Resources.getSystem();
		int bh = sr.getDimensionPixelSize(Resources.getSystem().getIdentifier("status_bar_height", "dimen", "android"));
		return bh;
	}

	/**
	 * ��ȡ�������ϸ����
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
	 * ��ȡӦ��ǩ����Ϣ
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
	 * �ı�app��������
	 * 
	 * @param lo
	 */
	public static void changeLanguage(Locale lo) {
		Configuration config = getApplication().getResources().getConfiguration();
		config.locale = lo;
		getApplication().getResources().updateConfiguration(config, getApplication().getResources().getDisplayMetrics());
	}

}
