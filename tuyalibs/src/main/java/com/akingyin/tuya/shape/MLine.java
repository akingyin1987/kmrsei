package com.akingyin.tuya.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.akingyin.tuya.Area;

public class MLine extends Shape {
	private Line line = new Line();
	private Arrow arrow = new Arrow();

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public Arrow getArrow() {
		return arrow;
	}

	public void setArrow(Arrow arrow) {
		this.arrow = arrow;
	}

	@Override
	public void draw(Canvas canvas, Paint paint) {
		line.draw(canvas, paint);
		arrow.draw(canvas, paint);
	}

	@Override public Pt getFlexPoint() {
		if(null != line.getFlexPoint()){
			return  line.getFlexPoint();
		}
		if(null != arrow.getFlexPoint()){
			return  arrow.getFlexPoint();
		}
		return  null;
	}

	@Override public Area getMoveArea() {
		if(null != line.getMoveArea() ){
			return  line.getMoveArea();
		}
		if(null != arrow.getMoveArea()){
			return  arrow.getMoveArea();
		}
		return null;
	}

	@Override public void setMoveArea(Area moveArea) {
		super.setMoveArea(moveArea);
    line.setMoveArea(moveArea);
    arrow.setMoveArea(moveArea);
	}

	@Override public void setFlexPoint(Pt flexPoint) {
		super.setFlexPoint(flexPoint);
		if(null == flexPoint){
			line.setFlexPoint(null);
			arrow.setFlexPoint(null);
		}
	}

	@Override public void setArea(Area area) {
		super.setArea(area);
		if(null == area){
			line.setArea(null);
			arrow.setArea(null);
		}
	}

	public void setStart(int x, int y) {
		line.setStart(x, y);
	}

	public void setEnd(int x, int y) {
		line.setEnd(x, y);
		arrow.setStart(x, y);
	}

	public void setArrowEnd(int x, int y) {
		arrow.setEnd(x, y);
	}

	@Override
	public void calculate() {

	}

	@Override public void move(int moveX, int moveY) {

		super.move(moveX, moveY);
		if(null == line.getMoveArea()){
		  line.setMoveArea(arrow.getMoveArea());
    }
    if(null == arrow.getMoveArea()){
		  arrow.setMoveArea(line.getMoveArea());
    }
		line.move(moveX,moveY);
		arrow.move(moveX,moveY);
	}

	@Override
	public void onDrawPoints(Canvas canvas, float radius, Paint pointPaint, Paint pointFillPaint) {
		super.onDrawPoints(canvas, radius, pointPaint, pointFillPaint);
		if(null == start || null == end  ){
			return;
		}

		if(null == pointPaint || null == pointFillPaint){
			return;
		}
		line.onDrawPoints(canvas,radius,pointPaint,pointFillPaint);
		arrow.onDrawPoints(canvas,radius,pointPaint,pointFillPaint);
	}

	@Override public boolean checkPoints(MotionEvent event, int x, int y, float radius) {
		return line.checkPoints(event,x,y,radius)|| arrow.checkPoints(event,x,y,radius);
	}

	@Override public void onMovePoint(int moveX, int moveY) {
		super.onMovePoint(moveX, moveY);
		if(null != line.getFlexPoint() && line.getFlexPoint().eq(arrow.getStart())){
			arrow.setFlexPoint(arrow.getStart());
			System.out.println("------111111--------");
		}else{
			if(null != arrow.getFlexPoint() && line.getEnd().eq(arrow.getFlexPoint())){
				line.setFlexPoint(line.getEnd());
				System.out.println("-----222222------");
			}
		}

		line.onMovePoint(moveX,moveY);
		arrow.onMovePoint(moveX,moveY);
	}
}
