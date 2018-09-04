package com.zlcdgroup.mrsei.presenter

import com.akingyin.base.BasePresenter
import com.zlcdgroup.mrsei.data.entity.UserEntity
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 13:40
 * @version V1.0
 */




class UserListPresenterImpl @Inject constructor():BasePresenter<UserListContract.View>(),UserListContract.Presenter {




    override fun getUserList(): List<UserEntity> {

        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addUser(userEntity: UserEntity): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delectUser(userEntity: UserEntity): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}