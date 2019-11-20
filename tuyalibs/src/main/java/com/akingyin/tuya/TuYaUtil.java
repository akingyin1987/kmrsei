package com.akingyin.tuya;

import android.graphics.Paint;
import android.graphics.PointF;
import com.akingyin.tuya.shape.Pt;

public class TuYaUtil {

	 // 计算叉乘 |P0P1| × |P0P2| 
    public  static  double Multiply(Pt p1, Pt p2, Pt p0)
    {
        return ((p1.x - p0.x) * (p2.y - p0.y) - (p2.x - p0.x) * (p1.y - p0.y));
    }
    
    
    /**
     * p1围绕center逆时针旋转angle度
     * @param center 
     * @param p1
     * @param angle
     * @return
     */
    public static   Pt PointRotate(Pt center, Pt p1, double angle) {
        Pt tmp = new Pt();
        double angleHude = angle * Math.PI / 180;/*角度变成弧度*/
        double x1 = (p1.x - center.x) * Math.cos(angleHude) + (p1.y - center.y ) * Math.sin(angleHude) + center.x;
        double y1 = -(p1.x - center.x) * Math.sin(angleHude) + (p1.y - center.y) * Math.cos(angleHude) + center.y;
        tmp.x = (int)x1;
        tmp.y = (int)y1;
        return tmp;
    }
    
    /**
     * 三角形面积
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static double triangleArea(Pt a, Pt b, Pt c) 

    {
     double result = Math.abs((a.x * b.y + b.x * c.y + c.x * a.y - b.x * a.y
     - c.x * b.y - a.x * c.y) / 2.0D);
    
     return result;
    }

	public static double getPointsDistance(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
    /**
     * 获取两点间的距离
     * @param p1
     * @param p2
     * @return
     */
    public  static   int  distancePoint(Pt   p1,Pt  p2){
    	
		return (int) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y)*(p1.y - p2.y));
    	
    }
    
    
