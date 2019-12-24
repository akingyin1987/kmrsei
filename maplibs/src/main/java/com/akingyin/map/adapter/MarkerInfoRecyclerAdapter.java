package com.akingyin.map.adapter;

import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.akingyin.map.R;
import com.akingyin.map.base.ILoadImage;
import com.akingyin.map.base.IOperationListen;
import com.akingyin.map.model.IMarkerModel;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import java.text.MessageFormat;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/12/17 11:58
 */
public class MarkerInfoRecyclerAdapter<T extends IMarkerModel> extends BaseQuickAdapter<T , BaseViewHolder> {

  private ILoadImage mILoadImage;

  private IOperationListen mIOperationListen;

  /** 是否是路径浏览显示 */
  private   boolean    isPathMarker;

  public ILoadImage getILoadImage() {
    return mILoadImage;
  }

  public void setILoadImage(ILoadImage ILoadImage) {
    mILoadImage = ILoadImage;
  }

  public IOperationListen getIOperationListen() {
    return mIOperationListen;
  }

  public void setIOperationListen(IOperationListen IOperationListen) {
    mIOperationListen = IOperationListen;
  }

  public boolean isPathMarker() {
    return isPathMarker;
  }

  public void setPathMarker(boolean pathMarker) {
    isPathMarker = pathMarker;
  }

  public MarkerInfoRecyclerAdapter() {
    super(R.layout.openmap_detai);
  }

  @Override protected void convert(@NonNull BaseViewHolder helper, T iMarkerModel) {
    ImageView  detai_img =  helper.getView(R.id.detai_img);
    if(null != mILoadImage && !TextUtils.isEmpty(iMarkerModel.getMarkerDetaiImgPath())){
      detai_img.setVisibility(View.VISIBLE);
      mILoadImage.loadImageView(iMarkerModel.getMarkerDetaiImgPath(),mContext,detai_img);
    }else{
      detai_img.setVisibility(View.GONE);
    }

    detai_img.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onObjectImg(helper.getAdapterPosition(),iMarkerModel,detai_img);
        }
      }
    });

    TextView btn_poidetail_showmap = helper.getView(R.id.btn_poidetail_showmap);
    if(TextUtils.isEmpty(iMarkerModel.getSortInfo())){
      btn_poidetail_showmap.setText(
          MessageFormat.format("详情   {0}/{1}", helper.getAdapterPosition() + 1, getData().size()));

    }else{
      btn_poidetail_showmap.setText(iMarkerModel.getSortInfo());
    }


    TextView  detai_title = helper.getView(R.id.detai_title);
    detai_title.setVisibility(View.GONE);
    TextView  detai_info = helper.getView(R.id.detai_info);
    detai_info.setText(Html.fromHtml(iMarkerModel.getBaseInfo()));
    TextView  left = helper.getView(R.id.openmap_detai_leftbtn);
    TextView  right = helper.getView(R.id.openmap_detai_rightbtn);
    TextView  center = helper.getView(R.id.openmap_detai_middlebtn);
    final TextView  other = helper.getView(R.id.openmap_detai_other);
    final CheckBox checkBox = helper.getView(R.id.cb_show);
    if(null != mIOperationListen){
      mIOperationListen.initView(left,center,right,helper.getAdapterPosition(),iMarkerModel,other,checkBox);
    }

    other.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onOtherOperation(helper.getAdapterPosition(),iMarkerModel,other);
        }
      }
    });
    checkBox.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onOtherOperation(helper.getAdapterPosition(),iMarkerModel,checkBox);
        }
      }
    });
    left.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onOperation(helper.getAdapterPosition(),iMarkerModel);
        }
      }
    });

    center.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onPathPlan(helper.getAdapterPosition(),iMarkerModel);
        }
      }
    });

    right.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mIOperationListen){
          mIOperationListen.onTuWen(helper.getAdapterPosition(),iMarkerModel);
        }
      }
    });

  }
}
