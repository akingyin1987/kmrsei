/*
 * Copyright (c) 2017. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.akingyin.media.multimedia;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import cn.jzvd.Jzvd;
import com.akingyin.media.ImageLoadUtil;
import com.akingyin.media.R;
import com.akingyin.media.model.IDataMultimedia;
import com.akingyin.media.model.MultimediaEnum;
import com.akingyin.media.model.OperationStateEnum;
import com.akingyin.media.widget.HackyViewPager;
import com.github.chrisbanes.photoview.OnViewTapListener;
import java.util.ArrayList;
import java.util.List;

/**
 * @author king
 * @version V1.0
 * @ Description:
 *
 * @ Date 2017/12/5 11:57
 */
public class PreviewLayout<T extends IDataMultimedia> extends FrameLayout implements ViewPager.OnPageChangeListener {

    public static final long ANIM_DURATION = 400;

    private View mBackgroundView;
    private HackyViewPager mViewPager;
    private ImageView mScalableImageView;

    private List<T> mThumbViewInfoList = new ArrayList<>();
    private int mIndex;
    private Rect mStartBounds = new Rect();
    private Rect mFinalBounds = new Rect();
    private boolean isAnimFinished = true;
    private  boolean   clickOut = true;

    public boolean isClickOut() {
        return clickOut;
    }

    public void setClickOut(boolean clickOut) {
        this.clickOut = clickOut;
    }

    public PreviewLayout(Context context) {
        this(context, null);
    }