//    public  static   Pt   Circle_Center(Pt  p1,Pt  p2,double  radius){
//    	Pt   center =  new  Pt();
//    	center.x = (p1.x + p2.x);
//    	center.y = (p1.y + p2.y);
//    	
//    	Pt    poioninCircle = new Pt(0,0);
//    	 double startAngle = ( Math.atan2(p1.x-p2.x,p1.y-p2.y) *180/Math.PI);
//    	if(p1.x < p2.x){
//    		poioninCircle.x = (int) (p1.x+radius*Math.cos(startAngle+60));
//    		poioninCircle.y = (int)(p1.y+radius*Math.sin(startAngle+60));
//    	}else{
//    		poioninCircle.x = (int) (p2.x+radius*Math.cos(startAngle+60));
//    		poioninCircle.y = (int)(p2.y+radius*Math.sin(startAngle+60));
//    	}
//    	
//    	double   d = (center.y-poioninCircle.y)/(center.x-poioninCircle.x);
//    	
//		return poioninCircle;
//    	
//    }
    
    public  static    Pt  Circle_Center(Pt p1,Pt p2,double dRadius)
    {
    	double k = 0.0,k_verticle = 0.0;
    	double mid_x = 0.0,mid_y = 0.0;
    	double a = 1.0;
    	double b = 1.0;
    	double c = 1.0;
    	Pt center1 = new Pt(),center2 = new Pt(),top = new Pt();
    	if(p2.x == p1.x){
    		center1.x = (int) ((int) (p1.x+dRadius)*Math.sin(Math.PI/3));
    		center2.x = (int) ((int) (p1.x-dRadius)*Math.sin(Math.PI/3));
    		
    		center1.y = (p1.y+p2.y)/2;
    		center2.y = (p1.y+p2.y)/2;
    		
    	}else{
    		k = (p2.y - p1.y) / (p2.x - p1.x);
	    	if(k == 0)
	    	{
	    		center1.x = (int) ((p1.x + p2.x) / 2.0);
	    		center2.x = (int) ((p1.x + p2.x) / 2.0);
	    		center1.y = (int) (p1.y + Math.sqrt(dRadius * dRadius -(p1.x - p2.x) * (p1.x - p2.x) / 4.0));
	    		center2.y = (int) (p2.y - Math.sqrt(dRadius * dRadius -(p1.x - p2.x) * (p1.x - p2.x) / 4.0));
	    	}
	    	else
	    	{
	    		k_verticle = -1.0 / k;
	    		mid_x = (p1.x + p2.x) / 2.0;
	    		mid_y = (p1.y + p2.y) / 2.0;
	    		a = 1.0 + k_verticle * k_verticle;
	    		b = -2 * mid_x - k_verticle * k_verticle * (p1.x + p2.x);
	    		c = mid_x * mid_x + k_verticle * k_verticle * (p1.x + p2.x) * (p1.x + p2.x) / 4.0 - 
	    			(dRadius * dRadius - ((mid_x - p1.x) * (mid_x - p1.x) + (mid_y - p1.y) * (mid_y - p1.y)));
	    		
	    		center1.x = (int) ((-1.0 * b + Math.sqrt(b * b -4 * a * c)) / (2 * a));
	    		center2.x = (int) ((-1.0 * b - Math.sqrt(b * b -4 * a * c)) / (2 * a));
	    		center1.y = (int) Y_Coordinates(mid_x,mid_y,k_verticle,center1.x);
	    		center2.y = (int) Y_Coordinates(mid_x,mid_y,k_verticle,center2.x);
	    	}

    	}
    	if(p1.x==p2.x){
    		return center2;
    	}else if(p1.x > p2.x){
    		top.x = (int) (Math.sqrt(dRadius*dRadius/(k_verticle*(k_verticle+1)))+center1.x);
    		top.y = (int) (k_verticle*(top.x - center1.x)+center1.y);
    	}else{
    		top.x = (int) (Math.sqrt(dRadius*dRadius/(k_verticle*(k_verticle+1)))+center2.x);
    		top.y = (int) (k_verticle*(top.x - center2.x)+center2.y);
    	}
    	
    	
    	
		return top;
    }

    /**
     *  AB 与BC垂直
     * @param p1  线段AB A点
     * @param p2  线段AB B点
     * @param dRadius  抛物线顶点到 线段BC中点距离
     * @param center   线段BC C点
     * @return
     */
    public   static   Pt   Circle_Top(Pt p1,Pt p2,double dRadius,Pt center){
    	
    	Pt   top =  new Pt();
    	if(p1.x==p2.x){
    		if(p1.y>p2.y){
    			top.x = (p2.x+center.x)/2;
    			top.y =  (int) (p2.y-dRadius);
    			return top;
    		}
    		top.x = (p2.x+center.x)/2;
			top.y =  (int) (p2.y+dRadius);
    		return top;
    	}
    	if(p1.y == p2.y){
    		if(p1.x<p2.x){
    			top.x = (int) (p2.x+dRadius);
    			top.y = (p2.y+center.y)/2;
    		}else{
    			top.x = (int) (p2.x-dRadius);
    			top.y = (p2.y+center.y)/2;
    		}
    		return top;
    	}
    	
//    	double angel = Math.atan2(center.y, center.x);
//    	double  r = distancePoint(p1, p2);
//    	top.y = (int) (top.y + r *Math.sin(angel));
//    	top.x = (int) (top.x + r *Math.cos(angel));
    	double  k_verticle = -1/((p2.y - center.y) / (double)(p2.x - center.x));
    	System.out.println((p1.x-p2.x)+":"+(p1.y-p2.y));
    	if(p1.x < p2.x ){
    		top.x = (int) (center.x+dRadius *Math.sqrt(1/(k_verticle*k_verticle+1)));
    	}else{
    		top.x = (int) (center.x-dRadius *Math.sqrt(1/(k_verticle*k_verticle+1)));
    	}
    	
    	top.y = (int) (k_verticle*(top.x - center.x)+center.y);
    	//System.out.println("p2="+p2.toString()+":center="+center.toString()+":"+top.toString());
    	//System.out.println(dRadius+":"+distancePoint(center, top));
		return top;
    	
    }
    
 public static   double Y_Coordinates(double x,double y,double k,double x0)
    {
    	return k * x0 - k * x + y;
    }
    
  
 public  static   Pt    Circle_Top(Pt  pt1,Pt  pt2,Pt pt3){
	 Pt  center = new Pt();
	if(pt1.x == pt2.x){
		
		if(pt1.y>pt2.y){
			center.y = pt1.y+ distancePoint(pt1,pt2);
		}else{
			center.y = pt1.y- distancePoint(pt1,pt2);
		}
		center.x = (pt1.x+pt2.x)/2;
		return  center;
	}
	double  k = (pt2.y - pt1.y) / (double)(pt2.x - pt1.x);
	center.x = (int) ((pt2.x+k*k*pt3.x-k*pt3.y+k*pt2.y)/(k*k + 1));
	center.y = (int) (pt3.y+k*center.x-k*pt3.x);
	
	
	return center;
	
 }
	// 点到直线的最短距离的判断 点（x0,y0） 到由两点组成的线段（x1,y1） ,( x2,y2 )
	public static double pointToLine(int x1, int y1, int x2, int y2, int x0,
							   int y0) {
		double space = 0;
		double a, b, c;
		a = lineSpace(x1, y1, x2, y2);// 线段的长度
		b = lineSpace(x1, y1, x0, y0);// (x1,y1)到点的距离
		c = lineSpace(x2, y2, x0, y0);// (x2,y2)到点的距离
		if (c <= 0.000001 || b <= 0.000001) {
			space = 0;
			return space;
		}
		if (a <= 0.000001) {
			space = b;
			return space;
		}
		if (c * c >= a * a + b * b) {
			space = b;
			return space;
		}
		if (b * b >= a * a + c * c) {
			space = c;
			return space;
		}
		double p = (a + b + c) / 2;// 半周长
		double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));// 海伦公式求面积
		space = 2 * s / a;// 返回点到线的距离（利用三角形面积公式求高）
		return space;
	}

	// 计算两点之间的距离
	public static double lineSpace(int x1, int y1, int x2, int y2) {
		double lineLength = 0;
		lineLength = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2)
				* (y1 - y2));
		return lineLength;
	}




	public static int getTextWidth(Paint paint, String str) {
		int iRet = 0;
		if (str != null && str.length() > 0) {
			int len = str.length();
			float[] widths = new float[len];
			paint.getTextWidths(str, widths);
			for (int j = 0; j < len; j++) {
				iRet += (int) Math.ceil(widths[j]);
			}
		}
		return iRet;
	}

	/**
	 * 求直线外一点到直线上的投影点
	 *
	 * @param pLine    线上一点
	 * @param k        斜率
	 * @param pOut     线外一点
	 * @param pProject 投影点
	 */
	public static void getProjectivePoint(PointF pLine, double k, PointF pOut, PointF pProject) {
		//垂线斜率不存在情况
		if (k == 0) {
			pProject.x = pOut.x;
			pProject.y = pLine.y;
		} else {
			pProject.x = (float) ((k * pLine.x + pOut.x / k + pOut.y - pLine.y) / (1 / k + k));
			pProject.y = (float) (-1 / k * (pProject.x - pOut.x) + pOut.y);
		}
	}
	/**
	 * 求pOut在pLine以及pLine2所连直线上的投影点
	 *
	 * @param pLine
	 * @param pLine2
	 * @param pOut
	 * @param pProject
	 */
	public static void getProjectivePoint(PointF pLine, PointF pLine2, PointF pOut, PointF pProject) {
		double k = 0;
		try {
			k = getSlope(pLine.x, pLine.y, pLine2.x, pLine2.y);
		} catch (Exception e) {
			k = 0;
		}
		getProjectivePoint(pLine, k, pOut, pProject);
	}
	/**
	 * 通过两个点坐标计算斜率
	 * 已知A(x1,y1),B(x2,y2)
	 * 1、若x1=x2,则斜率不存在；
	 * 2、若x1≠x2,则斜率k=[y2－y1]/[x2－x1]
	 *
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @throws Exception 如果x1==x2,则抛出该异常
	 */
	public static double getSlope(double x1, double y1, double x2, double y2) throws Exception {

		if (x1 == x2) {
			throw new Exception("Slope is not existence,and div by zero!");
		}
		return (y2 - y1) / (x2 - x1);
	}
}
