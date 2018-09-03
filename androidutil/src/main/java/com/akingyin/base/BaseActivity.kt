package com.akingyin.base

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.classic.common.MultipleStatusView
import dagger.android.AndroidInjection
import javax.inject.Inject


/**
 * 基础类
 * @ Description:
 * @author king
 * @ Date 2018/8/3 16:16
 * @version V1.0
 */
abstract  class BaseActivity<T : BasePresenter<IBaseView>> : AppCompatActivity(),IBaseView {


    protected   var multipleStatusView : MultipleStatusView?=null
    // Log tag
    protected var TAG_LOG: String? = null

    @Inject
    lateinit var mPresenter: T
    protected var mContext: Context? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppManager.getInstance()!!.addActivity(this)
        TAG_LOG = this.localClassName
        setContentView(getLayoutId())
        mContext = this
        initInjection()
        initializationData(savedInstanceState)
        initView()
        startRequest()
        initStatusViewListion()

    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        onSaveInstanceData(outState)
    }


    /**
     * dagger2注入
     */
    private   fun   initInjection(){
        AndroidInjection.inject(this)
    }

    private   fun   initStatusViewListion(){
        multipleStatusView?.setOnClickListener(mRetryClickListener)
    }

    open   var  mRetryClickListener :View.OnClickListener = View.OnClickListener {
       startRequest()
    }


    /**
     * 加载布局
     */
    @LayoutRes
    abstract    fun    getLayoutId():Int

    /**
     * 初始化数据
     */
    abstract    fun    initializationData(savedInstanceState :Bundle?)


    /**
     * 保存当前状态
     */
    abstract   fun    onSaveInstanceData(outState: Bundle?)


    /**
     * 初始化View
     */
    abstract    fun    initView()


    /**
     * 开始请求
     */
    abstract    fun     startRequest()


    /**
     * 打开软键盘
     */
    fun   openKeyBord(mEditText: EditText, mContext: Context){
       var imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText,InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    /**
     * 关阀软键盘
     */
    fun   closeKeyBord(mEditText: EditText,mContext: Context){
       val   imm  = mContext.getSystemService(Context.INPUT_METHOD_SERVICE)  as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken,0)
    }



}