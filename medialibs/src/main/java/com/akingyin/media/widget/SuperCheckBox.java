/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SoundEffectConstants;
import androidx.appcompat.widget.AppCompatCheckBox;

/**
 * @author king
 * @version V1.0
 * @ Description:
 *
 * @ Date 2017/12/5 11:43
 */
public class SuperCheckBox extends AppCompatCheckBox {

  public SuperCheckBox(Context context) {
    super(context);
  }

  public SuperCheckBox(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public SuperCheckBox(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  @Override
  public boolean performClick() {
    final boolean handled = super.performClick();
    if (!handled) {
      // View only makes a sound effect if the onClickListener was
      // called, so we'll need to make one here instead.
      playSoundEffect(SoundEffectConstants.CLICK);
    }
    return handled;
  }
}
