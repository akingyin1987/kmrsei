package com.akingyin.base

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.DialogFragment
import com.akingyin.base.ext.no
import com.akingyin.base.ext.yes
import com.classic.common.MultipleStatusView
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import es.dmoral.toasty.Toasty

/**
 * @ Description:
 * @author king
 * @ Date 2020/11/26 12:04
 * @version V1.0
 */
abstract class SimpleDialogFragment:DialogFragment(),IBaseView {

    private var isInited = false
    var  TAG :String  = this.javaClass.simpleName

    override fun onAttach(context: Context) {
        injection()
        super.onAttach(context)

    }

    open val cameraPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    open val videoPermissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    open  fun   onUseActivityBackCallBack() = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if(onUseActivityBackCallBack()){
            requireActivity().onBackPressedDispatcher.addCallback(this,object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() {
                    onBackPressed()
                }
            })
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        //        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                onBackPressed()
//            }
//        })
        return if(useDataBindView()){
            initDataBindView(inflater,container) ?:throw Exception("root view  must not be null")
        }else{
            if(useViewBind()){
                initViewBind(inflater,container) ?:throw Exception("root view  must not be null")
            }else{
                inflater.inflate(getLayoutId(), null)
            }
        }
    }

    abstract   fun  injection()
    abstract fun getLayoutId(): Int

    abstract fun initEventAndData()
    // an abstract function which will be called on the Back button press
    open fun onBackPressed(){

    }
    open    fun    useDataBindView()= false

    open    fun    useViewBind() = false


    open    fun    useAndroidNfc() = false

    /**
     * 初始化dataBinding
     */
    open   fun   initDataBindView(inflater: LayoutInflater, container: ViewGroup?): View?{

        // mBinding =  DataBindingUtil.inflate(inflater, R.layout.test_fragment_idcard,container,false);
        return null
    }

    /**
     * 初始化Viewbind
     */
    open   fun   initViewBind(inflater: LayoutInflater, container: ViewGroup?): View?{
        return null
    }
    /**
     * 视图是否加载完毕
     */


    private var isViewPrepare = false

    /**
     * 数据是否加载过了
     */
    private var hasLoadData = false

    /**
     * 多种状态的 View 的切换
     */
    private var mLayoutStatusView: MultipleStatusView? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isInited = true
        initEventAndData()
        isViewPrepare = true
        initView()

        //多种状态切换的view 重试点击事件
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }


    private fun lazyLoadDataIfPrepared() {
        if (!hasLoadData) {
            lazyLoad()
            hasLoadData = true
        }
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        lazyLoad()
    }


    /**
     * 初始化 ViewI
     */
    abstract fun initView()

    /**
     * 懒加载
     */
    abstract fun lazyLoad()


    override fun showMessage(msg: String?) {
        if (msg != null) {
            Toasty.info(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showSucces(msg: String?) {
        if (msg != null) {
            Toasty.success(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showError(msg: String?) {
        if (msg != null) {
            Toasty.error(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showWarning(msg: String?) {
        if (msg != null) {
            Toasty.warning(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun close() {
    }

    override fun showTips(msg: String?) {
        if (msg != null) {
            Toasty.info(requireContext(), msg, Toast.LENGTH_SHORT).show()
            // QMUITipDialog.Builder(this).setTipWord(msg).setFollowSkin(true).create(true).show()

        }
    }

    private var loadingDialog: QMUITipDialog? = null

    override fun showLoadDialog(msg: String?, cancelBack: (() -> Unit)?) {
        loadingDialog?.let {
            it.isShowing.yes {
                it.dismiss()
            }.no { }
        }

        loadingDialog = QMUITipDialog.Builder(requireContext())
                .setIconType(QMUITipDialog.Builder.ICON_TYPE_LOADING).create(true)

        loadingDialog?.let { dialog ->

            dialog.setOnCancelListener {
                cancelBack?.invoke()
                onCancelLoading()
            }
            dialog.setOnDismissListener {
                dismissLoading()
            }
            dialog.show()
        }

    }

    override fun hideLoadDialog() {
        loadingDialog?.let {
            it.isShowing.yes {
                it.dismiss()
            }.no { }
        }
    }

    override fun showLoading() {
        showLoadDialog(null)
    }


    override fun dismissLoading() {
    }

    override fun onCancelLoading() {

    }

    override fun onStart() {
        super.onStart()


    }

    override fun onResume() {
        super.onResume()
        lazyLoadDataIfPrepared()
    }

    override fun onPause() {
        super.onPause()
    }

}