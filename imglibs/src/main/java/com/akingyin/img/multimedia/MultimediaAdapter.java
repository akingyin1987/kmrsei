/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.multimedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.akingyin.img.model.IDataMultimedia;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
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
 * @ Date 2016/11/17 15:53
 * @ Version V1.0
 */

public class MultimediaAdapter<T extends IDataMultimedia>  extends BaseItemDraggableAdapter<T,MultimediaViewHolder> {


  public MultimediaAdapter(Context context,int layoutResId, List<T> data) {
    super(layoutResId, data);
    
    this.mContext = context;
    this.mLayoutInflater = LayoutInflater.from(context);

  }

  @Override protected void convert(final MultimediaViewHolder multimediaViewHolder, T t) {
    if(!isItemDraggable()){
      multimediaViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
          return   getOnItemLongClickListener().onItemLongClick(MultimediaAdapter.this, v, multimediaViewHolder.getLayoutPosition() - getHeaderLayoutCount());
        }
      });
    }
    multimediaViewHolder.bind(t);
  }

  @Override
  protected MultimediaViewHolder createBaseViewHolder(ViewGroup parent, int layoutResId) {
    View  view = mLayoutInflater.inflate(layoutResId, parent, false);
    System.out.println("-----MultimediaViewHolder-----");
    return new MultimediaViewHolder(view);
  }


}
