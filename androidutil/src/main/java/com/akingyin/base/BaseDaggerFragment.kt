/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.akingyin.base

import android.content.Context
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2020/7/21 15:31
 * @version V1.0
 */
abstract class BaseDaggerFragment : SimpleFragment(), HasAndroidInjector,IBaseView {


    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>

    override fun onAttach(context: Context) {
        injection()

        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return   childFragmentInjector
    }

    override fun injection() {
        AndroidSupportInjection.inject(this)
    }
}