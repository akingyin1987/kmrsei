package com.akingyin.base.mvvm

import android.os.Bundle
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akingyin.base.SimpleActivity
import com.akingyin.base.mvvm.viewmodel.BaseViewModel
import com.akingyin.base.net.exception.ApiException
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 11:23
 * @version V1.0
 */
abstract class BaseVMActivity<VM:BaseViewModel>  :SimpleActivity(),LifecycleObserver{

    lateinit var mViewModel: VM


    override fun onCreate(savedInstanceState: Bundle?) {
        initVM()
        super.onCreate(savedInstanceState)
        startObserve()
    }


    private  fun  initVM(){
       providerVMClass()?.let {
           mViewModel =  ViewModelProvider.AndroidViewModelFactory.getInstance(application).create(it)
       }
    }

    open fun providerVMClass(): Class<VM>? = null


    open fun startObserve() {
        mViewModel.mException.observe(this, Observer { it?.let { onError(it) } })
    }

    open fun onError(e: ApiException) {}


    override fun onDestroy() {

        lifecycle.removeObserver(mViewModel)
        super.onDestroy()
    }

    protected val scopeProvider: AndroidLifecycleScopeProvider by lazy {
        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
    }
}