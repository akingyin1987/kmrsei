/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.base;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import androidx.annotation.NonNull;
import com.akingyin.map.R;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/24 14:03
 */

public class MapLoadingDialog  extends Dialog {

  public MapLoadingDialog(@NonNull Context context) {
    super(context, R.style.LocationProgressStyle);
    getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    setContentView(R.layout.map_loading_dialog);

    setCanceledOnTouchOutside(false);
    setCancelable(false);

  }
}
