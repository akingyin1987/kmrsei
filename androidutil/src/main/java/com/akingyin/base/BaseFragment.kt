package com.akingyin.base


import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject


/**
 * @ Description:
 * @author king
 * @ Date 2018/8/3 17:56
 * @version V1.0
 */
abstract class BaseFragment :SimpleFragment(),HasAndroidInjector,IBaseView{


    @Inject
    lateinit var childFragmentInjector: DispatchingAndroidInjector<Any>


    override fun androidInjector(): AndroidInjector<Any> {
        return   childFragmentInjector
    }




}