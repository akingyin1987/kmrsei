package com.zlcdgroup.mrsei.ui.fragment

import android.content.Context
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.akingyin.base.BaseFragment
import com.akingyin.base.dialog.DialogUtil
import com.chad.library.adapter.base.BaseQuickAdapter
import com.stepstone.stepper.Step
import com.stepstone.stepper.VerificationError
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.presenter.UserListFragmentContract
import com.zlcdgroup.mrsei.presenter.UserListFragmentPresenterImpl
import com.zlcdgroup.mrsei.ui.adapter.UserListAdapter
import com.zlcdgroup.mrsei.ui.adapter.UserViewHolder
import dagger.android.support.AndroidSupportInjection
import kotlinx.android.synthetic.main.fragment_userlist.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:01
 * @version V1.0
 */


class  UserListFragment @Inject constructor() :BaseFragment() ,UserListFragmentContract.View , Step {
    companion object {

        private const val CLICKS_KEY = "clicks"

        private const val TAP_THRESHOLD = 2

        private const val LAYOUT_RESOURCE_ID_ARG_KEY = "messageResourceId"


    }

    @Inject
    lateinit var userListFragmentPresenterImpl: UserListFragmentPresenterImpl


    lateinit var userListAdapter: UserListAdapter

    lateinit   var  onNavigationBarListener:OnNavigationBarListener




    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if(context is OnNavigationBarListener){
            onNavigationBarListener = context
        }
        userListAdapter = UserListAdapter(context!!)
    }

    override fun showUserList(userEntitys: List<UserEntity>?) {
       userListAdapter.setNewData(userEntitys)
    }

    override fun showAddUserDialog() {


        MaterialDialog.Builder(mContext!!).title("用户信息修改")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive { dialog, which ->
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
                .onPositive { dialog, which ->

                    userEntity.age = age.text.toString().trim().toInt()
                    userEntity.name = name.text.toString().trim()
                    userListFragmentPresenterImpl.modifyUser(userEntity,postion)
                }
                .customView(view,false)
                .show()

    }

    override fun initView() {

        userListFragmentPresenterImpl.attachView(this)
        recycle.layoutManager=LinearLayoutManager(mContext)
        recycle.itemAnimator = DefaultItemAnimator()
        recycle.adapter = userListAdapter
        userListAdapter.setOnItemClickListener{
            adapter, view, position ->
            showModifyUser(userListAdapter.getItem(position)!!,position)
        }
        userListAdapter.setOnItemLongClickListener {
            adapter, view, position ->
             showDelectUserDialog(userListAdapter.getItem(position)!!,position)
             true

        }
    }

    override fun lazyLoad() {

    }

    override fun getLayoutId(): Int = R.layout.fragment_userlist

    override fun initEventAndData() {

    }
    override fun onSelected() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun verifyStep(): VerificationError? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(error: VerificationError) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
}