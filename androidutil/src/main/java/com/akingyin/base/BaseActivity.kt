@file:Suppress("DEPRECATION")

package com.akingyin.base

import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject




/**
 * 基础类
 * @ Description:
 * @author king
 * @ Date 2018/8/3 16:16
 * @version V1.0
 */
abstract  class BaseActivity : SimpleActivity(),HasAndroidInjector {

//    @Inject
//    lateinit var supportFragmentInjector: DispatchingAndroidInjector<androidx.fragment.app.Fragment>
//    @Inject
//    lateinit var frameworkFragmentInjector: DispatchingAndroidInjector<android.app.Fragment>

    @Inject
    lateinit var fragmentInjector :DispatchingAndroidInjector<Any>
    override fun initInjection() {

        AndroidInjection.inject(this)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return  fragmentInjector
    }
//     fun fragmentInjector(): DispatchingAndroidInjector<Fragment>? {
//        return   frameworkFragmentInjector
//    }
//
//     fun supportFragmentInjector(): DispatchingAndroidInjector<androidx.fragment.app.Fragment>? {
//        return  supportFragmentInjector
//    }


}