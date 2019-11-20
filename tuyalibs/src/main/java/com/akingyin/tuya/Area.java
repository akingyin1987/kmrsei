package com.akingyin.tuya;

import com.akingyin.tuya.shape.Pt;

public class Area {

	public Pt topleftPt;
	
	public    Pt    toprightPt;
	
	public    Pt    bottomleftPt;
	
	public    Pt    bottomrightPt;
	
	
	public    boolean   isInArea(Pt   pt){
		if(null == topleftPt || null == toprightPt || 
				null == bottomleftPt || null == bottomrightPt){
			return  false;
		}
     return SpatialRelationUtil.isPolygonContainsPoint(pt,topleftPt,toprightPt,bottomrightPt,bottomleftPt);

	}
	// 计算
	public static double[] rotateVec(Float px, Float py, double ang, boolean isChLen,
							  double newLen) {
		double mathstr[] = new double[2];

		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen ;
			vy = vy / d * newLen ;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}else{
			mathstr[0] = vx;
			mathstr[1] = vy;
		}

		return mathstr;
	}
	
	public static   Area   CreateArea(Pt  statPt  ,Pt  endPt,int  length){
		float ex, ey, sx, sy;
		ex = endPt.x;
		ey = endPt.y;
		sx = statPt.x;
		sy = statPt.y;

		Area   area  =  new   Area();
		int   dis = TuYaUtil.distancePoint(statPt,endPt);
    if(dis<50){
    	return area;
		}

		if(statPt.x == endPt.x){
			area.topleftPt = new  Pt();
			area.topleftPt.x = (int) (sx-length);
			area.topleftPt.y = endPt.y;
			area.bottomleftPt = new Pt(sx-length,sy);
			area.toprightPt = new Pt(sx-length,ey);
			area.bottomrightPt = new  Pt(sx+length,sy);
			return  area;
		}else if(statPt.y == endPt.y){
			area.topleftPt = new  Pt(sx, sy+length);
			area.toprightPt = new  Pt(ex, ey+length);
			area.bottomleftPt = new  Pt(sx, sy-length);
			area.bottomrightPt = new  Pt(ex, ey-length);
			return area;
		}

		double awrad = Math.atan(1);
		double arraow_len = Math.sqrt(length * length + length * length);
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		int x_3 = (int) (ex - arrXY_1[0]);
		int y_3 = (int) (ey - arrXY_1[1]);
		int x_4 = (int) (ex - arrXY_2[0]);
		int y_4 = (int) (ey - arrXY_2[1]);
		area.bottomrightPt = new  Pt((int)( sx+arrXY_1[0]), (int) (sy+arrXY_1[1]));
		area.bottomleftPt = new  Pt((int)( sx+arrXY_2[0]), (int) (sy+arrXY_2[1]));
		area.topleftPt = new  Pt(x_3, y_3);
		area.toprightPt = new  Pt(x_4 ,y_4);

		return area;
		
	}   
	
	
}
