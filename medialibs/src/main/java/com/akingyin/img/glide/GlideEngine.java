///*
// * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
// * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
// * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
// * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
// * Vestibulum commodo. Ut rhoncus gravida arcu.
// */
//
//package com.akingyin.img.glide;
//
//import android.content.Context;
//import android.graphics.drawable.Drawable;
//import android.net.Uri;
//import android.widget.ImageView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.Priority;
//import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
//import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
//import com.bumptech.glide.request.RequestOptions;
//
//
///**
// * Created by Administrator on 2017/9/21.
// */
//
//public class GlideEngine implements ImageEngine {
//
//
//    @Override
//    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
//        Glide.with(context)
//                .asBitmap()
//                .load(uri)
//                .apply(new RequestOptions().placeholder(placeholder).override(resize,resize).centerCrop())
//                .into(imageView);
//    }
//
//    @Override
//    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
//        Glide.with(context)
//                .asBitmap()
//                .load(uri)
//                .apply(new RequestOptions().placeholder(placeholder).override(resize,resize).centerCrop())
//                .transition(new BitmapTransitionOptions().crossFade(300))
//                .into(imageView);
//    }
//
//    @Override
//    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
//        Glide.with(context)
//                .load(uri)
//                .apply(new RequestOptions().override(resizeX,resizeY).centerCrop().priority(Priority.HIGH))
//                .transition(new DrawableTransitionOptions().crossFade(300))
//                .into(imageView);
//    }
//
//    @Override
//    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
//        Glide.with(context)
//                .asGif()
//                .load(uri)
//                .apply(new RequestOptions().override(resizeX,resizeY).centerCrop().priority(Priority.HIGH))
//                .into(imageView);
//    }
//
//    @Override
//    public boolean supportAnimatedGif() {
//        return true;
//    }
//}
