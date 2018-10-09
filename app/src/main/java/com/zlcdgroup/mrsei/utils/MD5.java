package com.zlcdgroup.mrsei.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
	/**
	 * 字符串MD5编码
	 * 
	 * @param str
	 *            被编码的字符串
	 * @return 编码后的字符串
	 */
	public static String md5(String str) {
		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		char[] charArray = str.toCharArray();
		byte[] byteArray = new byte[charArray.length];
		for (int i = 0; i < charArray.length; i++) {
			byteArray[i] = (byte) charArray[i];
		}
		byte[] md5Bytes = md5.digest(byteArray);
		StringBuffer hexValue = new StringBuffer();
		for (int i = 0; i < md5Bytes.length; i++) {
			int val = ((int) md5Bytes[i]) & 0xff;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	public static String getFileMD5(final File file) {
		if (file == null) return null;
		DigestInputStream dis = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			MessageDigest md = null;
			try {
				md = MessageDigest.getInstance("MD5");
				dis = new DigestInputStream(fis, md);
				byte[] buffer = new byte[1024 * 256];
				while (true) {
					if (!(dis.read(buffer) > 0)) break;
				}
				md = dis.getMessageDigest();
				return byte2HexString(md.digest()).toUpperCase();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}

		} catch ( IOException e) {
			e.printStackTrace();
		} finally {
		  if(null != dis){
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static String getMd5ByFile(File file) throws FileNotFoundException {
		String value = null;
		FileInputStream in = new FileInputStream(file);
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024*100];
			int length ;
			while ((length = in.read(buffer)) != -1) {
				md5.update(buffer, 0, length);
			}
		//	MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());

			//md5.update(byteBuffer);
			value = byte2HexString(md5.digest()).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			if(null != in) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}

	public static String byte2HexString(byte[] bytes) {
		StringBuilder ret = new StringBuilder();
		if (bytes != null) {
			// for (Byte b : bytes) {
			// ret += String.format("%02X", b.intValue() & 0xFF);
			// }
			for (int i=0;i<bytes.length;i++) {
				Byte b = bytes[i];
				ret.append( String.format("%02X", b.intValue() & 0xFF));
			}
		}
		return ret.toString();
	}
}
