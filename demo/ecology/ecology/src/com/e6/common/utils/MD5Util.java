package com.e6.common.utils;

import java.security.MessageDigest;

/**
 * MD5码工具类
 * 
 * @author HM
 *
 */
public class MD5Util {

	public static String md5(String src) throws Exception {
		StringBuilder sb = new StringBuilder();
		MessageDigest md5;
		md5 = MessageDigest.getInstance("md5");
		md5.update(src.getBytes());
		for (byte b : md5.digest()) {
			sb.append(String.format("%02X", b)); // 10进制转16进制，X 表示以十六进制形式输出，02 表示不足两位前面补0输出
		}
		return sb.toString();
	}

	/**
	 * md5加密
	 * 
	 * @param str
	 * @return
	 */
	public static String getMD5(String str) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] b = md5.digest(str.getBytes("UTF-8"));
			String h = ByteHex2String(b);
			return h;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	public static String ByteHex2String(byte[] b) {
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			int val = b[i] & 0xFF;
			if (val < 16)
				hexValue.append("0");
			hexValue.append(Integer.toHexString(val));
		}

		String h = hexValue.toString().toUpperCase();
		return h;
	}

}
