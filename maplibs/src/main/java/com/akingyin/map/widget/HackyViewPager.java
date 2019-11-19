/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import androidx.viewpager.widget.ViewPager;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/25 11:37
 */

public class HackyViewPager  extends ViewPager {

  private boolean isLocked;

  public HackyViewPager(Context context) {
    super(context);
    isLocked = false;
  }

  public HackyViewPager(Context context, AttributeSet attrs) {
    super(context, attrs);
    isLocked = false;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    if (!isLocked) {
      try {
        return super.onInterceptTouchEvent(ev);
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
        return false;
      }
    }
    return false;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return !isLocked && super.onTouchEvent(event);
  }

  public void toggleLock() {
    isLocked = !isLocked;
  }

  public void setLocked(boolean isLocked) {
    this.isLocked = isLocked;
  }

  public boolean isLocked() {
    return isLocked;
  }
}
