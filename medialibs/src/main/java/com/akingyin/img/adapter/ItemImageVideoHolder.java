/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.akingyin.img.R;
import com.github.chrisbanes.photoview.PhotoView;


public class ItemImageVideoHolder extends RecyclerView.ViewHolder {
  private PhotoView pvImg;
  private TextView cardPicNum;
  private TextView tvText;
  private TextView tvPagenum;

  public ItemImageVideoHolder(LayoutInflater inflater, ViewGroup parent) {
    this(inflater.inflate(R.layout.item_image_video, parent, false));
  }

  public ItemImageVideoHolder(View view) {
    super(view);
    pvImg = (PhotoView) view.findViewById(R.id.pv_img);
    cardPicNum = (TextView) view.findViewById(R.id.card_pic_num);
    tvText = (TextView) view.findViewById(R.id.tv_text);
    tvPagenum = (TextView) view.findViewById(R.id.tv_pagenum);
  }

  public TextView getCardPicNum() {
    return cardPicNum;
  }

  public PhotoView getPvImg() {
    return pvImg;
  }

  public TextView getTvText() {
    return tvText;
  }

  public TextView getTvPagenum() {
    return tvPagenum;
  }
}
