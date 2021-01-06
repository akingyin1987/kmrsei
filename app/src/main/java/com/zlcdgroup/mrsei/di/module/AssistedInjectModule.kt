/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.zlcdgroup.stms.di

import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.FragmentComponent

/**
 * @ Description:
 * @author king
 * @ Date 2020/10/14 16:30
 * @version V1.0
 */

// AssistedInject puts all assisted bindings in the same module.
// We need to make a decision about where to install it.
// In this case, as we only need it in fragments, we install it there.
// source: https://gist.github.com/manuelvicnt/437668cda3a891d347e134b1de29aee1
@InstallIn(ActivityComponent::class,FragmentComponent::class)
@AssistedModule
//@Module(includes = [AssistedInject_AssistedInjectModule::class])
@Module
// Needed until AssistedInject is incorporated into Dagger
interface AssistedInjectModule