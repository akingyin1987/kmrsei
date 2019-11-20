package com.akingyin.tuya;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.akingyin.tuya.shape.Pt;
import com.akingyin.tuya.shape.Shape;
import java.io.Serializable;

/**
 * 表位圈
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/4/21 13:14
 */
public class LoctionCircle extends Shape implements Serializable {

  private static final long serialVersionUID = 160996505076327114L;
  /**
   * 0=未处理  1=已扫标签确认表位 2=已忽略此表位
   */
  private    int    status;

  /**
   * true=待删除
   */
  private    boolean   toBeDelect  = false;

  public boolean isToBeDelect() {
    return toBeDelect;
  }

  public void setToBeDelect(boolean toBeDelect) {
    this.toBeDelect = toBeDelect;
  }

  private    String   meterId;

  public String getMeterId() {
    return meterId;
  }

  public void setMeterId(String meterId) {
    this.meterId = meterId;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
    if(status == 0 || status == 2){
      this.meterId = null;
    }
  }

  private Pt center = new Pt();
  private   Pt     movePt = new  Pt();

  public int getRadius() {
    return radius;
  }

  @Override
  public    void   setMovePt(int  x,int y){
    movePt.x  = x;
    movePt.y   = y;
  }

  @Override
  public   int    getMoveX(){
    return  movePt.x;
  }
  @Override
  public   int    getMoveY(){
    return  movePt.y;
  }

  public Pt getCenter() {
    return center;
  }


  private int radius;

  @Override
  public void draw(Canvas canvas, Paint paint) {
    if(radius>0 ){
      paint.setStyle(Paint.Style.STROKE);
      canvas.drawCircle(center.x, center.y, radius, paint);
      return;
    }
    if (end.x == 0 && end.y == 0) {
      return;
    }
    if (center.x == 0 && center.y == 0) {
      return;
    }
    paint.setStyle(Paint.Style.STROKE);

    canvas.drawCircle(center.x, center.y, radius, paint);

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


  public   void   setCenterRadius(int  centerX,int centerY,int  radius){
    this.center.x = centerX;
    this.center.y = centerY;
    this.radius = radius;
  }

  @Override
  public void calculate() {
    int xd = end.x - start.x;
    int yd = end.y - start.y;
    center.x = xd / 2 + start.x;
    center.y = yd / 2 + start.y;
    radius = (int) Math.sqrt(xd * xd + yd * yd) / 2;
  }

  public    boolean   inCricle(Pt  pt){
    if(null == center){
      return  false;
    }
    float  result =
        (float) (Math.sqrt((pt.x-center.x)*(pt.x-center.x)+(pt.y-center.y)*(pt.y-center.y))/2);
    return  result<radius;

  }

  @Override
  public void move(int   movex,int  movey) {
    center.x = center.x +movex;
    center.y = center.y +movey;

  }

}
