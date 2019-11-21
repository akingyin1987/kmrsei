/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.widget;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.akingyin.img.R;
import java.util.List;

/**
 * Created by Administrator on 2016/6/22.
 */
public class MaterialSelectDialog<T> extends Dialog   {


    private ListView  content_list;

    private TextView  title;

    private ArrayAdapter<T>   adapter;


    public MaterialSelectDialog(Context context) {
        super(context, R.style.MyDialogStyle);
        init();
    }


    public void init() {
        setContentView(R.layout.layout_material_dialog);
        setCanceledOnTouchOutside(true);
        title = (TextView)findViewById(R.id.title);
        content_list = (ListView)findViewById(R.id.content_list);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1);
        content_list.setAdapter(adapter);
    }


    public   void   items(List<T>  data){
        adapter.clear();
        adapter.addAll(data);
        adapter.notifyDataSetChanged();
    }





    private   String   message;

    public   void  setTitle(String message){
        this.message = message;
        if(TextUtils.isEmpty(message)){
            title.setVisibility(View.GONE);
        }else{
            title.setVisibility(View.VISIBLE);
            title.setText(message);
        }
    }

    private AdapterView.OnItemClickListener    itemClickListener;

    public   void  showSelectItems(List<T> data, AdapterView.OnItemClickListener  itemClickListener){
        this.itemClickListener = itemClickListener;

        items(data);
        if(null != itemClickListener){
            content_list.setOnItemClickListener(itemClickListener);
        }
        show();
    }


}
