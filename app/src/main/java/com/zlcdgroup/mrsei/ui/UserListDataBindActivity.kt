package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import android.widget.EditText
import androidx.annotation.Nullable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.akingyin.base.BaseDaggerActivity
import com.akingyin.base.ext.click
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.impl.UserListPresenterImpl
import com.zlcdgroup.mrsei.ui.adapter.DataBindUserListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_userlist.*
import kotlinx.android.synthetic.main.include_toolbar.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/15 15:30
 * @version V1.0
 */
@AndroidEntryPoint
class UserListDataBindActivity :BaseDaggerActivity(), UserListContract.View{

    @Inject
    lateinit var userListPresenterImpl: UserListPresenterImpl

    @Inject
    lateinit var userListAdapter: DataBindUserListAdapter

    override fun useDataBindView()=true

    override fun getLayoutId() = R.layout.activity_userlist

    override fun initializationData(savedInstanceState: Bundle?) {
       DataBindingUtil.setContentView<ViewDataBinding>(this,getLayoutId())
       recycle.layoutManager = LinearLayoutManager(this)
       recycle.itemAnimator = DefaultItemAnimator()
       recycle.adapter = userListAdapter
       userListAdapter.setNewInstance(userListPresenterImpl.getUserList()?.toMutableList())
       userListPresenterImpl.attachView(this)
        setToolBar(toolbar,"dataBind")
       fab.click {
           showAddOrModifyUser(UserEntity(),-1)
       }
        userListAdapter.setOnItemClickListener { _, _, position ->
            val userEntity = userListAdapter.getItem(position)
            showAddOrModifyUser(userEntity!!,position)
        }
    }


    private fun    showAddOrModifyUser(@Nullable  userEntity: UserEntity, postion: Int  ){
       MaterialDialog(this).show {
           title(text = "用户信息修改")
           positiveButton(text = "确定")
           negativeButton(text = "取消")
           setContentView(R.layout.dialog_edit_user)
           positiveButton {
               val name: EditText = it.findViewById(R.id.edit_name) as EditText
               val age: EditText = it.findViewById(R.id.edit_age) as EditText
               userEntity.age = age.text.toString().trim().toInt()
               userEntity.name = name.text.toString().trim()
               if(null == userEntity.id){
                   userListPresenterImpl.addUser(userEntity)
               }else{
                   userListPresenterImpl.modifyUser(userEntity,postion)
               }
           }
       }

    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
    }

    override fun startRequest() {
    }

    override fun showUserList(userEntitys: List<UserEntity>?) {
        userListAdapter.setNewInstance(userEntitys?.toMutableList())
    }

    override fun showAddUser(userEntity: UserEntity) {
        userListAdapter.addData(0,userEntity)
    }

    override fun notifyModifyUser(userEntity: UserEntity, postion: Int) {
        userListAdapter.notifyItemChanged(postion)
    }

    override fun showDelect(userEntity: UserEntity, postion: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        userListPresenterImpl.detachView()
    }
}