/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.multimedia;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.akingyin.base.utils.FileUtils;
import com.akingyin.img.ImageLoadUtil;
import com.akingyin.img.R;
import com.akingyin.img.model.IDataMultimedia;
import com.akingyin.img.model.ViewDataStatusEnum;
import com.akingyin.img.widget.AudioPlayerView;
import com.akingyin.img.widget.SuperCheckBox;
import com.chad.library.adapter.base.BaseViewHolder;
import java.io.File;

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

 * @author  king
 * @ Date 2016/11/17 15:55
 * @ Version V1.0
 */

public class MultimediaViewHolder extends BaseViewHolder {
  ImageView   iv_image,iv_marker_video;
  AudioPlayerView audio_player;
  TextView  tv_text;
  View  mask;
  SuperCheckBox cb_check;



  public MultimediaViewHolder(View view) {
    super(view);
    iv_image = getView(R.id.iv_image);
    iv_marker_video = getView(R.id.iv_marker_video);
    audio_player = getView(R.id.audio_player);
    mask = getView(R.id.mask);
    tv_text = getView(R.id.tv_text);
    cb_check = getView(R.id.cb_check);
    cb_check.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
          if(null != iDataMultimedia){
            iDataMultimedia.setSelected(cb_check.isChecked());
            mask.setVisibility(cb_check.isChecked()?View.VISIBLE:View.GONE);
          }
      }
    });
  }

  private IDataMultimedia iDataMultimedia;
  public   void   bind(IDataMultimedia iDataMultimedia){
    this.iDataMultimedia = iDataMultimedia;
    if(iDataMultimedia.getViewStatus() == ViewDataStatusEnum.NULL){
      cb_check.setVisibility(View.GONE);
      mask.setVisibility(View.GONE);
    }else{
      cb_check.setVisibility(View.VISIBLE);
      cb_check.setChecked(iDataMultimedia.isSelected());
      if(iDataMultimedia.getViewStatus() != ViewDataStatusEnum.Clipboard){

        mask.setVisibility(iDataMultimedia.isSelected()?View.VISIBLE:View.GONE);
      }else{
        mask.setVisibility(View.GONE);
      }
    }
    iv_marker_video.setVisibility(View.GONE);
    iv_image.setVisibility(View.GONE);
    tv_text.setVisibility(View.GONE);
    audio_player.setVisibility(View.GONE);


    switch (iDataMultimedia.getMultimediaEnum()){
      case IMAGE:
        iv_image.setVisibility(View.VISIBLE);
     //   KLog.d(iDataMultimedia.getServerPath());
        try {
          if(iDataMultimedia.isNetToWeb()){
            if( FileUtils.isFileExist(iDataMultimedia.getLocalPath())){
              ImageLoadUtil.loadImageLocalFile(iDataMultimedia.getLocalPath(),getConvertView().getContext(),iv_image);
            }else{
              ImageLoadUtil.loadImageServerFile(iDataMultimedia.getServerPath(),getConvertView().getContext(),iv_image);
            }
          }else{
            ImageLoadUtil.loadImageLocalFile(iDataMultimedia.getLocalPath(),getConvertView().getContext(),iv_image);
          }

        }catch (Exception e){

          e.printStackTrace();
        }
        break;
      case TEXT:
         tv_text.setVisibility(View.VISIBLE);
         tv_text.setText(iDataMultimedia.getTextDes());

        break;
      case Video:
        iv_image.setVisibility(View.VISIBLE);
        iv_marker_video.setVisibility(View.VISIBLE);
        try {
          if(iDataMultimedia.isNetToWeb()){
            if( FileUtils.isFileExist(iDataMultimedia.getLocalPath())){
              ImageLoadUtil.loadImageLocalFile(iDataMultimedia.getLocalPath(),getConvertView().getContext(),iv_image);
            }else{
              ImageLoadUtil.loadImageServerFile(iDataMultimedia.getServerPath(),getConvertView().getContext(),iv_image);
            }
          }else{
            ImageLoadUtil.loadImageLocalFile(iDataMultimedia.getLocalPath(),getConvertView().getContext(),iv_image);
          }

        }catch (Exception e){
          e.printStackTrace();
        }
        break;
      case Audio:
         audio_player.setVisibility(View.VISIBLE);
        try {
          if(iDataMultimedia.isNetToWeb()){
            if( FileUtils.isFileExist(iDataMultimedia.getLocalPath())){
              audio_player.withUrl(iDataMultimedia.getLocalPath());
            }else{
              audio_player.withUrl(iDataMultimedia.getBaseUrl()+File.separator+iDataMultimedia.getServerPath());
            }
          }else{
            audio_player.withUrl(iDataMultimedia.getLocalPath());
          }

        }catch (Exception e){
          e.printStackTrace();
        }
        break;
      case ImageText:

        break;
        default:
    }
  }

}
