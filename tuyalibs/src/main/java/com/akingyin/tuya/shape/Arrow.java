package com.akingyin.tuya.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.MotionEvent;
import com.akingyin.tuya.TuYaUtil;

public class Arrow extends Shape {
	public static final int arrowEdgeLength = 40;
	public static final double arrowAngle = Math.PI / 3;

	private Pt A1 = new Pt(), A2 = new Pt();

	private Path path = null;


	@Override
	public boolean drag(Pt pt) {

		if (null == getArea()) {
			return false;
		}
		return getArea().isInArea(pt);
	}
	@Override
	public void draw(Canvas canvas, Paint paint) {
		if(null == start || null  == end){
			return;
		}
		if (end.x == 0 && end.y == 0) {
			return;
		}
		paint.setStyle(Style.STROKE);
		canvas.drawLine(start.x, start.y, end.x, end.y, paint);
		if (path != null) {
			paint.setStyle(Style.FILL);
			canvas.drawPath(path, paint);
		}


	}

	public void setStart(int x, int y) {
		start.x = x;
		start.y = y;
	}

	public void setEnd(int x, int y) {
		end.x = x;
		end.y = y;
		calculate();
	}


	float ex, ey, sx, sy;
	double H = 40;
	double L = 14;

	@Override
	public void calculate() {
		ex = end.x;
		ey = end.y;
		sx = start.x;
		sy = start.y;

		int x3 = 0;
		int y3 = 0;
		int x4 = 0;
		int y4 = 0;
		double awrad = Math.atan(L / H);
		double arraow_len = Math.sqrt(L * L + H * H);
		double[] arrXY_1 = rotateVec(ex - sx, ey - sy, awrad, true, arraow_len);
		double[] arrXY_2 = rotateVec(ex - sx, ey - sy, -awrad, true, arraow_len);
		double x_3 = ex - arrXY_1[0];
		double y_3 = ey - arrXY_1[1];
		double x_4 = ex - arrXY_2[0];
		double y_4 = ey - arrXY_2[1];
		Double X3 = new Double(x_3);
		x3 = X3.intValue();
		Double Y3 = new Double(y_3);
		y3 = Y3.intValue();
		Double X4 = new Double(x_4);
		x4 = X4.intValue();
		Double Y4 = new Double(y_4);
		y4 = Y4.intValue();

		if (ey == sy) {
			ey = ey + 4;
		} else if (ex == ey) {
			ex = ex + 4;
		} else {
			double slope = Math.atan((ey - sy) / (double) (ex - sx));
			if (ex > sx) {

				ex = (float) (ex + offx * Math.cos(slope));
				ey = (float) (ey + offx * Math.sin(slope));

			} else {
				ex = (float) (ex - offx * Math.cos(slope));
				ey = (float) (ey - offx * Math.sin(slope));

			}

		}

		path = new Path();
		path.moveTo(ex, ey);
		path.lineTo(x3, y3);
		path.lineTo(x4, y4);
		path.close();

	}

	// 计算
	public double[] rotateVec(Float px, Float py, double ang, boolean isChLen,
			double newLen) {
		double mathstr[] = new double[2];
		// 矢量旋转函数，参数含义分别是x分量、y分量、旋转角、是否改变长度
		double vx = px * Math.cos(ang) - py * Math.sin(ang);
		double vy = px * Math.sin(ang) + py * Math.cos(ang);
		if (isChLen) {
			double d = Math.sqrt(vx * vx + vy * vy);
			vx = vx / d * newLen;
			vy = vy / d * newLen;
			mathstr[0] = vx;
			mathstr[1] = vy;
		}
		return mathstr;
	}

	public static final float offx = 4f;

	@Override
	public void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {
		super.onDrawPoints(canvas, radius, pointPaint, pointFillPaint);
    if(null == start || null == end   ){
    	return;
		}

		if(null == pointPaint || null == pointFillPaint){
    	return;
		}
		float   moveX = 0F;

		float   moveY = 0F;

		canvas.drawCircle(start.x+moveX, start.y+moveY, radius, pointFillPaint);
		canvas.drawCircle(start.x+moveX, start.y+moveY, radius, pointPaint);
		canvas.drawCircle(end.x-moveX, end.y-moveY, radius, pointFillPaint);
		canvas.drawCircle(end.x-moveX, end.y-moveY, radius, pointPaint);
		if(null != getArea() && null != getArea().topleftPt){
			Path   path  = new Path();
			path.moveTo(getArea().topleftPt.x,getArea().topleftPt.y);
			path.lineTo(getArea().toprightPt.x,getArea().toprightPt.y);
			path.lineTo(getArea().bottomrightPt.x,getArea().bottomrightPt.y);
			path.lineTo(getArea().bottomleftPt.x,getArea().bottomleftPt.y);
			path.lineTo(getArea().topleftPt.x,getArea().topleftPt.y);
			canvas.drawPath(path,pointPaint);
		}
	}

	@Override public void onMovePoint(int moveX, int moveY) {
		super.onMovePoint(moveX, moveY);
		if(null != getFlexPoint()){
			getFlexPoint().x = moveX;
			getFlexPoint().y = moveY;
			calculate();

		  if(null != getMoveArea()){
				setMoveArea(null);
				setArea(null);
				getArea();
				setMoveArea(getArea());
			}else{
				setArea(null);
		  	 getArea();
			}



		}

	}

	@Override public boolean checkPoints(MotionEvent event, int x, int y, float radius) {

    setFlexPoint(null);
    setMoveArea(null);
		if(TuYaUtil.distancePoint(start,new Pt(x,y))<radius){

			setFlexPoint(start);
			return  true;
		}

		if(TuYaUtil.distancePoint(end,new Pt(x,y))<radius){

			setFlexPoint(end);
			return  true;
		}
		boolean  result = getArea().isInArea(new Pt(x,y));
		if(result){
			System.out.println(" area  point");
			setMoveArea(getArea());
		}
		return  result;
	}

	@Override public void move(int movex, int movey) {
		super.move(movex, movey);

		if(null != getMoveArea()){
			start.x= start.x+movex;
			start.y = start.y+movey;
			end.x = end.x+movex;
			end.y = end.y+movey;
			calculate();
			setArea(null);
			 getArea();
			setMoveArea(getArea());
		}

	}


}
