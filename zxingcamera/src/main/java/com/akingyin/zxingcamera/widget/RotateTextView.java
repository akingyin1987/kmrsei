package com.akingyin.zxingcamera.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.Gravity;
import androidx.appcompat.widget.AppCompatTextView;
import static android.R.attr.angle;
/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/11/18 10:55
 */
public class RotateTextView extends AppCompatTextView {

  private static final String TAG = RotateTextView.class.getSimpleName();


  private static final int[] mAttr = {angle};
  private static final int ATTR_ANDROID_ANGLE = 0;
  /**
   * 旋转角度
   */
  private float mAngle = 0;

  public RotateTextView(Context context) {
    super(context);
    setGravity(Gravity.CENTER);
  }

  public RotateTextView(Context context, AttributeSet attrs) {
    super(context, attrs);
    setGravity(Gravity.CENTER);

    TypedArray typedArray = context.obtainStyledAttributes(attrs, mAttr);
    mAngle = typedArray.getFloat(ATTR_ANDROID_ANGLE, 0f);
    typedArray.recycle();
  }

  @Override
  protected void onDraw(Canvas canvas) {
    canvas.save();
    canvas.rotate(mAngle, getWidth() / 2, getHeight() / 2);
    super.onDraw(canvas);
    canvas.restore();
  }

  public void setAngle(float angle) {
    mAngle = angle;
    this.invalidate();
  }
}
