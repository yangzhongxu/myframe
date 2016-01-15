package com.yzx.frames.tool.secret;

import java.security.Key;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 
 * @ClassName: com.qust.rollcallstudent.utils.DESUtil
 * @Description: DES���ܽ��ܹ��߰�
 * @author yzx
 * @date 2014-11-13 ����8:40:56
 * 
 */
public class DESUtil {

	// ���ܷ�ʽ / ģʽ / ���
	private static final String ALGORITHM_DES = "DES/CBC/PKCS5Padding";
	// ��ʼ����
	private static final String INIT_VECTOR = "12345678";

	/**
	 * DES�㷨������
	 * 
	 * @param data
	 *            �������ַ���
	 * @param key
	 *            ����˽Կ�����Ȳ��ܹ�С��8λ
	 * @return ���ܺ���ֽ����飬һ����Base64����ʹ��
	 */
	public static String encode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, paramSpec);
			byte[] bytes = cipher.doFinal(data.getBytes());
			return byte2String(bytes);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * DES�㷨������
	 * 
	 * @param data
	 *            �������ַ���
	 * @param key
	 *            ����˽Կ�����Ȳ��ܹ�С��8λ
	 * @return ���ܺ���ֽ�����
	 */
	public static String decode(String key, String data) {
		if (data == null)
			return null;
		try {
			DESKeySpec dks = new DESKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			Key secretKey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(ALGORITHM_DES);
			IvParameterSpec iv = new IvParameterSpec(INIT_VECTOR.getBytes());
			AlgorithmParameterSpec paramSpec = iv;
			cipher.init(Cipher.DECRYPT_MODE, secretKey, paramSpec);
			return new String(cipher.doFinal(byte2hex(data.getBytes())));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * ������ת�ַ���
	 * 
	 */
	private static String byte2String(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b != null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toUpperCase(Locale.CHINA);
	}

	/**
	 * ������ת����16����
	 * 
	 */
	private static byte[] byte2hex(byte[] b) {
		if ((b.length % 2) != 0)
			throw new IllegalArgumentException();
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

}