/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import com.akingyin.media.ImageLoadUtil;
import com.akingyin.media.ImagesDetailActivity;
import com.akingyin.media.R;
import com.akingyin.media.widget.SmoothImageView;

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2018/1/26 16:07
 */

public class ImagesDetailFragemntDialog  extends DialogFragment {

  @Override public void show(FragmentManager manager, String tag) {
    super.show(manager, tag);

  }
  public static ImagesDetailFragemntDialog newInstance(String  url) {
    Bundle arguments = new Bundle();
    arguments.putString(ImagesDetailActivity.INTENT_IMAGE_URL_TAG,url);


    ImagesDetailFragemntDialog fragment = new ImagesDetailFragemntDialog();

    fragment.setArguments(arguments);
    return fragment;
  }

  private   String   url;
  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);

    url = getArguments().getString(ImagesDetailActivity.INTENT_IMAGE_URL_TAG);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View   rootView = inflater.inflate(R.layout.activity_images_detail,container,false);
    SmoothImageView smoothImageView = (SmoothImageView) rootView.findViewById(R.id.images_detail_smooth_image);
    ImageLoadUtil.loadImage(url,getContext(),smoothImageView);
    return rootView;
  }
}
