package com.akingyin.map.offline;
/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.akingyin.map.OnBdListion;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import java.util.LinkedList;
import java.util.List;

/**
 * @ Description:
 * @ Author king
 * @ Date 2017/9/8 11:08
 * @ Version V1.0
 */

public class OfflineAdapter  extends RecyclerView.Adapter<ItemOffineHolder> {

  Context mContext;
  LayoutInflater  mInflater;

  private SparseArray<ItemOffineHolder>   mOffineHolderSparseArray = new SparseArray<>();


  public ItemOffineHolder getOffineHolder(int  postion){
    return  mOffineHolderSparseArray.get(postion);
  }

  OnBdListion mOnBdListion;

  public OnBdListion getOnBdListion() {
    return mOnBdListion;
  }

  public void setOnBdListion(OnBdListion onBdListion) {
    mOnBdListion = onBdListion;
  }

  private List<MKOLUpdateElement>   mUpdateElements  =  new LinkedList<>();

  public List<MKOLUpdateElement> getUpdateElements() {
    return mUpdateElements;
  }

  public void setUpdateElements(List<MKOLUpdateElement> updateElements) {
    mUpdateElements = updateElements;
  }

  public OfflineAdapter(Context context) {
    mContext = context;
    mInflater = LayoutInflater.from(context);
  }

  @Override public ItemOffineHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new ItemOffineHolder(mInflater,parent);
  }

  @Override public void onBindViewHolder(ItemOffineHolder holder, int position) {

      holder.setOnBdListion(mOnBdListion);
      holder.bind(mUpdateElements.get(position));
      mOffineHolderSparseArray.put(position,holder);

  }

  @Override public int getItemCount() {
    return mUpdateElements.size();
  }
}
