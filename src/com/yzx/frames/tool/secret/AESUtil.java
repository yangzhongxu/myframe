package com.yzx.frames.tool.secret;

import android.annotation.SuppressLint;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class AESUtil {

	public static String encrypt(String key, String data) {
		byte[] arrayOfByte = null;
		try {
			arrayOfByte = encrypt(getRawKey(key.getBytes()), data.getBytes());
		} catch (Exception localException) {
			localException.printStackTrace();
		}
		if (arrayOfByte != null)
			return toHex(arrayOfByte);
		return null;
	}

	public static String decrypt(String key, String data) {
		try {
			byte[] rawKey = getRawKey(key.getBytes());
			byte[] dataByte = toByte(data);
			return new String(decrypt(rawKey, dataByte));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressLint("TrulyRandom")
	private static byte[] getRawKey(byte[] paramArrayOfByte) throws Exception {
		KeyGenerator localKeyGenerator = KeyGenerator.getInstance("AES");
		SecureRandom localSecureRandom;
		(localSecureRandom = SecureRandom.getInstance("SHA1PRNG")).setSeed(paramArrayOfByte);
		localKeyGenerator.init(128, localSecureRandom);
		return localKeyGenerator.generateKey().getEncoded();
	}

	private static byte[] encrypt(byte[] keyByte, byte[] dataByte) throws Exception {
		SecretKeySpec sks = new SecretKeySpec(keyByte, "AES");
		Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		localCipher.init(1, sks, new IvParameterSpec(new byte[localCipher.getBlockSize()]));
		return localCipher.doFinal(dataByte);
	}

	private static byte[] decrypt(byte[] keyByte, byte[] dataByte) throws Exception {
		SecretKeySpec sks = new SecretKeySpec(keyByte, "AES");
		Cipher localCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		localCipher.init(2, sks, new IvParameterSpec(new byte[localCipher.getBlockSize()]));
		return localCipher.doFinal(dataByte);
	}

	private static byte[] toByte(String paramString) {
		int i;
		byte[] arrayOfByte = new byte[i = paramString.length() / 2];
		for (int j = 0; j < i; j++)
			arrayOfByte[j] = Integer.valueOf(paramString.substring(2 * j, 2 * j + 2), 16).byteValue();
		return arrayOfByte;
	}

	private static String toHex(byte[] paramArrayOfByte) {
		if (paramArrayOfByte == null)
			return "";
		StringBuffer localStringBuffer = new StringBuffer(2 * paramArrayOfByte.length);
		for (int i = 0; i < paramArrayOfByte.length; i++)
			appendHex(localStringBuffer, paramArrayOfByte[i]);
		return localStringBuffer.toString();
	}

	private static void appendHex(StringBuffer paramStringBuffer, byte paramByte) {
		paramStringBuffer.append("0123456789ABCDEF".charAt(paramByte >> 4 & 0xF)).append("0123456789ABCDEF".charAt(paramByte & 0xF));
	}

}