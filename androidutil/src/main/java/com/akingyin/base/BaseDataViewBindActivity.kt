package com.akingyin.base


import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding

/**
 * data Bind
 * @ Description:
 * @author king
 * @ Date 2020/5/15 15:37
 * @version V1.0
 */
abstract class BaseDataViewBindActivity<DB : ViewDataBinding> : BaseDaggerActivity(){

    lateinit var mDataBind: DB

    override fun useDataBindView()= true

    override fun initDataBindView() {
        super.initDataBindView()
         mDataBind = DataBindingUtil.setContentView(this,getLayoutId())
         mDataBind.lifecycleOwner = this
    }


    override fun onDestroy() {
        super.onDestroy()
        mDataBind.unbind()
    }
}