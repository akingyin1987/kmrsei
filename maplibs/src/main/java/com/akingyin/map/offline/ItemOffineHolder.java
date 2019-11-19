package com.akingyin.map.offline;/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.akingyin.map.OnBdListion;
import com.akingyin.map.R;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import java.text.MessageFormat;
import java.util.Locale;

public class ItemOffineHolder extends RecyclerView.ViewHolder {
  private TextView title;
  private TextView update;
  private TextView ratio;
  private Button remove;
  private Button download;

  public ItemOffineHolder(LayoutInflater inflater, ViewGroup parent) {
    this(inflater.inflate(R.layout.item_offine, parent, false));
  }

  public ItemOffineHolder(View view) {
    super(view);
    title = (TextView) view.findViewById(R.id.title);
    update = (TextView) view.findViewById(R.id.update);
    ratio = (TextView) view.findViewById(R.id.ratio);
    remove = (Button) view.findViewById(R.id.remove);
    download = (Button)view.findViewById(R.id.download);
  }
  public String formatDataSize(int size) {
    String ret = "";
    if (size < (1024 * 1024)) {
      ret = String.format(Locale.getDefault(),"%dK", size / 1024);
    } else {
      ret = String.format(Locale.getDefault(),"%.1fM", size / (1024 * 1024.0));
    }
    return ret;
  }


  private OnBdListion mOnBdListion;

  public OnBdListion getOnBdListion() {
    return mOnBdListion;
  }

  public void setOnBdListion(OnBdListion bdListion) {
    mOnBdListion = bdListion;
  }

  public MKOLUpdateElement getMKOLUpdateElement() {
    return mMKOLUpdateElement;
  }

  private   MKOLUpdateElement   mMKOLUpdateElement;

  public   void   bind(final MKOLUpdateElement   mkolUpdateElement){
    this.mMKOLUpdateElement = mkolUpdateElement;
    title.setText(
        MessageFormat.format("{0}({1})", mkolUpdateElement.cityName, mkolUpdateElement.cityID));
    if(mkolUpdateElement.update ){
      update.setText("可更新");
    }else{
      update.setText("已最新");
    }
    if(mkolUpdateElement.ratio != 100){
      download.setVisibility(View.VISIBLE);
    }else{
      download.setVisibility(View.GONE);
    }
    ratio.setText(MessageFormat.format("{0}/{1}", formatDataSize(mkolUpdateElement.size),
        formatDataSize(mkolUpdateElement.serversize)));
    remove.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != mOnBdListion){
          mOnBdListion.call(mkolUpdateElement,0);
        }
      }
    });
    if(mkolUpdateElement.status == MKOLUpdateElement.DOWNLOADING){
      download.setTag(2);
      download.setText("暂停");
    }else{
      download.setTag(1);
      download.setText("下载");
    }

    download.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        try {
          int  tag = Integer.parseInt(v.getTag().toString());
          if(tag == 1){
            download.setTag(2);
            download.setText("暂停");
            if(null != mOnBdListion){
              mOnBdListion.call(mkolUpdateElement,1);
            }
          }else{
            download.setTag(1);
            download.setText("下载");
            if(null != mOnBdListion){
              mOnBdListion.call(mkolUpdateElement,2);
            }
          }
        }catch (Exception e){
          e.printStackTrace();
        }
      }
    });
  }

  public TextView getUpdate() {
    return update;
  }

  public TextView getRatio() {
    return ratio;
  }

  public TextView getTitle() {
    return title;
  }

  public Button getRemove() {
    return remove;
  }
}
