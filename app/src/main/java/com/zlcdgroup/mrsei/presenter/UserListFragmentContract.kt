package com.zlcdgroup.mrsei.presenter

import com.akingyin.base.IBaseView
import com.akingyin.base.IPresenter
import com.chad.library.adapter.base.BaseQuickAdapter
import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.ui.adapter.UserViewHolder

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/7 16:09
 * @version V1.0
 */
interface UserListFragmentContract {

    interface  View  : IBaseView {
        fun    showUserList(userEntitys: List<UserEntity>? )

        fun    showAddUserDialog( )

        fun    showModifyUser(userEntity: UserEntity,postion: Int)

        fun    showDelectUserDialog(userEntity: UserEntity, postion:Int)

        fun    getAdapter():BaseQuickAdapter<UserEntity,UserViewHolder>
    }


    interface   Presenter : IPresenter<View> {


        fun   getUserList():List<UserEntity>?

        fun   addUser(userEntity: UserEntity):Boolean

        fun   delectUser(userEntity: UserEntity,postion:Int):Boolean

        fun   modifyUser(userEntity: UserEntity,postion: Int):Boolean
    }
}