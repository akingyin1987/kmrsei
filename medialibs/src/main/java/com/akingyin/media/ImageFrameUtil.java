/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

/**
 * @ Description:
 * @author king
 * @ Date 2017/9/14 14:02
 * @ Version V1.0
 */

public class ImageFrameUtil {

   public   static int[]  getViewFrame(Activity context,View  view){
     Rect frame = new Rect();
     context.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
     int statusBarHeight = frame.top;

     int[] location = new int[2];
     view.getLocationOnScreen(location);
     location[1] += statusBarHeight;

     int width = view.getWidth();
     int height = view.getHeight();
     return  new int[]{ location[0],
         location[1],
         width,
         height};
   }

}
