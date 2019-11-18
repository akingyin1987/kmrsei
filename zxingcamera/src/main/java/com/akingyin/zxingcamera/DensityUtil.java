package com.akingyin.zxingcamera;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

public class DensityUtil {
   
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	public   static   final   double  scale16_9 = 16/9.0;
	public   static   final   double  scale4_3  =  4/3.0;
	
	public  static   int[]    findBsetHeight(Point   cameraPoint,Point screenPoint){
		if(null == screenPoint){
			return null;
		}
		int   width = Math.min(cameraPoint.x, cameraPoint.y);
		int   height = Math.max(cameraPoint.x, cameraPoint.y);
		
		double  scale = width/(double)height;
		int  screanwidth = Math.min(screenPoint.x, screenPoint.y);
		int  screanheight = Math.max(screenPoint.x, screenPoint.y);
		if(Math.abs(scale16_9 - scale) < 0.001){
			//16:9
			int  tempheight = (int) (screanwidth * scale16_9);
			if(tempheight > screanheight){
				//如果屏幕高度没这么高那只能处理宽度
				screanwidth = (int) (screanheight/scale16_9);
				return new int[]{screanwidth,0};
			}
			return new int[]{0,(int) (screanwidth * scale16_9)};
		}else if(Math.abs(scale4_3 - scale) <0.001){
			//4:3
			int  tempheight = (int) (screanwidth * scale4_3);
			if(tempheight > screanheight){
				//如果屏幕高度没这么高那只能处理宽度
				screanwidth = (int) (screanheight/scale4_3);
				return new int[]{screanwidth,0};
			}
			return new int[]{0,(int) (screanwidth * scale4_3)};
		}
		return  null;
	}


	public static DisplayMetrics getScreenWH(Context context) {

		return context.getResources().getDisplayMetrics();

	}
}
