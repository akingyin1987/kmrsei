package com.zlcdgroup.mrsei.ui.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.akingyin.base.BaseFragment
import com.akingyin.base.dialog.DialogUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.stepstone.stepper.BlockingStep
import com.stepstone.stepper.StepperLayout
import com.stepstone.stepper.VerificationError
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.presenter.UserListFragmentContract
import com.zlcdgroup.mrsei.presenter.impl.UserListFragmentPresenterImpl
import com.zlcdgroup.mrsei.ui.adapter.UserListAdapter
import com.zlcdgroup.mrsei.ui.adapter.UserViewHolder
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_userlist.*
import java.util.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:01
 * @version V1.0
 */


class  UserListFragment @Inject constructor() :BaseFragment() ,UserListFragmentContract.View , BlockingStep {
    companion object {

        private const val CLICKS_KEY = "clicks"

        private const val TAP_THRESHOLD = 2

        private const val LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId"


    }

    @Inject
    lateinit var userListFragmentPresenterImpl: UserListFragmentPresenterImpl

    @Inject
    lateinit var userListAdapter: UserListAdapter

    lateinit   var  onNavigationBarListener:OnNavigationBarListener




    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is OnNavigationBarListener){
            onNavigationBarListener = context
        }

    }

    override fun showUserList(userEntitys: List<UserEntity>?) {
       userListAdapter.setNewData(userEntitys)
    }

    override fun showAddUserDialog() {

        MaterialDialog.Builder(mContext!!).title("用户信息修改")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive { dialog, _ ->
                    var  name:EditText = dialog.findViewById(R.id.edit_name) as EditText
                    var  age :EditText = dialog.findViewById(R.id.edit_age) as EditText
                    var userEntity :UserEntity = UserEntity()
                    userEntity.age = age.text.toString().trim().toInt()
                    userEntity.name = name.text.toString().trim()
                    userListFragmentPresenterImpl.addUser(userEntity)
                }
                .customView(R.layout.dialog_edit_user,false)
                .show()
    }

    override fun showDelectUserDialog(userEntity: UserEntity, postion: Int) {
       DialogUtil.showConfigDialog(mContext,"确定要删除当前信息?") {
           result -> if(result){
           userListFragmentPresenterImpl.delectUser(userEntity,postion)
       }
       }
    }

    override fun getAdapter(): BaseQuickAdapter<UserEntity, UserViewHolder> {
       return  userListAdapter
    }

    override fun showModifyUser(userEntity: UserEntity, postion: Int) {
        var   view : View = LayoutInflater.from(mContext).inflate(R.layout.dialog_edit_user,null)
        var  name:EditText = view.findViewById(R.id.edit_name) as EditText
        var  age :EditText = view.findViewById(R.id.edit_age) as EditText
         name.setText(userEntity.name)
         age.setText(userEntity.age.toString())

         MaterialDialog.Builder(mContext!!).title("用户信息修改")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive { _, _ ->

                    userEntity.age = age.text.toString().trim().toInt()
                    userEntity.name = name.text.toString().trim()
                    userListFragmentPresenterImpl.modifyUser(userEntity,postion)
                }
                .customView(view,false)
                .show()

    }

    override fun initView() {

        userListFragmentPresenterImpl.attachView(this)

        recycle.layoutManager= androidx.recyclerview.widget.LinearLayoutManager(mContext)
        recycle.itemAnimator = androidx.recyclerview.widget.DefaultItemAnimator()
        recycle.adapter = userListAdapter
        userListAdapter.setOnItemClickListener{
            _, _, position ->
            showModifyUser(userListAdapter.getItem(position)!!,position)
        }
        userListAdapter.setOnItemLongClickListener {
            _, _, position ->
             showDelectUserDialog(userListAdapter.getItem(position)!!,position)
             true

        }
        fab.setOnClickListener {
             showAddUserDialog()

        }
    }

    override fun lazyLoad() {

    }

    override fun getLayoutId(): Int = R.layout.fragment_userlist
    var  string:String ? = null
    override fun initEventAndData() {
      string =arguments?.getString("postion")
    }
    override fun onSelected() {
        showMessage(if (string.isNullOrEmpty())"select" else "select $string")
    }

    override fun verifyStep(): VerificationError? {
        var i :Int = Random().nextInt(100)
        return if(i % 2 == 0) null else VerificationError("error $string")
    }

    override fun onError(error: VerificationError) {
        showError(error.errorMessage)
    }

    override fun onDestroy() {
        userListFragmentPresenterImpl.detachView()
        super.onDestroy()
    }

    private var i = 0
    private val isAboveThreshold: Boolean
        get() = i >= TAP_THRESHOLD

    private fun updateNavigationBar() {
        onNavigationBarListener.onChangeEndButtonsEnabled(isAboveThreshold)
    }

    override fun injection() {
        AndroidSupportInjection.inject(this)
    }

    override fun onBackClicked(callback: StepperLayout.OnBackClickedCallback?) {

    }

    override fun onCompleteClicked(callback: StepperLayout.OnCompleteClickedCallback?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onNextClicked(callback: StepperLayout.OnNextClickedCallback?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}