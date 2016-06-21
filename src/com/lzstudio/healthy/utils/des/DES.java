package com.lzstudio.healthy.utils.des;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DES {
	private static byte[] iv = { 1, 9, 9, 4, 0, 8, 0, 1 };

	/**
	 * des加密
	 * 
	 * @param encryptString
	 *            需要加密的字符串
	 * @param encryptKey
	 *            加密的密钥
	 * @return
	 * @throws Exception
	 */
	public static String encryptDES(String encryptString, String encryptKey)
			throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());

		return Base64.encode(encryptedData);
	}

	/**
	 * des解密
	 * 
	 * @param decryptString
	 *            需要解密的字符串
	 * @param decryptKey
	 *            解密的key
	 * @return
	 * @throws Exception
	 */
	public static String decryptDES(String decryptString, String decryptKey)
			throws Exception {
		byte[] byteMi = new Base64().decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);

		return new String(decryptedData);
	}
}