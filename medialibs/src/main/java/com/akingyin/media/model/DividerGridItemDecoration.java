/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.model;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * * *                #                                                   #
 * #                       _oo0oo_                     #
 * #                      o8888888o                    #
 * #                      88" . "88                    #
 * #                      (| -_- |)                    #
 * #                      0\  =  /0                    #
 * #                    ___/`---'\___                  #
 * #                  .' \\|     |# '.                 #
 * #                 / \\|||  :  |||# \                #
 * #                / _||||| -:- |||||- \              #
 * #               |   | \\\  -  #/ |   |              #
 * #               | \_|  ''\---/''  |_/ |             #
 * #               \  .-\__  '-'  ___/-. /             #
 * #             ___'. .'  /--.--\  `. .'___           #
 * #          ."" '<  `.___\_<|>_/___.' >' "".         #
 * #         | | :  `- \`.;`\ _ /`;.`/ - ` : | |       #
 * #         \  \ `_.   \_ __\ /__ _/   .-` /  /       #
 * #     =====`-.____`.___ \_____/___.-`___.-'=====    #
 * #                       `=---='                     #
 * #     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~   #
 * #                                                   #
 * #               佛祖保佑         永无BUG              #
 * #
 *
 * @ Description:                                          #
 *
 * @Author king
 * @ Date 2016/11/25 12:08
 * @ Version V1.0
 */

public class DividerGridItemDecoration extends  RecyclerView.ItemDecoration{

  private static final int[] ATTRS = new int[] { android.R.attr.listDivider };
  private Drawable mDivider;

  public DividerGridItemDecoration(Context context)
  {
    final TypedArray a = context.obtainStyledAttributes(ATTRS);
    mDivider = a.getDrawable(0);
    a.recycle();
  }

  @Override
  public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state)
  {

    drawHorizontal(c, parent);
    drawVertical(c, parent);

  }

  private int getSpanCount(RecyclerView parent)
  {
    // 列数
    int spanCount = -1;
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    if (layoutManager instanceof GridLayoutManager)
    {

      spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
    } else if (layoutManager instanceof StaggeredGridLayoutManager)
    {
      spanCount = ((StaggeredGridLayoutManager) layoutManager)
          .getSpanCount();
    }
    return spanCount;
  }

  public void drawHorizontal(Canvas c, RecyclerView parent)
  {
    int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++)
    {
      final View child = parent.getChildAt(i);
      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
          .getLayoutParams();
      final int left = child.getLeft() - params.leftMargin;
      final int right = child.getRight() + params.rightMargin
          + mDivider.getIntrinsicWidth();
      final int top = child.getBottom() + params.bottomMargin;
      final int bottom = top + mDivider.getIntrinsicHeight();
      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  public void drawVertical(Canvas c, RecyclerView parent)
  {
    final int childCount = parent.getChildCount();
    for (int i = 0; i < childCount; i++)
    {
      final View child = parent.getChildAt(i);

      final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
          .getLayoutParams();
      final int top = child.getTop() - params.topMargin;
      final int bottom = child.getBottom() + params.bottomMargin;
      final int left = child.getRight() + params.rightMargin;
      final int right = left + mDivider.getIntrinsicWidth();

      mDivider.setBounds(left, top, right, bottom);
      mDivider.draw(c);
    }
  }

  private boolean isLastColum(RecyclerView parent, int pos, int spanCount,
      int childCount)
  {
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    if (layoutManager instanceof GridLayoutManager)
    {
      if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
      {
        return true;
      }
    } else if (layoutManager instanceof StaggeredGridLayoutManager)
    {
      int orientation = ((StaggeredGridLayoutManager) layoutManager)
          .getOrientation();
      if (orientation == StaggeredGridLayoutManager.VERTICAL)
      {
        if ((pos + 1) % spanCount == 0)// 如果是最后一列，则不需要绘制右边
        {
          return true;
        }
      } else
      {
        childCount = childCount - childCount % spanCount;
        if (pos >= childCount)// 如果是最后一列，则不需要绘制右边
          return true;
      }
    }
    return false;
  }

  private boolean isLastRaw(RecyclerView parent, int pos, int spanCount,
      int childCount)
  {
    RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
    if (layoutManager instanceof GridLayoutManager)
    {
      childCount = childCount - childCount % spanCount;
      if (pos >= childCount)// 如果是最后一行，则不需要绘制底部
        return true;
    } else if (layoutManager instanceof StaggeredGridLayoutManager)
    {
      int orientation = ((StaggeredGridLayoutManager) layoutManager)
          .getOrientation();
      // StaggeredGridLayoutManager 且纵向滚动
      if (orientation == StaggeredGridLayoutManager.VERTICAL)
      {
        childCount = childCount - childCount % spanCount;
        // 如果是最后一行，则不需要绘制底部
        if (pos >= childCount) {
          return true;
        }
      } else
      // StaggeredGridLayoutManager 且横向滚动
      {
        // 如果是最后一行，则不需要绘制底部
        if ((pos + 1) % spanCount == 0)
        {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void getItemOffsets(Rect outRect, int itemPosition,
      RecyclerView parent)
  {
    int spanCount = getSpanCount(parent);
    int childCount = parent.getAdapter().getItemCount();
    if (isLastRaw(parent, itemPosition, spanCount, childCount))// 如果是最后一行，则不需要绘制底部
    {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
    } else if (isLastColum(parent, itemPosition, spanCount, childCount))// 如果是最后一列，则不需要绘制右边
    {
      outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    } else
    {
      outRect.set(0, 0, mDivider.getIntrinsicWidth(),
          mDivider.getIntrinsicHeight());
    }
  }
}
