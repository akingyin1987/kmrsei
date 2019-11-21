/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.multimedia;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import cn.jzvd.JzvdStd;
import com.akingyin.img.ImageLoadUtil;
import com.akingyin.img.R;
import com.akingyin.img.model.IDataMultimedia;
import com.akingyin.img.model.MultimediaEnum;
import com.akingyin.img.model.ViewDataStatusEnum;
import com.akingyin.img.widget.AudioPlayerView;
import com.akingyin.img.widget.SmoothImageView;
import com.akingyin.img.widget.SuperCheckBox;
import com.github.chrisbanes.photoview.PhotoView;
import java.io.File;
import java.text.MessageFormat;

/**
 * @author king
 * @version V1.0
 * @ Description:
 *

 * @ Date 2017/12/5 11:57
 */
public class ViewMultimediaInfoHolder<T extends IDataMultimedia>{

  private     boolean   transformIn;
  private TextView multimediaText;
  private PhotoView imagesDetailSmoothImage;
  private ImageView ivMarkerVideo;
  private View mask;
  private SuperCheckBox cbCheck;
  private AudioPlayerView audioPlayer;
  private TextView tvPageinfo;
  private JzvdStd mVideoPlayerStandard;
  private  View  root;

  private int mLocationX;
  private int mLocationY;
  private int mWidth;
  private int mHeight;

  public int getLocationX() {
    return mLocationX;
  }

  public void setLocationX(int locationX) {
    mLocationX = locationX;
  }

  public int getLocationY() {
    return mLocationY;
  }

  public void setLocationY(int locationY) {
    mLocationY = locationY;
  }

  public int getWidth() {
    return mWidth;
  }

  public void setWidth(int width) {
    mWidth = width;
  }

  public int getHeight() {
    return mHeight;
  }

  public void setHeight(int height) {
    mHeight = height;
  }

  public View getRoot() {
    return root;
  }

  public void setRoot(View root) {
    this.root = root;
  }

  private Activity mContext;

  public boolean isTransformIn() {
    return transformIn;
  }

  public void setTransformIn(boolean transformIn) {
    this.transformIn = transformIn;
  }

  public ViewMultimediaInfoHolder(LayoutInflater inflater, ViewGroup parent,Activity  context) {
    this(inflater.inflate(R.layout.view_multimedia_info, parent, false));
    this.mContext = context;
  }

  public void setImagesDetailSmoothImage(SmoothImageView imagesDetailSmoothImage) {
    this.imagesDetailSmoothImage = imagesDetailSmoothImage;
  }

  public   void   transformOut(){
    if(imagesDetailSmoothImage.getVisibility() == View.VISIBLE){

    }
  }


