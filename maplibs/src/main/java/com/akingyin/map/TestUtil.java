/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map;

import java.util.Random;

public class TestUtil {

	/* 产生8位16进制的数 */
	public static String getRandomValue() {
		String str = "";
		for (int i = 0; i < 8; i++) {
			char temp = 0;
			int key = (int) (Math.random() * 2);
			switch (key) {
			case 0:
				temp = (char) (Math.random() * 10 + 48);// 产生随机数字
				break;
			case 1:
				temp = (char) (Math.random() * 6 + 'a');// 产生a-f
				break;
			default:
				break;
			}
			str = str + temp;
		}
		return str;
	}

	/* 产生numSize位16进制的数 */
	public static String getRandomValue(int numSize) {
		String str = "";
		for (int i = 0; i < numSize; i++) {
			char temp = 0;
			int key = (int) (Math.random() * 2);
			switch (key) {
			case 0:
				temp = (char) (Math.random() * 10 + 48);// 产生随机数字
				break;
			case 1:
				temp = (char) (Math.random() * 6 + 'a');// 产生a-f
				break;
			default:
				break;
			}
			str = str + temp;
		}
		return str;
	}
	
	public   static    int  minlat = (int) (29.520000 * 1E6);
	public   static    int  minlng = (int) (106.554000 * 1E6);
	public   static    int  Interval = 370000;
	
	
	public   static    double[]   Latlng(){
		Random   r = new  Random();
		   int rlat = r.nextInt(Interval);
	    int rlng = r.nextInt(Interval);
	    int lat = minlat + rlat;
	    int lng = minlng + rlng;
	    
		return new double[]{lat/1E6,lng/1E6};
		
	}

	public static String MD5(String md5) {
		try {
			java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
			byte[] array = md.digest(md5.getBytes());
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
			}
			return sb.toString();
		} catch (java.security.NoSuchAlgorithmException e) {
		}
		return null;
	}
	
	
}
