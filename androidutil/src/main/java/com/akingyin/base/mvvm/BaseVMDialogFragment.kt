package com.akingyin.base.mvvm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.akingyin.base.SimpleDialogFragment
import com.akingyin.base.mvvm.viewmodel.BaseViewModel
import com.akingyin.base.repo.StateActionEvent

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/26 12:07
 * @version V1.0
 */
abstract class BaseVMDialogFragment<DB : ViewDataBinding,VM : BaseViewModel>:SimpleDialogFragment() {

    protected lateinit var mViewModel: VM

    protected lateinit var mDataBind: DB

    abstract  fun  initVM():VM

    override fun initDataBindView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mDataBind = DataBindingUtil.inflate(inflater,getLayoutId(),container,false)

        mViewModel = initVM()
        startObserve()
        return  mDataBind.root
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


    open fun onError(e: Throwable) {}
    override fun onDestroy() {
        lifecycle.removeObserver(mViewModel)
        super.onDestroy()
    }

}