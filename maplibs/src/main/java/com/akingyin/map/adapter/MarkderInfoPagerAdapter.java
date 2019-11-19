/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.map.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.akingyin.map.R;
import com.akingyin.map.base.ILoadImage;
import com.akingyin.map.base.IOperationListen;
import com.akingyin.map.model.IMarkerModel;
import java.text.MessageFormat;
import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2017/11/25 11:54
 */

public class MarkderInfoPagerAdapter  extends PagerAdapter {

  private ILoadImage mILoadImage;

  private IOperationListen mIOperationListen;

  /** 是否是路径浏览显示 */
  private   boolean    isPathMarker;

  public boolean isPathMarker() {
    return isPathMarker;
  }

  public void setPathMarker(boolean pathMarker) {
    isPathMarker = pathMarker;
  }

  public IOperationListen getIOperationListen() {
    return mIOperationListen;
  }

  public void setIOperationListen(IOperationListen IOperationListen) {
    mIOperationListen = IOperationListen;

  }

  public ILoadImage getILoadImage() {
    return mILoadImage;
  }

  public void setILoadImage(ILoadImage ILoadImage) {
    mILoadImage = ILoadImage;
  }

  private List<IMarkerModel>   mIMarkerModels;
  private Context   mContext;


  public    IMarkerModel     getIMarkerModel(int   postion){
    if(postion<0 || postion> mIMarkerModels.size()){
      return  null;
    }
    return  mIMarkerModels.get(postion);
  }

  public MarkderInfoPagerAdapter(Context  context,List<IMarkerModel> IMarkerModels) {
    mIMarkerModels = IMarkerModels;
    this.mContext = context;
  }

  public   void   setNewDatas(List<IMarkerModel>   markerModels){
    this.mIMarkerModels = markerModels;
    notifyDataSetChanged();
  }

  @Override public int getCount() {
    return mIMarkerModels.size();
  }

  @Override public boolean isViewFromObject(View view, Object object) {
    return view == object;
  }

  @Override public void destroyItem(ViewGroup container, int position, Object object) {


    ((ViewPager) container).removeView((View) object);
  }

  @Override public Object instantiateItem(ViewGroup container, final int position) {
   View  view = LayoutInflater.from(mContext).inflate(R.layout.openmap_detai, null);
   final IMarkerModel  iMarkerModel = mIMarkerModels.get(position);
    final ImageView  imageView = (ImageView)view.findViewById(R.id.detai_img);
    if(null != mILoadImage && !TextUtils.isEmpty(iMarkerModel.getMarkerDetaiImgPath())){
      mILoadImage.loadImageView(iMarkerModel.getMarkerDetaiImgPath(),mContext,imageView);
    }else{
      imageView.setVisibility(View.GONE);
    }
    imageView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onObjectImg(position,iMarkerModel,imageView);
        }
      }
    });
    TextView  btn_poidetail_showmap = (TextView)view.findViewById(R.id.btn_poidetail_showmap);
    if(TextUtils.isEmpty(iMarkerModel.getSortInfo())){
      btn_poidetail_showmap.setText(
          MessageFormat.format("详情   {0}/{1}", position + 1, mIMarkerModels.size()));

    }else{
      btn_poidetail_showmap.setText(iMarkerModel.getSortInfo());
    }

    TextView  detai_title = (TextView)view.findViewById(R.id.detai_title);
    detai_title.setVisibility(View.GONE);
    TextView  detai_info = (TextView)view.findViewById(R.id.detai_info);
    detai_info.setText(Html.fromHtml(iMarkerModel.getBaseInfo()));

    TextView  left = (TextView) view.findViewById(R.id.openmap_detai_leftbtn);
    TextView  right = (TextView)view.findViewById(R.id.openmap_detai_rightbtn);
    TextView  center = (TextView)view.findViewById(R.id.openmap_detai_middlebtn);
    final TextView  other = (TextView)view.findViewById(R.id.openmap_detai_other);
    final CheckBox  checkBox = (CheckBox)view.findViewById(R.id.cb_show);
    if(null != mIOperationListen){
      mIOperationListen.initView(left,center,right,position,iMarkerModel,other,checkBox);
    }
    other.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onOtherOperation(position,iMarkerModel,other);
        }
      }
    });
    checkBox.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onOtherOperation(position,iMarkerModel,checkBox);
        }
      }
    });
      left.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onOperation(position,iMarkerModel);
        }
      }
    });

    center.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onPathPlan(position,iMarkerModel);
        }
      }
    });

    right.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onTuWen(position,iMarkerModel);
        }
      }
    });

    ((ViewPager) container).addView(view);
   return  view;
  }
}
