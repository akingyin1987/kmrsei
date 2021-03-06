package com.zlcdgroup.mrsei.presenter.impl

import com.akingyin.base.BasePresenter
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.data.source.UserRepository
import com.zlcdgroup.mrsei.presenter.UserListFragmentContract
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:17
 * @version V1.0
 */
open class UserListFragmentPresenterImpl @Inject constructor(var userRepository: UserRepository)
    :BasePresenter<UserListFragmentContract.View>(),UserListFragmentContract.Presenter {

    



    override fun getUserList(): List<UserEntity>? {
       return  userRepository.getUserList()
    }

    override fun addUser(userEntity: UserEntity): Boolean {
        if (userEntity.name.isEmpty()){
            mRootView?.let {
                it.showError("用户名不可为空")
                it.hideLoadDialog()
            }

            return false
        }
        if(userEntity.age<=0){
            mRootView?.showError("年龄不可为空")
            return false
        }
        val   result = userRepository.addUser(userEntity)
        if(result){
            mRootView?.getAdapter()?.addData(userEntity)
        }else{
            mRootView?.showError("新增数据失败")
        }
        return result
    }

    override fun modifyUser(userEntity: UserEntity, postion: Int): Boolean {
        if (userEntity.name.isEmpty()){
            mRootView?.let {
                it.showError("用户名不可为空")
                it.hideLoadDialog()
            }


            return false
        }
        if(userEntity.age<=0){
            mRootView?.showError("年龄不可为空")
            return false
        }
        val   result  =  userRepository.addUser(userEntity)
        if(result){
            mRootView?.getAdapter()?.setData(postion,userEntity)
        }else{
            mRootView?.showError("修改数据失败")
        }
        return result
    }

    override fun delectUser(userEntity: UserEntity, postion: Int): Boolean {
        var   result :Boolean = userRepository.delectUser(userEntity);
        if(result){
            mRootView?.getAdapter()?.removeAt(postion)
        }else{
            mRootView?.showError("删除数据失败")
        }
        return  result
    }


}