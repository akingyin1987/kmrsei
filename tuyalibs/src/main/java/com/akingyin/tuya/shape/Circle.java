package com.akingyin.tuya.shape;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.view.MotionEvent;
import com.akingyin.tuya.TuYaUtil;

public class Circle extends Shape {
	private Pt center = new Pt();

	public Pt getCenter() {
		return center;
	}

	private int radius;

	public int getRadius() {
		return radius;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		if (center.x == 0 && center.y == 0) {
			return;
		}
		paint.setStyle(Style.STROKE);
		int color = paint.getColor();
		paint.setColor(Color.RED);
		canvas.drawCircle(center.x, center.y, radius, paint);
		paint.setColor(color);
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

	@Override
	public void calculate() {
		int xd = end.x - start.x;
		int yd = end.y - start.y;
		center.x = xd / 2 + start.x;
		center.y = yd / 2 + start.y;
		radius = (int) Math.sqrt(xd * xd + yd * yd) / 2;
	}

	@Override protected boolean drag(Pt pt) {

			if (null == getArea()) {
				return false;
			}
			return getArea().isInArea(pt);

	}


	private   Pt   top,right,bottom,left;

	@Override
	public void onDrawPoints(Canvas canvas, float radius1, Paint pointPaint, Paint pointFillPaint) {
		super.onDrawPoints(canvas, radius, pointPaint, pointFillPaint);
		if(null == start || null == end  ){
			return;
		}

		if(null == pointPaint || null == pointFillPaint){
			return;
		}
		canvas.drawCircle(center.x+radius, center.y, radius1, pointFillPaint);
		canvas.drawCircle(center.x+radius, center.y, radius1, pointPaint);
		canvas.drawCircle(center.x-radius, center.y, radius1, pointFillPaint);
		canvas.drawCircle(center.x-radius, center.y, radius1, pointPaint);

		canvas.drawCircle(center.x, center.y+radius, radius1, pointFillPaint);
		canvas.drawCircle(center.x, center.y+radius, radius1, pointPaint);
		canvas.drawCircle(center.x, center.y-radius, radius1, pointFillPaint);
		canvas.drawCircle(center.x, center.y-radius, radius1, pointPaint);

		top = new Pt(center.x,center.y+radius);
		bottom = new Pt(center.x,center.y-radius);
		left = new Pt(center.x - radius,center.y);
		right = new Pt(center.x+ radius,center.y);
	}

	@Override public boolean checkPoints(MotionEvent event, int x, int y, float radius) {
		setFlexPoint(null);
		setMoveArea(null);
		if(null == top || null == bottom || null == left || null ==right){
			top = new Pt(center.x,center.y+radius);
			bottom = new Pt(center.x,center.y-radius);
			left = new Pt(center.x - radius,center.y);
			right = new Pt(center.x+ radius,center.y);
		}
		if(null != top){
			if(TuYaUtil.distancePoint(top,new Pt(x,y))<radius){
				setFlexPoint(top);
				start.x = bottom.x;
				start.y = bottom.y;
				end.x = top.x;
				end.y = top.y;
				return  true;
			}
		}
		if(null != bottom){
			if(TuYaUtil.distancePoint(bottom,new Pt(x,y))<radius){
				setFlexPoint(bottom);
				start.x = top.x;
				start.y = top.y;
				end.x = bottom.x;
				end.y = bottom.y;
				return  true;
			}
		}

		if(null != left){
			if(TuYaUtil.distancePoint(left,new Pt(x,y))<radius){
				setFlexPoint(left);
				start.x = right.x;
				start.y = right.y;
				end.x = left.x;
				end.y = left.y;
				return  true;
			}
		}

		if(null != right){
			if(TuYaUtil.distancePoint(right,new Pt(x,y))<radius){
				setFlexPoint(right);
				start.x = left.x;
				start.y = left.y;
				end.x = right.x;
				end.y = right.y;
				return  true;
			}
		}
		if(TuYaUtil.distancePoint(center,new Pt(x,y))<radius*2){
			setMoveArea(getArea());
			return  true;
		}
		return  false;

	}

	@Override public void move(int moveX, int moveY) {
		super.move(moveX, moveY);
		if(null != getMoveArea()){
			center.x = moveX;
			center.y = moveY;
		}

	}

	@Override public void onMovePoint(int moveX, int moveY) {
		super.onMovePoint(moveX, moveY);
		if(null != getFlexPoint()){
			System.out.println("拉长----->>>");
			end.x = moveX;
			end.y = moveY;
			calculate();
		}
	}
}
