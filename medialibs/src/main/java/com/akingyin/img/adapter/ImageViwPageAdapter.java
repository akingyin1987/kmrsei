/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.img.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import cn.jzvd.JzvdStd;
import com.akingyin.audio.AudioPlayDialog;
import com.akingyin.audio.AudioPlayView;
import com.akingyin.audio.AudioUtil;
import com.akingyin.base.utils.FileUtils;
import com.akingyin.img.ImageLoadUtil;
import com.akingyin.img.R;
import com.akingyin.img.model.ImageTextModel;
import com.github.chrisbanes.photoview.PhotoView;
import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author king
 * @date 2016/10/10
 */

public class ImageViwPageAdapter  extends BasePageAdapter<ImageTextModel> {
    private final LayoutInflater inflater;
    private FragmentManager  fragmentManager;

    public FragmentManager getFragmentManager() {
        return fragmentManager;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public ImageViwPageAdapter(Context context) {
        super(context);
        inflater = LayoutInflater.from(context);
    }

    public ImageViwPageAdapter(List<ImageTextModel> datas, Context context) {
        super(datas, context);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, ImageTextModel imageTextModel, View convertView, ViewGroup container) {
     View   rootview = inflater.inflate(R.layout.item_image_video, null);
     ViewHolder    holder = new ViewHolder(rootview,context);
        System.out.println("----------------getView---------------");
        holder.bind(imageTextModel,position+1);
        return rootview;
    }

    private  class ViewHolder {

        final PhotoView pv_img;
        final TextView tv_text;
        final TextView tv_pagenum;
        final  TextView card_pic_num;
        final   ImageView  iv_marker_video;
        final JzvdStd videoplayer;
        final   ImageView  iv_audio_markder;
        final   TextView  tv_audio_time;
        final AudioPlayView   mAudioPlayView;

        private  Context context;

        public ViewHolder(View view,Context context) {
            this.context = context;
            mAudioPlayView = view.findViewById(R.id.audio_player);
            pv_img = (PhotoView) view.findViewById(R.id.pv_img);
           tv_text = (TextView)view.findViewById(R.id.tv_text);
            tv_pagenum =(TextView)view.findViewById(R.id.tv_pagenum);
            card_pic_num = (TextView)view.findViewById(R.id.card_pic_num);
            iv_marker_video = (ImageView)view.findViewById(R.id.iv_marker_video);
            videoplayer = view.findViewById(R.id.videoplayer);
            iv_audio_markder = view.findViewById(R.id.iv_audio_markder);
            tv_audio_time = view.findViewById(R.id.tv_audio_time);
        }
        public  void   bind(final ImageTextModel   imageTextModel,int postion){
            if(TextUtils.isEmpty(imageTextModel.title)){
                card_pic_num.setText(MessageFormat.format("{0}", getCount()));
            }else{
                card_pic_num.setText(Html.fromHtml("<font color = 'red'>"+imageTextModel.title+"</font>"+getCount()));
            }
            videoplayer.setVisibility(View.GONE);
            iv_marker_video.setVisibility(View.GONE);
            mAudioPlayView.setVisibility(View.GONE);
            tv_pagenum.setText(MessageFormat.format("{0}/{1}", postion, getCount()));
            if(!TextUtils.isEmpty(imageTextModel.text)){
                pv_img.setVisibility(View.GONE);
                tv_text.setVisibility(View.VISIBLE);
                tv_text.setText(imageTextModel.text);
            }else{
                tv_text.setVisibility(View.GONE);
                pv_img.setVisibility(View.VISIBLE);

                if(null == pv_img.getScaleType()){
                    pv_img.setScaleType(ImageView.ScaleType.CENTER);
                }
                if(imageTextModel.haveNetServer){
                    try {
                        if(FileUtils.isFileExist(imageTextModel.localPath)){
                            if(imageTextModel.multimediaType == 2){
                                pv_img.setVisibility(View.GONE);
                                iv_marker_video.setVisibility(View.VISIBLE);
                                videoplayer.setVisibility(View.VISIBLE);
                                ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath,context,videoplayer.thumbImageView);
                                videoplayer.setUp(imageTextModel.localPath,"视频");
                            }else if(imageTextModel.multimediaType == 1){
                                ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath,context,pv_img);
                            }else if(imageTextModel.multimediaType == 3){
                                iv_marker_video.setVisibility(View.GONE);
                                videoplayer.setVisibility(View.GONE);
                                pv_img.setVisibility(View.GONE);
                                mAudioPlayView.setVisibility(View.VISIBLE);
                                mAudioPlayView.setUrl(imageTextModel.localPath);
                               // iv_audio_markder.setVisibility(View.VISIBLE);
                               // setAudioDuration(tv_audio_time,imageTextModel.localPath);
                            }


                        }else{
                            if(imageTextModel.multimediaType == 1){
                                ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath,context,pv_img);
                            }else if(imageTextModel.multimediaType == 2){
                                pv_img.setVisibility(View.GONE);
                                iv_marker_video.setVisibility(View.VISIBLE);
                                videoplayer.setVisibility(View.VISIBLE);
                                ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath,context,videoplayer.thumbImageView);
                                videoplayer.setUp(imageTextModel.serverPath,"视频");

                            }else if(imageTextModel.multimediaType == 3){
                                iv_marker_video.setVisibility(View.GONE);
                                videoplayer.setVisibility(View.GONE);
                                pv_img.setVisibility(View.GONE);
                                mAudioPlayView.setVisibility(View.VISIBLE);
                                mAudioPlayView.setUrl(imageTextModel.serverPath);
                               // iv_audio_markder.setVisibility(View.VISIBLE);
                               // setAudioDuration(tv_audio_time,imageTextModel.localPath);
                            }

                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        if(imageTextModel.multimediaType == 1){
                            ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath,context,pv_img);
                        }else if(imageTextModel.multimediaType == 2){
                            pv_img.setVisibility(View.GONE);
                            iv_marker_video.setVisibility(View.VISIBLE);
                            videoplayer.setVisibility(View.VISIBLE);
                            ImageLoadUtil.loadImageServerFile(imageTextModel.serverPath,context,videoplayer.thumbImageView);
                            videoplayer.setUp(imageTextModel.serverPath,"视频");

                        }else if(imageTextModel.multimediaType == 3){
                            iv_marker_video.setVisibility(View.GONE);
                            videoplayer.setVisibility(View.GONE);
                            pv_img.setVisibility(View.GONE);
                            iv_audio_markder.setVisibility(View.VISIBLE);
                            setAudioDuration(tv_audio_time,imageTextModel.localPath);
                        }

                    }
                }else{
                    if(imageTextModel.multimediaType == 1){
                        ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath,context,pv_img);
                    }else if(imageTextModel.multimediaType == 2){
                        iv_marker_video.setVisibility(View.VISIBLE);
                        videoplayer.setVisibility(View.VISIBLE);
                        pv_img.setVisibility(View.GONE);
                        ImageLoadUtil.loadImageLocalFile(imageTextModel.localPath,context,videoplayer.thumbImageView);
                        videoplayer.setUp(imageTextModel.localPath,"视频");
                    }else if(imageTextModel.multimediaType == 3){
                         iv_marker_video.setVisibility(View.GONE);
                         videoplayer.setVisibility(View.GONE);
                         pv_img.setVisibility(View.GONE);

                        iv_audio_markder.setVisibility(View.VISIBLE);
                        setAudioDuration(tv_audio_time,imageTextModel.localPath);
                    }


                }
            }
            iv_audio_markder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(FileUtils.isFileExist(imageTextModel.localPath)){
                        try {
                            AudioPlayDialog.Companion.getInstance(imageTextModel.localPath).show(fragmentManager,"audio");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        try {
                            AudioPlayDialog.Companion.getInstance(imageTextModel.serverPath).show(fragmentManager,"audio");
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                }
            });
            iv_marker_video.setVisibility(View.GONE);
            iv_marker_video.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    try {
                        if(FileUtils.isFileExist(imageTextModel.localPath)){
                            playVideo(context,imageTextModel.localPath);
                        }else {
                            if(imageTextModel.haveNetServer){
                                playVideo(context,imageTextModel.serverPath);
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    public  void    setAudioDuration(TextView  textView ,String  localPath){
        if(TextUtils.isEmpty(localPath)){
            textView.setVisibility(View.GONE);
        }else{
            int  itemDuration = AudioUtil.INSTANCE.getMediaDuration(localPath);

            long minutes = TimeUnit.MILLISECONDS.toMinutes(itemDuration);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(itemDuration)
                    - TimeUnit.MINUTES.toSeconds(minutes);
            textView.setText(String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds));
        }
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
                uri = FileProvider.getUriForFile(context, "com.zlcdgroup.caims.provider", file);
            }else{
                uri = Uri.fromFile(file);
            }
            intent.setDataAndType(uri, "video/*");
            context.startActivity(intent);
        }

    }
}
