package com.akingyin.tuya.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.MotionEvent;
import com.akingyin.tuya.Area;

public abstract  class Shape {
	
	 public  static   float  minRadius  = 10f;
	
	 protected  Pt start = new Pt(), end = new Pt();

	public Pt getStart() {
		return start;
	}



	public Pt getEnd() {
		return end;
	}



	private   Pt     movePt = new  Pt();
	
	 private Area area;
	
	 private   Paint    mypaint;


	 private    Pt    movePointPt = null;

	/**
	 * 伸缩某个节点坐标
	 */
	private   Pt    flexPoint;

	/**
	 * 移动区域
	 */
	private   Area   moveArea;

	public Area getMoveArea() {
		return moveArea;
	}

	public void setMoveArea(Area moveArea) {
		this.moveArea = moveArea;
	}

	public Pt getFlexPoint() {
		return flexPoint;
	}

	public void setFlexPoint(Pt flexPoint) {
		this.flexPoint = flexPoint;
	}

	public Pt getMovePointPt() {
		return movePointPt;
	}

	public void setMovePointPt(Pt movePointPt) {
		this.movePointPt = movePointPt;
	}

	public Paint getMypaint() {
		return mypaint;
	}

	public void setMypaint(Paint mypaint) {
		this.mypaint = mypaint;
	}

	public Area getArea() {

		if(null == area || null == area.topleftPt){

			area = Area.CreateArea(start, end, 30);
		}
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}


	public    void   setMovePt(int  x,int y){
		 movePt.x  = x;
		 movePt.y   = y;
	}
	
	public   int    getMoveX(){
        return  movePt.x;
	}
	
	public   int    getMoveY(){
		return  movePt.y;
	}
	
	
	private   TuyaAction    tuyaAction = TuyaAction.NULL;
	
	
	public TuyaAction getTuyaAction() {
		return tuyaAction;
	}

	public void setTuyaAction(TuyaAction tuyaAction) {
		this.tuyaAction = tuyaAction;
	}

	public abstract void draw(Canvas canvas, Paint paint);

	public abstract void calculate();
	
	 protected boolean  drag(Pt   pt){
	 	return  false;
	 }
    
	 protected boolean   isEmpty(){
	  	return  false;
	 }

	/**
	 *  //旋转角度
	 * @param angle
	 */
	protected    void  spin(int  angle){}

	/**
	 * //坐标整体移动
	 * @param moveX
	 * @param moveY
	 */
	public   void   move(int   moveX,int  moveY){}

	/**
	 * 画节点小园
	 * @param canvas
	 * @param pointPaint
	 * @param pointFillPaint
	 */
	public     void   onDrawPoints(Canvas  canvas,float radius,Paint  pointPaint,Paint pointFillPaint){

	}

	/**
	 *
	 * @param event
	 * @param x
	 * @param y
	 * @param radius
	 */
	public     boolean     checkPoints(MotionEvent  event,int  x,int y,float radius){
   return  false;
	}

	/**
	 * 移动某个节点坐标
	 * @param moveX
	 * @param moveY
	 */
	 public     void      onMovePoint(int  moveX,int  moveY){

	}
}
