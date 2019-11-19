/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import androidx.viewpager.widget.ViewPager;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/2/10 17:25
 */

public class ContentViewPager  extends ViewPager {
  public ContentViewPager(Context context) {
    super(context);
  }
  public ContentViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int index = getCurrentItem();
    int height = 0;
    View v = (View) getAdapter().instantiateItem(this, index);
    if (v != null) {
      v.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
      height = v.getMeasuredHeight();
    }
    heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
    super.onMeasure(widthMeasureSpec, heightMeasureSpec);
  }


}
