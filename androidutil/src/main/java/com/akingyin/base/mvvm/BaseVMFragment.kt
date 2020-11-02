package com.akingyin.base.mvvm

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.akingyin.base.SimpleFragment
import com.akingyin.base.mvvm.viewmodel.BaseViewModel

/**
 * @ Description:
 * @author king
 * @ Date 2019/8/1 12:18
 * @version V1.0
 */
abstract class BaseVMFragment<VM : BaseViewModel> :SimpleFragment() {

    protected lateinit var mViewModel: VM

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initVM()

        startObserve()
        super.onViewCreated(view, savedInstanceState)
    }

    private fun initVM() {
        providerVMClass()?.let {
            mViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application).create(it)

            lifecycle.addObserver(mViewModel)
        }
    }
    open fun startObserve() {

        mViewModel.mException.observe(viewLifecycleOwner, Observer { it?.let { onError(it) } })
    }
    open fun providerVMClass(): Class<VM>? = null

    open fun onError(e: Throwable) {}
    override fun onDestroy() {
        lifecycle.removeObserver(mViewModel)
        super.onDestroy()
    }

}