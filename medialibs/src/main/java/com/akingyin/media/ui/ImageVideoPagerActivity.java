/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.ui;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import cn.jzvd.JzvdStd;
import com.akingyin.media.R;
import com.akingyin.media.adapter.ImageViwPageAdapter;
import com.akingyin.media.model.ImageTextList;

/**
 * 图片列表详情
 *
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/21 11:12
 */

public class ImageVideoPagerActivity extends AppCompatActivity {

  public   static    final   String   DATA_KEY="data";
  public   static    final   String   POSTION_KEY="postion";

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.activity_viewpager);
    ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
    ImageTextList imageTextList = (ImageTextList) getIntent().getSerializableExtra(DATA_KEY);
    int  postion = getIntent().getIntExtra(POSTION_KEY,0);
    if(null == imageTextList ||  null == imageTextList.items || imageTextList.items.size()==0){
      finish();
      Toast.makeText(this,"数据出错了",Toast.LENGTH_SHORT).show();
      return;
    }

   ImageViwPageAdapter adapter = new ImageViwPageAdapter(imageTextList.items,this);
   viewPager.setAdapter(adapter);
   adapter.fragmentManager = getSupportFragmentManager();

    if(imageTextList.items.size()<=postion){
      postion = 0;
    }
   viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
     @Override
     public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

     }

     @Override public void onPageSelected(int position) {

     }

     @Override public void onPageScrollStateChanged(int state) {

     }
   });
   viewPager.setCurrentItem(postion);

  }

  @Override public void onBackPressed() {
    if (JzvdStd.backPress()) {
      return;
    }
    super.onBackPressed();
  }

  @Override protected void onPause() {
    super.onPause();
    JzvdStd.releaseAllVideos();
  }
}
