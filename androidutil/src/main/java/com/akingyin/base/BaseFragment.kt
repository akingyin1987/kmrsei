package com.akingyin.base

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.akingyin.base.ext.no
import com.akingyin.base.ext.yes
import com.classic.common.MultipleStatusView
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import es.dmoral.toasty.Toasty
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

    override fun onAttach(context: Context) {
         injection()

        super.onAttach(context)
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return   childFragmentInjector
    }
    abstract   fun  injection()
//    fun supportFragmentInjector(): DispatchingAndroidInjector<androidx.fragment.app.Fragment> {
//        return childFragmentInjector
//    }


}