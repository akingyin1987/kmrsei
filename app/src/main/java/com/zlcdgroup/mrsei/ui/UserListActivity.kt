package com.zlcdgroup.mrsei.ui

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import com.akingyin.base.BaseActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.UserListPresenterImpl
import com.zlcdgroup.mrsei.ui.adapter.UserListAdapter
import kotlinx.android.synthetic.main.activity_userlist.*
import kotlinx.android.synthetic.main.include_toolbar.*
import java.util.*
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 10:52
 * @version V1.0
 */
class UserListActivity  : BaseActivity(),UserListContract.View {

    @Inject
    lateinit var userListPresenterImpl: UserListPresenterImpl

    @Inject
    lateinit var userListAdapter: UserListAdapter

    override fun getLayoutId(): Int = R.layout.activity_userlist

    override fun initializationData(savedInstanceState: Bundle?) {
        setToolBar(toolbar,"测试")
        userListPresenterImpl.attachView(this)
        recycle.layoutManager = LinearLayoutManager(this)
        recycle.itemAnimator = DefaultItemAnimator()
        recycle.adapter = userListAdapter
        userListAdapter.setNewData(userListPresenterImpl.getUserList())
        userListAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener {
            adapter, view, position ->
            showSucces(userListAdapter.getItem(position)?.name)
        }
        fab.setOnClickListener {
            view ->         var  random :Random = Random()
                            var   userEntity: UserEntity = UserEntity()
                            userEntity.name="name1"+random.nextInt(100)
                            userEntity.age = random.nextInt(100)
                            userListPresenterImpl.addUser(userEntity)



        }

    }

    override fun onSaveInstanceData(outState: Bundle?) {
    }

    override fun initView() {
    }

    override fun startRequest() {

    }

    override fun showUserList(userEntitys: List<UserEntity>?) {
        userListAdapter.setNewData(userEntitys)
    }

    override fun showAddUser(userEntity: UserEntity) {
       // userListAdapter.addData(userEntity)
        var  intent :Intent = Intent()
        intent.setClass(this,SteperActivity::class.java)
        startActivity(intent)

    }

    override fun showDelect(userEntity: UserEntity, postion: Int) {

    }
}