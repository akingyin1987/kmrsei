package com.akingyin.base.mvvm


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.akingyin.base.BaseDataViewBindFragment
import com.akingyin.base.mvvm.viewmodel.BaseViewModel
import com.akingyin.base.repo.StateActionEvent

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 12:18
 * @version V1.0
 */
abstract class BaseVMFragment<DB : ViewDataBinding,VM : BaseViewModel> :BaseDataViewBindFragment<DB>() {

    protected lateinit var mViewModel: VM



    abstract  fun  initVM():VM

    override fun initDataBindView(inflater: LayoutInflater, container: ViewGroup?): View? {
        val view = super.initDataBindView(inflater, container)
        mViewModel = initVM()
        startObserve()
        return  view
    }

    open fun startObserve() {

        mViewModel.mException.observe(viewLifecycleOwner, { it?.let { onError(it) } })
        mViewModel.mStateLiveData.observe(this, {
            when (it) {
                is StateActionEvent.LoadingState -> showLoadDialog(null)
                is StateActionEvent.SuccessState -> hideLoadDialog()
                is StateActionEvent.ErrorState -> {
                    hideLoadDialog()
                    showError(it.message)
                }
                is StateActionEvent.SuccessInfoState -> showSucces(it.message)
            }
        })
    }
    open fun providerVMClass(): Class<VM>? = null

    open fun onError(e: Throwable) {}
    override fun onDestroy() {
        lifecycle.removeObserver(mViewModel)
        super.onDestroy()
    }

}