  public ViewMultimediaInfoHolder(View view) {
    this.root = view;

    multimediaText = (TextView) view.findViewById(R.id.multimedia_text);
    imagesDetailSmoothImage = (PhotoView) view.findViewById(R.id.images_detail_smooth_image);
    ivMarkerVideo = (ImageView) view.findViewById(R.id.iv_marker_video);
    mask = view.findViewById(R.id.mask);
    cbCheck = (SuperCheckBox) view.findViewById(R.id.cb_check);
    audioPlayer = (AudioPlayerView) view.findViewById(R.id.audio_player);
    tvPageinfo = (TextView) view.findViewById(R.id.tv_pageinfo);
    mVideoPlayerStandard = (JzvdStd) view.findViewById(R.id.videoplayer);
    cbCheck.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(null != data && null != data.getViewStatus()
            && data.getViewStatus() != ViewDataStatusEnum.NULL){
          data.setSelected(!data.isSelected());
          cbCheck.setChecked(data.isSelected());
          mask.setVisibility(data.isSelected()?View.VISIBLE:View.GONE);
        }
      }
    });
    imagesDetailSmoothImage.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if(data.getMultimediaEnum() == MultimediaEnum.Video){
          try {
            File  file = new File(data.getLocalPath());
            if(file.exists() && file.length()>10*1000){
              playVideo(mContext,file.getAbsolutePath());
            }else{
              if(data.isNetToWeb()){
                playVideo(mContext,data.getBaseUrl()+data.getServerPath());
              }
            }
          }catch (Exception e){
            e.printStackTrace();
            if(data.isNetToWeb()){
              playVideo(mContext,data.getBaseUrl()+data.getServerPath());
            }
          }
        }
      }
    });

    ivMarkerVideo.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        playVideo(mContext,data.getBaseUrl()+data.getServerPath());
      }
    });
  }


  public   void   playVideo(Context context,String   path){
    if(path.startsWith("http")){
      String extension = MimeTypeMap.getFileExtensionFromUrl(path);
      String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
      Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
      mediaIntent.setDataAndType(Uri.parse(path), mimeType);
      context.startActivity(mediaIntent);
    }else{
      Intent intent = new Intent(Intent.ACTION_VIEW);
      File file = new File(path);
      Uri uri = null;
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
        uri = FileProvider.getUriForFile(context, mContext.getPackageName(), file);
      }else{
        uri = Uri.fromFile(file);
      }
      intent.setDataAndType(uri, "video/*");
      context.startActivity(intent);
    }

  }

  public TextView getMultimediaText() {
    return multimediaText;
  }

  public TextView getTvPageinfo() {
    return tvPageinfo;
  }

  public AudioPlayerView getAudioPlayer() {
    return audioPlayer;
  }

  public PhotoView getImagesDetailSmoothImage() {
    return imagesDetailSmoothImage;
  }

  public ImageView getIvMarkerVideo() {
    return ivMarkerVideo;
  }

  public View getMask() {
    return mask;
  }

  public SuperCheckBox getCbCheck() {
    return cbCheck;
  }


  private    T    data;


  public   void    bind(T  t,int postion,int  count){
    this.data = t;
     if(null == t.getViewStatus() || t.getViewStatus() == ViewDataStatusEnum.NULL){
       mask.setVisibility(View.GONE);
       cbCheck.setVisibility(View.GONE);
     }else if(t.getViewStatus() == ViewDataStatusEnum.Clipboard){

     }else if(t.getViewStatus() == ViewDataStatusEnum.COPY){
         mask.setVisibility(t.isSelected()?View.VISIBLE:View.GONE);
         cbCheck.setChecked(t.isSelected());
     }else if(t.getViewStatus() == ViewDataStatusEnum.DELECT){
       mask.setVisibility(t.isSelected()?View.VISIBLE:View.GONE);
       cbCheck.setChecked(t.isSelected());
     }else if(t.getViewStatus() == ViewDataStatusEnum.SELECT){
       mask.setVisibility(t.isSelected()?View.VISIBLE:View.GONE);
       cbCheck.setChecked(t.isSelected());
     }else if(t.getViewStatus() == ViewDataStatusEnum.MODIFY){

     }

    tvPageinfo.setText(MessageFormat.format("{0}/{1}", postion + 1, count));
     switch (t.getMultimediaEnum()){
       case TEXT:
         mVideoPlayerStandard.setVisibility(View.GONE);
         multimediaText.setText(t.getTextDes());
         multimediaText.setVisibility(View.VISIBLE);
         imagesDetailSmoothImage.setVisibility(View.GONE);
         ivMarkerVideo.setVisibility(View.GONE);
         audioPlayer.setVisibility(View.GONE);
         break;
       case IMAGE:

         multimediaText.setVisibility(View.GONE);
         imagesDetailSmoothImage.setVisibility(View.VISIBLE);
         ivMarkerVideo.setVisibility(View.GONE);
         mVideoPlayerStandard.setVisibility(View.GONE);
         audioPlayer.setVisibility(View.GONE);
         try {
           if(!TextUtils.isEmpty(t.getLocalPath())){
             ImageLoadUtil.loadImageLocalFile(t.getLocalPath(),mContext,imagesDetailSmoothImage);
           }else{
             if(t.isNetToWeb()){
               ImageLoadUtil.loadImageServerFileNoPlaceHolder(t.getServerPath(),mContext,imagesDetailSmoothImage);
             }
           }
         }catch(Exception e){
           e.printStackTrace();
           if(t.isNetToWeb()){
             ImageLoadUtil.loadImageServerFileNoPlaceHolder(t.getServerPath(),mContext,imagesDetailSmoothImage);
           }
         }

         break;
       case Video:
         multimediaText.setVisibility(View.GONE);
         mVideoPlayerStandard.setVisibility(View.VISIBLE);

         imagesDetailSmoothImage.setVisibility(View.GONE);
         ivMarkerVideo.setVisibility(View.VISIBLE);
         audioPlayer.setVisibility(View.GONE);
         try {
           if(!TextUtils.isEmpty(t.getLocalPath())){
             mVideoPlayerStandard.setUp(t.getLocalPath(),"视频");
             ImageLoadUtil.loadImageLocalFile(t.getLocalPath(),mContext,mVideoPlayerStandard.thumbImageView);
           }else{
             if(t.isNetToWeb()){
               mVideoPlayerStandard.setUp(t.getBaseUrl()+t.getServerPath(),"视频");
               ImageLoadUtil.loadImageServerFileNoPlaceHolder(t.getServerPath(),mContext,mVideoPlayerStandard.thumbImageView);
             }
           }

         }catch(Exception e){
           e.printStackTrace();
           if(t.isNetToWeb()){
             ImageLoadUtil.loadImageServerFileNoPlaceHolder(t.getServerPath(),mContext,mVideoPlayerStandard.thumbImageView);
           }
         }

         break;
       case Audio:
         mVideoPlayerStandard.setVisibility(View.GONE);
         multimediaText.setVisibility(View.GONE);
         imagesDetailSmoothImage.setVisibility(View.GONE);
         ivMarkerVideo.setVisibility(View.GONE);
         audioPlayer.setVisibility(View.VISIBLE);
         audioPlayer.withUrl(t.getLocalPath());
         break;
       case ImageText:
         break;
         default:
     }
  }
}
