/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 * akingyin@163.com
 */

package com.zlcdgroup.mrsei.presenter.modules

import android.app.Activity
import com.zlcdgroup.mrsei.di.scope.PerActivity

import com.zlcdgroup.mrsei.ui.DropDownMenuActivity
import dagger.Binds
import dagger.Module

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/9 17:52
 * @version V1.0
 */

@Module
abstract class DropDownModule {
    @PerActivity
    @Binds
    abstract  fun   activity(activty: DropDownMenuActivity): Activity
}