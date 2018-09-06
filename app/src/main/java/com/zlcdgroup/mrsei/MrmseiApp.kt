package com.zlcdgroup.mrsei

import com.akingyin.base.BaseApp
import com.zlcdgroup.mrsei.di.component.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 17:27
 * @version V1.0
 */
class MrmseiApp :BaseApp() {
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
       return  DaggerAppComponent.builder().application(this).build()
    }

    override fun initInjection() {

    }
}