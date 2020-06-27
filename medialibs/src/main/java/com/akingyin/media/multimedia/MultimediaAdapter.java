/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.multimedia;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.akingyin.media.model.IDataMultimedia;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.module.DraggableModule;
import java.util.List;
import org.jetbrains.annotations.NotNull;

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

public class MultimediaAdapter<T extends IDataMultimedia>  extends
    BaseQuickAdapter<T,MultimediaViewHolder> implements DraggableModule {

  private   LayoutInflater  mLayoutInflater = null;
  public MultimediaAdapter(Context context,int layoutResId, List<T> data) {
    super(layoutResId, data);

    this.mLayoutInflater = LayoutInflater.from(context);

  }

  @Override protected void convert(final MultimediaViewHolder multimediaViewHolder, T t) {


    multimediaViewHolder.bind(t);
  }

  @NotNull @Override
  protected MultimediaViewHolder createBaseViewHolder(@NotNull ViewGroup parent, int layoutResId) {
    View  view = mLayoutInflater.inflate(layoutResId, parent, false);
    System.out.println("-----MultimediaViewHolder-----");
    return new MultimediaViewHolder(view);
  }
}
