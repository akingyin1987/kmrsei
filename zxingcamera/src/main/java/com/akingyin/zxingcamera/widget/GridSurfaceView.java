package com.akingyin.zxingcamera.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceView;
import android.view.WindowManager;
import com.akingyin.zxingcamera.R;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/11/18 16:42
 */
public class GridSurfaceView extends SurfaceView {

  /**
   * 竖向线条数,可通过布局属性设置列数
   */
  private int linesX = 2;
  /**
   * 竖向线条数，可通过布局属性设置行数
   */
  private int linesY = 2;

  private int width;
  private int height;
  private Paint mPaint = null;
  public GridSurfaceView(Context context) {
    this(context, null);
  }

  public GridSurfaceView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public GridSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GridSurfaceView);
    linesX = a.getInteger(R.styleable.GridSurfaceView_linesX, linesX);
    linesY = a.getInteger(R.styleable.GridSurfaceView_linesY, linesY);
    a.recycle();
    init();
  }
  WindowManager wm = null;
  private void init(){
    wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    mPaint = new Paint();
    mPaint.setColor(Color.WHITE);
    mPaint.setStyle(Paint.Style.FILL);
    mPaint.setStrokeWidth(1);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);


  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    width = getWidth();
    height = getHeight();
    if(width>0 && linesX>0&& linesY>0){
      int x = width/(linesX + 1);
      int y = height/(linesY + 1);
      for(int i = 1 ; i <= linesX ; i++){
        canvas.drawLine(x * i, 0, x * i, height, mPaint);
      }

      for (int i = 1; i <= linesY; i++) {
        canvas.drawLine(0, y * i, width, y * i, mPaint);
      }

    }

  }
}