    public PreviewLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PreviewLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.layout_preview, this, true);
        mBackgroundView = findViewById(R.id.background_view);
        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        mScalableImageView = (ImageView) findViewById(R.id.scalable_image_view);

        mScalableImageView.setPivotX(0f);
        mScalableImageView.setPivotY(0f);
        mScalableImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

    public void setData(List<T> list, int index) {
        if (list == null || list.isEmpty() || index < 0) {
            return;
        }

        this.mThumbViewInfoList = list;
        this.mIndex = index;
        mStartBounds = mThumbViewInfoList.get(mIndex).getBounds();
        if(null == mStartBounds){
            mStartBounds = new Rect(12,337,227,525);
        }

        System.out.println("mstartBounds="+mStartBounds.toString());
        post(new Runnable() {
            @Override
            public void run() {
                mViewPager.setAdapter(new ImagePagerAdapter());
                mViewPager.setCurrentItem(mIndex);
                mViewPager.addOnPageChangeListener(PreviewLayout.this);
                if(null != mStartBounds){
                    mScalableImageView.setX(mStartBounds.left);
                    mScalableImageView.setY(mStartBounds.top);
                }

                loadCurrotView(mIndex);
            }
        });
    }

    public   void   loadCurrotView(int  postion){
        try {
            MultimediaEnum multimediaEnum = mThumbViewInfoList.get(postion).getMultimediaEnum();
            T   t = mThumbViewInfoList.get(mIndex);
            switch (multimediaEnum){
                case IMAGE:
                    if(!TextUtils.isEmpty(t.getLocalPath())){
                        ImageLoadUtil.loadImageLocalFile(t.getLocalPath(),getContext(),mScalableImageView);
                    }else{
                        if(t.isNetToWeb()){
                            ImageLoadUtil.loadImageServerFileNoPlaceHolder(t.getServerPath(),getContext(),mScalableImageView);
                        }
                    }
                    break;
                case Video:
                    if(!TextUtils.isEmpty(t.getLocalPath())){
                        ImageLoadUtil.loadImageLocalFile(t.getLocalPath(),getContext(),mScalableImageView);
                    }else{
                        if(t.isNetToWeb()){
                            ImageLoadUtil.loadImageServerFileNoPlaceHolder(t.getServerPath(),getContext(),mScalableImageView);
                        }
                    }
                    break;
                case TEXT:

                    break;
                case  Audio:

                    break;
                    default:
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void startScaleUpAnimation() {
        if(null == mStartBounds){
            return;
        }
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Point globalOffset = new Point();
                getGlobalVisibleRect(mFinalBounds, globalOffset);
                mFinalBounds.offset(-globalOffset.x, -globalOffset.y);

                LayoutParams lp = new LayoutParams(
                        mStartBounds.width(),
                        mStartBounds.width() * mFinalBounds.height() / mFinalBounds.width()
                );
                mScalableImageView.setLayoutParams(lp);

                startAnim();

                if (Build.VERSION.SDK_INT >= 16) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPageSelected(int position) {
        mIndex = position;
        mStartBounds = mThumbViewInfoList.get(mIndex).getBounds();
        if (mStartBounds == null) {
            return;
        }

        computeStartScale();
    }

    private void startAnim() {
        if (!isAnimFinished) {
            return;
        }

        float startScale = computeStartScale();

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mBackgroundView, View.ALPHA, 0f, 1f),
                ObjectAnimator.ofFloat(mScalableImageView, View.X, mStartBounds.left, mFinalBounds.left),
                ObjectAnimator.ofFloat(mScalableImageView, View.Y, mStartBounds.top, mFinalBounds.top),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_X, 1f / startScale),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_Y, 1f / startScale));
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimFinished = false;
                mViewPager.setAlpha(0f);
                mScalableImageView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimFinished = true;
                mViewPager.setAlpha(1f);
                mScalableImageView.setVisibility(View.INVISIBLE);
            }
        });
        animatorSet.start();
    }

    private float computeStartScale() {
        float startScale;
        if ((float) mFinalBounds.width() / mFinalBounds.height()
                > (float) mStartBounds.width() / mStartBounds.height()) {
            // Extend start bounds horizontally （以竖直方向为参考缩放）
            startScale = (float) mStartBounds.height() / mFinalBounds.height();
            float startWidth = startScale * mFinalBounds.width();
            float deltaWidth = (startWidth - mStartBounds.width()) / 2;
            mStartBounds.left -= deltaWidth;
            mStartBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically （以水平方向为参考缩放）
            startScale = (float) mStartBounds.width() / mFinalBounds.width();
            float startHeight = startScale * mFinalBounds.height();
            float deltaHeight = (startHeight - mStartBounds.height()) / 2;
            mStartBounds.top -= deltaHeight;
            mStartBounds.bottom += deltaHeight;
        }

        return startScale;
    }

    public void startScaleDownAnimation() {
        if (!isAnimFinished) {
            return;
        }

        AnimatorSet animatorSet = new AnimatorSet();
        System.out.println("null =="+(null == mScalableImageView));
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mScalableImageView, View.X, mStartBounds.left),
                ObjectAnimator.ofFloat(mScalableImageView, View.Y, mStartBounds.top),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_X, 1f),
                ObjectAnimator.ofFloat(mScalableImageView, View.SCALE_Y, 1f));
        animatorSet.setDuration(ANIM_DURATION);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAnimFinished = false;
                loadCurrotView(mIndex);
                mScalableImageView.setVisibility(View.VISIBLE);
                mViewPager.setAlpha(0f);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAnimFinished = true;

                FrameLayout contentContainer = (FrameLayout) ((Activity) getContext()).findViewById(android.R.id.content);
                contentContainer.removeView(PreviewLayout.this);
                 Jzvd.releaseAllVideos();
                if(getContext() instanceof  BaseMultimediaActivity && (BaseMultimediaActivity.sOperationStateEnum == OperationStateEnum.Select||
                                            BaseMultimediaActivity.sOperationStateEnum == OperationStateEnum.Delect ||
                                            BaseMultimediaActivity.sOperationStateEnum == OperationStateEnum.Copy)){

                    ((BaseMultimediaActivity) getContext()).adapter.notifyDataSetChanged();
                }
            }
        });
        animatorSet.start();
    }

    private class ImagePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mThumbViewInfoList != null ? mThumbViewInfoList.size() : 0;
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
           ViewMultimediaInfoHolder<T>  viewMultimediaInfoHolder = new ViewMultimediaInfoHolder<>(LayoutInflater.from(getContext()),container,
               (Activity) getContext());

           // loadCurrotView(position);
            if(mThumbViewInfoList.get(position).getMultimediaEnum() != MultimediaEnum.Video){
                viewMultimediaInfoHolder.getImagesDetailSmoothImage().setOnViewTapListener(new OnViewTapListener() {
                    @Override public void onViewTap(View view, float x, float y) {
                        if(mThumbViewInfoList.get(position).getMultimediaEnum() != MultimediaEnum.Video){
                            startScaleDownAnimation();
                        }

                    }
                });
            }


            viewMultimediaInfoHolder.bind(mThumbViewInfoList.get(position),position,mThumbViewInfoList.size());
            container.addView(viewMultimediaInfoHolder.getRoot());

            return viewMultimediaInfoHolder.getRoot();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
