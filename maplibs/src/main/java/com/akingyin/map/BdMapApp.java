/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map;

import android.content.Context;
import android.content.IntentFilter;
import android.widget.Toast;
import com.baidu.lbsapi.BMapManager;
import com.baidu.lbsapi.MKGeneralListener;
import com.baidu.mapapi.SDKInitializer;
import timber.log.Timber;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/30 11:28
 */

public class BdMapApp {


  private   BdMapApp(){}


  private SDKReceiver mReceiver;

  private IntentFilter iFilter;

  public IntentFilter getiFilter() {
    return iFilter;
  }



  public SDKReceiver getReceiver() {
    return mReceiver;
  }



  private volatile   static BdMapApp cassApp;
  public   static BdMapApp get(){
     if(null == cassApp){
       cassApp = new BdMapApp();
     }
    return  cassApp;
  }



  public   void   initBaiDuMap(Context  context){

    SDKInitializer.initialize(context.getApplicationContext());

    iFilter = new IntentFilter();
    iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
    iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
    iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
    iFilter.addAction(SDKInitializer.SDK_BROADTCAST_INTENT_EXTRA_INFO_KEY_ERROR_CODE);
    mReceiver = new SDKReceiver();

    initEngineManager(context.getApplicationContext());

  }

  private BMapManager mBMapManager = null;
  private void initEngineManager(Context context) {
    if (mBMapManager == null) {
      mBMapManager = new BMapManager(context);
    }

    if (!mBMapManager.init(new MyGeneralListener(context))) {
      Toast.makeText(context, "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
    }
    Timber.d("initEngineManager");
  }


  public  class MyGeneralListener implements MKGeneralListener {

    private   Context  mContext;

    public MyGeneralListener(Context context) {
      mContext = context;
    }

    @Override
    public void onGetPermissionState(int iError) {
      // 非零值表示key验证未通过
      if (iError != 0) {
        // 授权Key错误：
        Toast.makeText(mContext,
            "地图key验证未通过,检查您的网络连接是否正常(可联系相关技术人员)！error: " + iError, Toast.LENGTH_LONG).show();
      } else {
        //Toast.makeText(mContext, "key认证成功", Toast.LENGTH_LONG)
        //    .show();
      }
    }
  }



}
