package com.zlcdgroup.mrsei.ui.fragment

import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.widget.EditText
import com.afollestad.materialdialogs.MaterialDialog
import com.akingyin.base.BaseFragment
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.presenter.UserListFragmentContract
import com.zlcdgroup.mrsei.presenter.UserListFragmentPresenterImpl
import com.zlcdgroup.mrsei.ui.adapter.UserListAdapter
import com.zlcdgroup.mrsei.ui.adapter.UserViewHolder
import kotlinx.android.synthetic.main.fragment_userlist.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:01
 * @version V1.0
 */


class  UserListFragment @Inject constructor() :BaseFragment() ,UserListFragmentContract.View {

    @Inject
    lateinit var userListFragmentPresenterImpl: UserListFragmentPresenterImpl

    @Inject
    lateinit var userListAdapter: UserListAdapter

    override fun showUserList(userEntitys: List<UserEntity>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAddUserDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showDelectUserDialog(userEntity: UserEntity, postion: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAdapter(): BaseQuickAdapter<UserEntity, UserViewHolder> {
       return  userListAdapter
    }

    override fun showModifyUser(userEntity: UserEntity, postion: Int) {
        MaterialDialog.Builder(mContext!!).title("用户信息修改")
                .positiveText("确定")
                .negativeText("取消")
                .onPositive { dialog, which ->
                    var  name:EditText = dialog.findViewById(R.id.edit_name) as EditText
                    var  age :EditText = dialog.findViewById(R.id.edit_age) as EditText
                    userEntity.age = age.text.toString().trim().toInt()
                    userEntity.name = name.text.toString().trim()
                    userListFragmentPresenterImpl.modifyUser(userEntity,postion)
                }
                .customView(R.layout.dialog_edit_user,false)
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
    }

    override fun lazyLoad() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getLayoutId(): Int = R.layout.fragment_userlist

    override fun initEventAndData() {

    }

    override fun onDestroy() {
        userListFragmentPresenterImpl.detachView()
        super.onDestroy()
    }
}