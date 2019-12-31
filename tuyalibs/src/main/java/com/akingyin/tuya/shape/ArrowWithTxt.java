package com.akingyin.tuya.shape;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.InputType;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.input.DialogInputExtKt;
import com.akingyin.tuya.AppTuyaCallBack;

public class ArrowWithTxt extends Shape {
  private boolean isover;

  public boolean isover() {
    return isover;
  }

  public void setIsover(boolean isover) {
    this.isover = isover;
  }

  public String getArrowTxt() {
    return arrowTxt;
  }

  public void setArrowTxt(String arrowTxt) {
    this.arrowTxt = arrowTxt;
  }

  private Arrow arrow = new Arrow();
  private String arrowTxt = "";
  private float txtX = -1, txtY = -1;

  private double degree;

  @Override public void draw(Canvas canvas, Paint paint) {
    arrow.draw(canvas, paint);
    int len = arrowTxt.length();
    // float[] widths = new float[len];
    // paint.getTextWidths(arrowTxt, 0, len, widths);
    // float totalLen = 0;
    // for (float f : widths) {
    // totalLen += f;
    // }
    if (len > 0) {
      paint.setTextAlign(Paint.Align.CENTER);
      paint.setTextSize(40);
      paint.setTypeface(Typeface.DEFAULT_BOLD);
      paint.setColor(paint.getColor());

      //获取文本路径

      Path desPath = new Path();
      Paint desPaint = new Paint();
      desPaint.setAntiAlias(true);
      desPaint.setColor(Color.WHITE);
      desPaint.setStyle(Paint.Style.FILL);



      Rect rect = new Rect();
      paint.getTextBounds(arrowTxt, 0, arrowTxt.length(), rect);
      int w = rect.width();
      int h = rect.height();
      Paint.FontMetrics fontMetrics = paint.getFontMetrics();
      desPath.moveTo(txtX-w/2-5,txtY-h-5);
      desPath.lineTo(txtX-w/2-5,txtY+10);
      desPath.lineTo(txtX+w/2+5,txtY+10);
      desPath.lineTo(txtX+w/2+5,txtY-h-5);
      desPath.close();

      //if(degree != 0){
      //  Matrix  matrix = new Matrix();
      //  matrix.postRotate((float)degree,txtX,txtY+h/2);
      //  desPath.transform(matrix);
      //}

      //if (degree != 0) {
      //  canvas.rotate((float)degree, txtX, txtY);
      //}

      //if(degree!=0){
      //  canvas.rotate((float)-degree, txtX, txtY);
      //}
      if (arrow.start.x == arrow.end.x) {
        canvas.drawPath(desPath,desPaint);
        canvas.drawText(arrowTxt, txtX, txtY, paint);

      } else {
        degree =
            Math.atan2((arrow.start.y - arrow.end.y) * 1.0, (arrow.start.x - arrow.end.x) * 1.0)
                * 180 / Math.PI;
        //   System.out.println("degree="+degree);
        // double  degree = Math.atan((arrow.start.y-arrow.end.y)*1.0/(arrow.start.x-arrow.end.x)*-1);
        drawText(canvas, arrowTxt, txtX, txtY, paint,
            (float) degree + (arrow.start.x < arrow.end.x ? 180 : 0),desPath,desPaint);

      }


    }
  }

  void drawText(Canvas canvas, String text, float x, float y, Paint paint, float angle,Path  desPath,Paint desPaint) {
    if (angle != 0) {
      canvas.rotate(angle, x, y);
    }
    canvas.drawPath(desPath,desPaint);
    canvas.drawText(text, x, y, paint);
    if (angle != 0) {
      canvas.rotate(-angle, x, y);
    }
  }

  @Override public Pt getStart() {
    return arrow.getStart();
  }

  @Override public Pt getEnd() {
    return arrow.getEnd();
  }

  public void setStart(int x, int y) {
    arrow.setStart(x, y);
  }

  public void setEnd(int x, int y) {
    arrow.setEnd(x, y);
    this.calculate();
  }

  @Override public void calculate() {

    if (arrow.start.x - arrow.end.x != 0) {
      degree =
          Math.atan2((arrow.start.y - arrow.end.y) * 1.0, (arrow.start.x - arrow.end.x) * 1.0) * 180
              / Math.PI;
      //   double   degree2=Math.atan((arrow.start.y-arrow.end.y)*1.0/(arrow.start.x-arrow.end.x))*180/Math.PI;
      txtX = (float) (arrow.start.x + (arrow.end.x - arrow.start.x) / 2);
      txtY = (float)(arrow.start.y + (arrow.end.y - arrow.start.y) / 2);

      if (-90 > degree && degree > -180) {
        txtX = txtX + 10;
        txtY = txtY - 10;
      } else if (0 > degree && degree > -90) {
        txtX = txtX - 10;
        txtY = txtY - 10;
      } else if (90 > degree && degree > 0) {
        txtY = txtY - 10;
        txtX = txtX + 10;
      } else {
        txtY = txtY - 10;
        txtX = txtX - 10;
      }
    }else{
      txtY = (float)(arrow.start.y + (arrow.end.y - arrow.start.y) / 2);
      txtX = txtX + 10;
    }
  }


  public void showDialog(Context activity, final AppTuyaCallBack<String>  callBack) {
    MaterialDialog dialog =  new  MaterialDialog(activity,MaterialDialog.getDEFAULT_BEHAVIOR());
    dialog.setTitle("输入文本");
    dialog = DialogInputExtKt.input(dialog, "请输入", null, null, null, InputType.TYPE_CLASS_TEXT, 10, false,
        false,(materialDialog, text)->{
          arrowTxt = text.toString();
          callBack.call(text.toString());
          return  null;
        });
    dialog.show();

  }
}
