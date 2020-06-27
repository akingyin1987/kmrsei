/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.multimedia;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.akingyin.media.adapter.BasePageAdapter;
import com.akingyin.media.model.IDataMultimedia;
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

 * @author king
 * @ Date 2016/12/27 17:38
 * @ Version V1.0
 */

public class MultimediaPageAdapter<T extends IDataMultimedia>  extends BasePageAdapter<T> {
  LayoutInflater   mInflater = null;
  private int mLocationX;
  private int mLocationY;
  private int mWidth;
  private int mHeight;
  public SparseArray<ViewMultimediaInfoHolder>   mSparseArray = new SparseArray<>();

  public int getLocationX() {
    return mLocationX;
  }

  public void setLocationX(int locationX) {
    mLocationX = locationX;
  }

  public int getLocationY() {
    return mLocationY;
  }

  public void setLocationY(int locationY) {
    mLocationY = locationY;
  }

  public int getWidth() {
    return mWidth;
  }

  public void setWidth(int width) {
    mWidth = width;
  }

  public int getHeight() {
    return mHeight;
  }

  public void setHeight(int height) {
    mHeight = height;
  }

  private    boolean   transformOut;

  private    int     postionTrans;

  public int getPostionTrans() {
    return postionTrans;
  }

  public void setPostionTrans(int postionTrans) {
    this.postionTrans = postionTrans;
  }

  public boolean isTransformOut() {
    return transformOut;
  }

  public void setTransformOut(boolean transformOut) {
    this.transformOut = transformOut;
  }

  public MultimediaPageAdapter(List<T> datas, Activity context) {
    super(datas, context);
    mInflater = LayoutInflater.from(context);
  }

  @Override public View getView(int position, T t, View convertView, ViewGroup container) {
    ViewMultimediaInfoHolder  viewMultimediaInfoHolder = new ViewMultimediaInfoHolder(mInflater,container,
        (Activity) context);
    viewMultimediaInfoHolder.setHeight(mHeight);
    viewMultimediaInfoHolder.setWidth(mWidth);
    viewMultimediaInfoHolder.setLocationY(mLocationY);
    viewMultimediaInfoHolder.setLocationX(mLocationX);
    if(postionTrans == position){
      viewMultimediaInfoHolder.setTransformIn(true);
    }
    viewMultimediaInfoHolder.bind(t,position,getCount());
    if(transformOut){
      viewMultimediaInfoHolder.transformOut();
    }
     mSparseArray.put(position,viewMultimediaInfoHolder);
    return viewMultimediaInfoHolder.getRoot();
  }

  public   ViewMultimediaInfoHolder   getView(int postion){
   return mSparseArray.get(postion);
  }

}
