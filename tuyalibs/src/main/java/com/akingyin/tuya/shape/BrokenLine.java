package com.akingyin.tuya.shape;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import com.akingyin.tuya.Area;

/**
 * 折线+角度显示
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/8/27 15:18
 */
public class BrokenLine extends Shape {

  private Line startLine =  new Line();
  private Line endLine  =  new Line();

  public Line getStartLine() {
    return startLine;
  }

  public void setStartLine(Line startLine) {
    this.startLine = startLine;
  }

  public Line getEndLine() {
    return endLine;
  }

  public void setEndLine(Line endLine) {
    this.endLine = endLine;
  }


  private Path   mPath = new Path();
  @Override public void draw(Canvas canvas, Paint paint) {
      startLine.draw(canvas,paint);
      if(endLine.start.x>0){
        endLine.draw(canvas,paint);
      }
      if(endLine.end.x>0){
        mPath.reset();
        mPath.moveTo(sPt.x,sPt.y);
        mPath.quadTo(cPt.x,cPt.y,ePt.x,ePt.y);
        canvas.drawPath(mPath,paint);

        if(null != angle){
          System.out.println("angle="+angle);
          float  textsize = paint.getTextSize();
          paint.setTextSize(30);
          float width =  paint.getStrokeWidth();
          paint.setStrokeWidth(3f);
          canvas.drawText(String.valueOf(angle.longValue()),cPt.x,cPt.y,paint);
          paint.setTextSize(textsize);
          paint.setStrokeWidth(width);
        }
      }

  }

  @Override public Pt getFlexPoint() {
    if(null != startLine.getFlexPoint()){
      return  startLine.getFlexPoint();
    }
    if(null != endLine.getFlexPoint()){
      return  endLine.getFlexPoint();
    }
    return null;
  }


  @Override public Area getMoveArea() {
    if(null != startLine.getMoveArea() ){
      return  startLine.getMoveArea();
    }
    if(null != endLine.getMoveArea()){
      return  endLine.getMoveArea();
    }
    return null;
  }
  @Override public void setMoveArea(Area moveArea) {
    super.setMoveArea(moveArea);
    startLine.setMoveArea(moveArea);
    endLine.setMoveArea(moveArea);
  }

  @Override public void setFlexPoint(Pt flexPoint) {
    super.setFlexPoint(flexPoint);
    if(null == flexPoint){
      startLine.setFlexPoint(null);
      endLine.setFlexPoint(null);
    }
  }
  @Override public void setArea(Area area) {
    super.setArea(area);
    if(null == area){
      startLine.setArea(null);
      endLine.setArea(null);
    }
  }
  public void setStart(int x, int y) {
    startLine.setStart(x, y);
  }

  public void setEnd(int x, int y) {
    startLine.setEnd(x, y);
    endLine.setStart(x, y);

  }

  public void setLineEnd(int x, int y) {
    endLine.setEnd(x, y);
    calculate();
  }


  @Override public void move(int moveX, int moveY) {

    super.move(moveX, moveY);
    if(null == startLine.getMoveArea()){
      startLine.setMoveArea(startLine.getMoveArea());
    }
    if(null == endLine.getMoveArea()){
      endLine.setMoveArea(endLine.getMoveArea());
    }
    startLine.move(moveX,moveY);
    endLine.move(moveX,moveY);
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
    startLine.onDrawPoints(canvas,radius,pointPaint,pointFillPaint);
    endLine.onDrawPoints(canvas,radius,pointPaint,pointFillPaint);
  }

  @Override public boolean checkPoints(MotionEvent event, int x, int y, float radius) {
    return startLine.checkPoints(event,x,y,radius)|| endLine.checkPoints(event,x,y,radius);
  }


  @Override public void onMovePoint(int moveX, int moveY) {
    super.onMovePoint(moveX, moveY);
    if(null != startLine.getFlexPoint() && startLine.getFlexPoint().eq(endLine.getStart())){
      endLine.setFlexPoint(endLine.getStart());
      System.out.println("------111111--------");
    }else{
      if(null != endLine.getFlexPoint() && startLine.getEnd().eq(endLine.getFlexPoint())){
        startLine.setFlexPoint(startLine.getEnd());
        System.out.println("-----222222------");
      }
    }

    startLine.onMovePoint(moveX,moveY);
    endLine.onMovePoint(moveX,moveY);
    calculate();
  }

  private   Pt    sPt = new Pt(),ePt = new Pt(),cPt = new Pt();
  private   Double  angle =  null;
  @Override public void calculate() {
      if(startLine.start.x>0 && endLine.start.x>0){

         Double   k1 = null,k2=null,k3 = null;

         if(startLine.start.y != startLine.end.y){
            k1 = (startLine.start.y*1.0-startLine.end.y)/(startLine.start.x*1.0-startLine.end.x);
         }
         if(endLine.start.y != endLine.end.y){
           k2 = (endLine.start.y*1.0-endLine.end.y)/(endLine.start.x*1.0-endLine.end.x);
           cPt.x = endLine.start.x;
           cPt.y = endLine.start.y;
         }
         if(null == k1 && null != k2){
           angle = Math.toDegrees(Math.atan(k2));
           k3 = Math.tan(angle/2);

         }else if(null != k1 && null ==k2){
           angle = Math.toDegrees(Math.atan(k1));
           k3 = Math.tan(angle/2);

         }else {
           if(null != k1 ){
             angle = Math.toDegrees(Math.atan((k1-k2)/(1+k1*k2)));
             if(angle<0){
               angle=180+angle;
             }

           }
         }
         sPt.x = ((startLine.end.x+startLine.start.x)/2+startLine.end.x)/2;
         sPt.y = ((startLine.end.y+startLine.start.y)/2+startLine.end.y)/2;
         ePt.x = ((endLine.start.x+endLine.end.x)/2+endLine.start.x)/2;
         ePt.y = ((endLine.start.y+endLine.end.y)/2+endLine.start.y)/2;
        //sPt.x = (int) (startLine.end.y+50*Math.sin(Math.atan2(startLine.start.y*1.0-startLine.end.y,startLine.start.x*1.0-startLine.end.x)));
        //sPt.y = (int) (startLine.end.x+50*Math.cos(Math.atan2(startLine.start.y*1.0-startLine.end.y,startLine.start.x*1.0-startLine.end.x)));
        //ePt.x = (int) (startLine.end.y+50*Math.sin(Math.atan(k2)));
        //ePt.y = (int) (startLine.end.x+50*Math.cos(Math.atan(k2)));
         cPt.x = (sPt.x+ePt.x+endLine.start.x)/3;
         cPt.y =  (sPt.y+ePt.y+endLine.start.y)/3;


        System.out.println("spt="+sPt.toString()+":"+ePt.toString()+":"+cPt.toString());

      }
  }
}
