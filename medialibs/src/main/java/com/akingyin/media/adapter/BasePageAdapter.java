/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.viewpager.widget.PagerAdapter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Administrator
 * @date 2016/10/10
 */

public abstract class BasePageAdapter<T>  extends PagerAdapter {
    static final int IGNORE_ITEM_VIEW_TYPE = AdapterView.ITEM_VIEW_TYPE_IGNORE;
    private List<T>   items = new LinkedList<>();
    public Context   context;

    public List<T> getItems() {
        return items;
    }


   public BasePageAdapter(RecycleBin recycleBin,Context context) {

       this.context = context;

    }
    public BasePageAdapter(Context  context) {

        this(new RecycleBin(),context);
    }

    public BasePageAdapter(List<T> datas,Context context) {
        items.addAll(datas);
        this.context = context;


    }

    public   void    addDatas(List<T>   datas){
        items.addAll(datas);
    }
    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

      return view.equals(object);
    }

    @Override public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }

    @Override public  Object instantiateItem(ViewGroup container, int position) {

        View view = getView(position,items.get(position), null, container);
        container.addView(view);
        return view;
    }
    @Override public final void destroyItem(ViewGroup container, int position, Object object) {
        View view = (View) object;
        container.removeView(view);

    }
    public int getViewTypeCount() {
        return 1;
    }

    public int getItemViewType(int position) {
        return 0;
    }



    public abstract View getView(int position,T  t, View convertView, ViewGroup container);
}
