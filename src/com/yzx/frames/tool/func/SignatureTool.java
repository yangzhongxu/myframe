package com.yzx.frames.tool.func;

import java.security.MessageDigest;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SignatureTool {

	/**
	 * 获取签名
	 * 
	 * @param context
	 * @param pckName
	 *            包名
	 * @return 签名md5
	 */
	public static String getSignMd5(Context context, String pckName) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(pckName, PackageManager.GET_SIGNATURES);
			return hexdigest(info.signatures[0].toByteArray());
		} catch (Exception e) {
		}
		return null;
	}

	private static final char[] hexDigits = { 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102 };

	private static String hexdigest(byte[] bs) {
		try {
			MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
			localMessageDigest.update(bs);
			byte[] arrayOfByte = localMessageDigest.digest();
			char[] arrayOfChar = new char[32];
			int i = 0;
			int j = 0;
			while (true) {
				if (i >= 16)
					return new String(arrayOfChar);
				int k = arrayOfByte[i];
				int m = j + 1;
				arrayOfChar[j] = hexDigits[(0xF & k >>> 4)];
				j = m + 1;
				arrayOfChar[m] = hexDigits[(k & 0xF)];
				i++;
			}
		} catch (Exception localException) {
		}
		return null;
	}

}
