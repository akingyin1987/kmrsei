package com.zlcdgroup.mrsei.presenter

import com.akingyin.base.IBaseView
import com.akingyin.base.IPresenter
import com.zlcdgroup.mrsei.data.entity.UserEntity

/**
 * @ Description:
 * @author king
 * @ Date 2018/9/4 10:54
 * @version V1.0
 */
interface UserListContract {

     interface  View  : IBaseView{
          fun    showUserList(userEntitys: List<UserEntity>? )

          fun    showAddUser( userEntity: UserEntity)

          fun    showDelect(userEntity: UserEntity,  postion:Int)
     }


      interface   Presenter :IPresenter<View>{


          fun   getUserList():List<UserEntity>?

          fun   addUser(userEntity: UserEntity):Boolean

          fun   delectUser(userEntity: UserEntity):Boolean
    }
}