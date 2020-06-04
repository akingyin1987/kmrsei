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
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class SquareTextView extends AppCompatTextView {
    private int mOffsetTop = 0;
    private int mOffsetLeft = 0;

    public SquareTextView(Context context) {
        super(context);
    }

    public SquareTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SquareTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int dimension = Math.max(width, height);
        if (width > height) {
            mOffsetTop = width - height;
            mOffsetLeft = 0;
        } else {
            mOffsetTop = 0;
            mOffsetLeft = height - width;
        }
        setMeasuredDimension(dimension, dimension);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.translate(mOffsetLeft / 2, mOffsetTop / 2);
        super.draw(canvas);
    }
}
