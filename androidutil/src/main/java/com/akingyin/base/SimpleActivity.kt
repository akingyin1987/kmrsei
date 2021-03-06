package com.akingyin.base

import android.content.Context

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.akingyin.base.ext.no
import com.akingyin.base.ext.yes
import com.akingyin.base.utils.EasyActivityResult
import com.classic.common.MultipleStatusView
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog
import com.qmuiteam.qmui.widget.dialog.QMUITipDialog.Builder.ICON_TYPE_LOADING
import es.dmoral.toasty.Toasty
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

import kotlinx.coroutines.Job


/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:03
 * @version V1.0
 */
abstract class SimpleActivity : AppCompatActivity() ,IBaseView{
    private var compositeDisposable = CompositeDisposable()
    private   var mLaunchManager: MutableList<Job> = mutableListOf()
    private   var multipleStatusView : MultipleStatusView?=null
    // Log tag
    var  TAG :String  = this.javaClass.simpleName
    protected var mContext: Context? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        initInjection()

        super.onCreate(savedInstanceState)
        AppManager.getInstance()!!.addActivity(this)

        if(useDataBindView()){
            initDataBindView()
        }else {
            if(useViewBind()){
                initViewBind()
            }else{
                setContentView(getLayoutId())
            }
        }

        mContext = this
        initializationData(savedInstanceState)
        initView()
        startRequest()
        initStatusViewListion()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        onSaveInstanceData(outState)
    }

    protected fun setToolBar(toolbar: Toolbar, title: String) {
        toolbar.title = title
        setSupportActionBar(toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowHomeEnabled(true)
            toolbar.setNavigationOnClickListener {
                onBackPressed()
            }
        }


    }

    /**
     * dagger2注入
     */
    abstract   fun   initInjection()


    open    fun    useDataBindView()= false

    open    fun    useViewBind() = false


    open    fun    useAndroidNfc() = false

    /**
     * 初始化dataBinding
     */
    open   fun   initDataBindView(){

    }

    /**
     * 初始化Viewbind
     */
    open   fun   initViewBind(){

    }

    private   fun   initStatusViewListion(){
        multipleStatusView?.setOnClickListener(mRetryClickListener)
    }

    open   var  mRetryClickListener : View.OnClickListener = View.OnClickListener {
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
    abstract    fun    initializationData(savedInstanceState : Bundle?)


    /**
     * 保存当前状态
     */
    open   fun    onSaveInstanceData(outState: Bundle?){}


    /**
     * 初始化View
     */
    abstract    fun    initView()


    /**
     * 开始请求
     */
    open    fun     startRequest(){}


    /**
     * 打开软键盘
     */
    fun   openKeyBord(mEditText: EditText, mContext: Context){
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

    }

    /**
     * 关阀软键盘
     */
    fun   closeKeyBord(mEditText: EditText, mContext: Context){
        val   imm  = mContext.getSystemService(Context.INPUT_METHOD_SERVICE)  as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken,0)
    }

    override fun showMessage(msg: String?) {
        if (msg != null) {
            Toasty.info(this,msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showSucces(msg: String?) {
        if (msg != null) {
            Toasty.success(this,msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showError(msg: String?) {
        if (msg != null) {
            Toasty.error(this,msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun showWarning(msg: String?) {
        if (msg != null) {
            Toasty.warning(this,msg, Toast.LENGTH_SHORT).show()
        }
    }

    override fun close() {
    }

    override fun showTips(msg: String?) {
        if (msg != null) {
            Toasty.info(this,msg, Toast.LENGTH_SHORT).show()

           // QMUITipDialog.Builder(this).setTipWord(msg).setFollowSkin(true).create(true).show()

        }
    }


   private var loadingDialog: QMUITipDialog? = null

    override fun showLoadDialog(msg: String?, cancelBack: (() -> Unit)?) {
        loadingDialog?.let {
            it.isShowing.yes {
                it.dismiss()
            }.no {  }
        }

        loadingDialog = QMUITipDialog.Builder(this)
                .setIconType(ICON_TYPE_LOADING).create(true,R.style.MyDialogStyle)

        loadingDialog?.let {  dialog ->

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
        loadingDialog?.dismiss()
    }

    override fun showLoading() {
        showLoadDialog(null)
    }

    override fun dismissLoading() {
    }

    override fun onCancelLoading() {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }
    open fun addSubscription(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }
    fun addJob(job: Job){
        mLaunchManager.add(job)
    }
    override fun onDestroy() {
        AppManager.getInstance()?.finishActivity(this)
        EasyActivityResult.onRemoveActivityResult(TAG)
        hideLoadDialog()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
        mLaunchManager.forEach {
            if(!it.isCancelled){
                it.cancel()
            }
        }
        mLaunchManager.clear()
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onBackPressed() {

        super.onBackPressed()
    }


}