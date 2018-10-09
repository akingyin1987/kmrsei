package com.zlcdgroup.mrsei.presenter.impl

import com.akingyin.base.BasePresenter
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.data.source.UserRepository
import com.zlcdgroup.mrsei.presenter.UserListContract
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 13:40
 * @version V1.0
 */




class UserListPresenterImpl @Inject constructor(var  userRepository: UserRepository):BasePresenter<UserListContract.View>(), UserListContract.Presenter {




    override fun getUserList(): List<UserEntity>? {
        return  userRepository.getUserList()
    }

    override fun addUser(userEntity: UserEntity): Boolean {
        var  result:Boolean = userRepository.addUser(userEntity)
        mRootView?.showAddUser(userEntity)
        return  result
    }

    override fun delectUser(userEntity: UserEntity): Boolean {
        return  userRepository.delectUser(userEntity)
    }
}