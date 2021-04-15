package com.akingyin.util;

import android.content.Context;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/27 16:54
 */
public class Utils {
  public static int dp2px(Context context, int dpVal) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpVal * scale + 0.5f);
  }

  public static int sp2px(Context context, int spVal) {
    final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
    return (int) (spVal * fontScale + 0.5f);
  }


}
