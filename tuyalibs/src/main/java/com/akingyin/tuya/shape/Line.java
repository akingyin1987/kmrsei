package com.akingyin.tuya.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.view.MotionEvent;
import com.akingyin.tuya.TuYaUtil;

public class Line extends Shape {

	@Override
	public void draw(Canvas canvas, Paint paint) {
		if (end.x == 0 && end.y == 0) {
			return;
		}
		paint.setStyle(Style.STROKE);
		canvas.drawLine(start.x, start.y, end.x, end.y, paint);
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

	}

	@Override public void move(int moveX, int moveY) {
		super.move(moveX, moveY);

		if(null != getMoveArea()){
			start.x= start.x+moveX;
			start.y = start.y+moveY;
			end.x = end.x+moveX;
			end.y = end.y+moveY;
			calculate();
			setArea(null);
			getArea();
			setMoveArea(getArea());
		}
	}

	@Override
	public void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {
		super.onDrawPoints(canvas, radius, pointPaint, pointFillPaint);

		float   moveX = 0F;

		float   moveY = 0F;

		canvas.drawCircle(start.x+moveX, start.y+moveY, radius, pointFillPaint);
		canvas.drawCircle(start.x+moveX, start.y+moveY, radius, pointPaint);
		canvas.drawCircle(end.x-moveX, end.y-moveY, radius, pointFillPaint);
		canvas.drawCircle(end.x-moveX, end.y-moveY, radius, pointPaint);
		if(null != getArea() && null != getArea().topleftPt){
			Path path  = new Path();
			path.moveTo(getArea().topleftPt.x,getArea().topleftPt.y);
			path.lineTo(getArea().toprightPt.x,getArea().toprightPt.y);
			path.lineTo(getArea().bottomrightPt.x,getArea().bottomrightPt.y);
			path.lineTo(getArea().bottomleftPt.x,getArea().bottomleftPt.y);
			path.lineTo(getArea().topleftPt.x,getArea().topleftPt.y);
			canvas.drawPath(path,pointPaint);
		}
	}

	@Override public boolean checkPoints(MotionEvent event, int x, int y, float radius) {
		setFlexPoint(null);
		setMoveArea(null);
		if(TuYaUtil.distancePoint(start,new Pt(x,y))<radius){
			System.out.println("start point");
			setFlexPoint(start);
			return  true;
		}

		if(TuYaUtil.distancePoint(end,new Pt(x,y))<radius){
			System.out.println("end  point");
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

	@Override public void onMovePoint(int moveX, int moveY) {
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
}
