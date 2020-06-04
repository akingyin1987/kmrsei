/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.map.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.akingyin.map.R;

/**
 * Draws a bubble with a shadow, filled with any color.
 */
class BubbleDrawable extends Drawable {

    private final Drawable mShadow;
    private final Drawable mMask;
    private int mColor = Color.WHITE;

    public BubbleDrawable(Context context) {
        mMask = ContextCompat.getDrawable(context, R.drawable.amu_bubble_mask);
        mShadow = ContextCompat.getDrawable(context, R.drawable.amu_bubble_shadow);
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public void draw(Canvas canvas) {
        mMask.draw(canvas);
        canvas.drawColor(mColor, PorterDuff.Mode.SRC_IN);
        mShadow.draw(canvas);
    }

    @Override
    public void setAlpha(int alpha) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        mMask.setBounds(left, top, right, bottom);
        mShadow.setBounds(left, top, right, bottom);
    }

    @Override
    public void setBounds(Rect bounds) {
        mMask.setBounds(bounds);
        mShadow.setBounds(bounds);
    }

    @Override
    public boolean getPadding(Rect padding) {
        return mMask.getPadding(padding);
    }
}
