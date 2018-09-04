package com.zlcdgroup.mrsei.ui

import android.os.Bundle
import com.akingyin.base.BaseActivity
import com.zlcdgroup.mrsei.R
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.presenter.UserListContract
import com.zlcdgroup.mrsei.presenter.UserListPresenterImpl
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


    init {
        print("userListPresenterImpl = "+(null == userListPresenterImpl))
        userListPresenterImpl.attachView(this)
    }

    override fun getLayoutId(): Int = R.layout.activity_userlist

    override fun initializationData(savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSaveInstanceData(outState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun startRequest() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showUserList(userEntitys: List<UserEntity>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showAddUser(userEntity: UserEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showDelect(userEntity: UserEntity) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}