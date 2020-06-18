package com.akingyin.base.mvvm

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import autodispose2.androidx.lifecycle.AndroidLifecycleScopeProvider
import com.akingyin.base.BaseDataBindActivity
import com.akingyin.base.mvvm.viewmodel.BaseViewModel
import com.akingyin.base.net.exception.ApiException
import com.akingyin.base.repo.StateActionEvent


/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 11:23
 * @version V1.0
 */
abstract class BaseVMActivity<DB : ViewDataBinding,VM:BaseViewModel>  :BaseDataBindActivity<DB>(),LifecycleObserver{

    lateinit var mViewModel: VM


    override fun initDataBindView() {
        super.initDataBindView()
        initVM()
        startObserve()
    }

    private  fun  initVM(){
        mViewModel = getViewModelFactory().create(providerVMClass())
    }


    abstract  fun   getViewModelFactory(): ViewModelProvider.Factory

    abstract fun providerVMClass(): Class<VM>
    open fun startObserve() {
        mViewModel.mException.observe(this, Observer { it?.let { onError(it) } })
        mDataBind.lifecycleOwner = this
        mViewModel.mStateLiveData.observe(this, Observer {
                when(it){
                    is StateActionEvent.LoadState -> showLoadDialog(null)
                    is StateActionEvent.SuccessState-> hideLoadDialog()
                    is StateActionEvent.ErrorState -> {
                        hideLoadDialog()
                        showError(it.message)
                    }
                    is StateActionEvent.SuccessInfoState -> showSucces(it.message)
                }
        })
    }

    open fun onError(e: ApiException) {
        showError(e.msg)
    }


    override fun onDestroy() {

        lifecycle.removeObserver(mViewModel)
        super.onDestroy()
    }

    protected val scopeProvider: AndroidLifecycleScopeProvider by lazy {
        AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY)
    }
}