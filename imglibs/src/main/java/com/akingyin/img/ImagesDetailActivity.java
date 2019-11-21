/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.akingyin.img.widget.SmoothImageView;
import com.github.chrisbanes.photoview.OnPhotoTapListener;


/**
 * @ Description:
 * @ Author king
 * @ Date 2017/9/14 13:20
 * @ Version V1.0
 */

public class ImagesDetailActivity  extends AppCompatActivity {

  public static final String INTENT_IMAGE_URL_TAG = "INTENT_IMAGE_URL_TAG";
  public static final String INTENT_IMAGE_X_TAG = "INTENT_IMAGE_X_TAG";
  public static final String INTENT_IMAGE_Y_TAG = "INTENT_IMAGE_Y_TAG";
  public static final String INTENT_IMAGE_W_TAG = "INTENT_IMAGE_W_TAG";
  public static final String INTENT_IMAGE_H_TAG = "INTENT_IMAGE_H_TAG";

  private String mImageUrl;
  private int mLocationX;
  private int mLocationY;
  private int mWidth;
  private int mHeight;

  SmoothImageView mSmoothImageView;
  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_images_detail);
    Intent  intent = getIntent();
    if(null != intent.getExtras()){
      Bundle   extras = intent.getExtras();
      mImageUrl = extras.getString(INTENT_IMAGE_URL_TAG);
      mLocationX = extras.getInt(INTENT_IMAGE_X_TAG);
      mLocationY = extras.getInt(INTENT_IMAGE_Y_TAG);
      mWidth = extras.getInt(INTENT_IMAGE_W_TAG);
      mHeight = extras.getInt(INTENT_IMAGE_H_TAG);
    }
    mSmoothImageView = (SmoothImageView)findViewById(R.id.images_detail_smooth_image);
    mSmoothImageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
    mSmoothImageView.transformIn();

    if(mImageUrl.startsWith("http")){
      ImageLoadUtil.loadImageServerFile(mImageUrl,this,mSmoothImageView);
    }else{
      ImageLoadUtil.loadImageLocalFile(mImageUrl,this,mSmoothImageView);
    }

    mSmoothImageView.setOnTransformListener(new SmoothImageView.TransformListener() {
      @Override
      public void onTransformComplete(int mode) {
        if (mode == 2) {
          finish();
        }
      }
    });


    mSmoothImageView.setOnPhotoTapListener(new OnPhotoTapListener() {

      @Override public void onPhotoTap(ImageView view, float x, float y) {
        mSmoothImageView.transformOut();
      }


    });
    if(null != getSupportActionBar()){
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);

    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      onBackPressed();

    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    mSmoothImageView.transformOut();
  }

  @Override
  protected void onPause() {
    super.onPause();
    if (isFinishing()) {
      overridePendingTransition(0, 0);
    }
  }
}
