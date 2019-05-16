package com.akingyin.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 15:33
 * @version V1.0
 */
abstract class SimpleFragment : androidx.fragment.app.Fragment(){

    protected var mView: View? = null
    protected lateinit var mActivity: Activity
    protected lateinit var mContext: Context
    protected var isInited = false



    override fun onAttach(context: Context) {
        super.onAttach(context)

        mActivity = context as Activity
        mContext = context
    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
         mView = inflater.inflate(getLayoutId(), null)
        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isInited = true
        initEventAndData()
        super.onViewCreated(view, savedInstanceState)
    }

    abstract  fun  getLayoutId():Int

    abstract  fun  initEventAndData()
}