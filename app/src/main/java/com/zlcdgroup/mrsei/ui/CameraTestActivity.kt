/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import com.akingyin.base.SimpleActivity
import com.akingyin.base.ext.gone
import com.akingyin.base.ext.visiable
import com.akingyin.camera.CameraParameBuild
import com.akingyin.camera.ui.BaseCameraFragment
import com.akingyin.camera.widget.CaptureButton
import com.akingyin.camera.widget.TypeButton
import com.qmuiteam.qmui.util.QMUIStatusBarHelper
import com.zlcdgroup.mrsei.R



/**
 * @ Description:
 * @author king
 * @ Date 2020/7/20 17:24
 * @version V1.0
 */
class CameraTestActivity : SimpleActivity() {

    override fun initInjection() {
        QMUIStatusBarHelper.translucent(this)
    }

    override fun getLayoutId()= R.layout.activity_camera

    override fun initializationData(savedInstanceState: Bundle?) {

    }

    override fun onSaveInstanceData(outState: Bundle?) {

    }

    override fun initView() {
        supportFragmentManager.beginTransaction().add(R.id.container,BaseCameraFragment.newInstance(CameraParameBuild()),"camera")



    }



    override fun startRequest() {

    }
}