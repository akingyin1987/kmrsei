package com.zlcdgroup.mrsei

import com.akingyin.base.BaseApp
import com.zlcdgroup.mrsei.di.component.DaggerAppComponent


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/3 17:27
 * @version V1.0
 */
class MrmseiApp :BaseApp() {

    override fun initInjection() {
      DaggerAppComponent.builder().application(this)
    }
}