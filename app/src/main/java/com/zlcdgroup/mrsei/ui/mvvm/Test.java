/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui.mvvm;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.akingyin.base.ext.Ext;
import com.zlcdgroup.mrsei.R;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2020/7/22 10:58
 */
class Test {
  public  void  test(){
    Animation animation = AnimationUtils.loadAnimation(Ext.INSTANCE.getCtx(), R.anim.alpha_anim);
    animation.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {

      }

      @Override public void onAnimationEnd(Animation animation) {

      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
  }
}
