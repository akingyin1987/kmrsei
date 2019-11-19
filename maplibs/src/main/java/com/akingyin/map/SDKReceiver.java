/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.baidu.mapapi.SDKInitializer;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/9/7 17:08
 * @ Version V1.0
 */

public class SDKReceiver  extends BroadcastReceiver {

  @Override
  public void onReceive(Context context, Intent intent) {
    String action = intent.getAction();
    if(action.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
      // 网络出错，相应处理
      Toast.makeText(context,"网络出错",Toast.LENGTH_SHORT).show();
    }else if(action.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)){
      Toast.makeText(context,"地图配置失败，请联系管理员",Toast.LENGTH_SHORT).show();
    }
  }
}
