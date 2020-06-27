/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.multimedia;

import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.media.R;
import com.akingyin.media.model.IDataMultimedia;
import com.akingyin.media.widget.HackyViewPager;
import java.util.LinkedList;
import java.util.List;

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
 * @ Author king
 * @ Date 2016/12/27 17:49
 * @ Version V1.0
 */

public  abstract class AbsMultimediaPageActivity<T extends IDataMultimedia> extends
    AppCompatActivity {


  public static final String INTENT_IMAGE_X_TAG = "INTENT_IMAGE_X_TAG";
  public static final String INTENT_IMAGE_Y_TAG = "INTENT_IMAGE_Y_TAG";
  public static final String INTENT_IMAGE_W_TAG = "INTENT_IMAGE_W_TAG";
  public static final String INTENT_IMAGE_H_TAG = "INTENT_IMAGE_H_TAG";
  public static final String INTENT_IMAGE_POSTION="INTENT_IMAGE_POSTION";

  private int mLocationX;
  private int mLocationY;
  private int mWidth;
  private int mHeight;
  private   int    currentPostion;

  private Rect mStartBounds = new Rect();
  private Rect mFinalBounds = new Rect();

  HackyViewPager mViewpager;

  MultimediaPageAdapter   mPageAdapter;



  List<T>   datas = new LinkedList<>();

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_image_text);
   mViewpager = findViewById(R.id.viewpager);
    if (null != getSupportActionBar()) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
    }
    getBundleExtras(getIntent().getExtras());
    datas = getAbsDatas();


    mPageAdapter = new MultimediaPageAdapter<>(datas,this);
    mPageAdapter.setHeight(mHeight);
    mPageAdapter.setWidth(mWidth);
    mPageAdapter.setLocationX(mLocationX);
    mPageAdapter.setLocationY(mLocationY);
    mViewpager.setAdapter(mPageAdapter);
    mPageAdapter.setPostionTrans(currentPostion);

    mViewpager.setCurrentItem(currentPostion);

  }

  protected void getBundleExtras(Bundle extras) {

    mLocationX = extras.getInt(INTENT_IMAGE_X_TAG);
    mLocationY = extras.getInt(INTENT_IMAGE_Y_TAG);
    mWidth = extras.getInt(INTENT_IMAGE_W_TAG);
    mHeight = extras.getInt(INTENT_IMAGE_H_TAG);
    currentPostion = extras.getInt(INTENT_IMAGE_POSTION,0);

  }

  @Override public void onBackPressed() {
    overridePendingTransition(0, 0);

    int  postion = mViewpager.getCurrentItem();
    if(currentPostion == postion){

      mPageAdapter.getView(postion).transformOut();
    }
    super.onBackPressed();
  }

  @Override protected void onPause() {
    super.onPause();
    if (isFinishing()) {
      overridePendingTransition(0, 0);
    }
  }

  public abstract List<T>  getAbsDatas();

  public abstract  void    saveData(T  t);

  public abstract  void    delectData(T  t);



}
