package com.zlcdgroup.mrsei.presenter

import com.akingyin.base.IBaseView
import com.akingyin.base.IPresenter
import com.zlcdgroup.mrsei.data.entity.PersonEntity

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 12:18
 * @version V1.0
 */
interface UserLoginContract {

    interface  View  : IBaseView {

        fun   showConfigDialog(  message:String)

        fun   goToMainActivity()

        fun   setAppTheme(theme:String)
    }


    interface   Presenter : IPresenter<View> {

        fun  getListPersons():List<PersonEntity>

        fun  delectOutTowMothsPersons()

        fun  getLastPerson():PersonEntity?

        fun  login(name:String,pass :String)

        fun  getTheme():String

        fun  saveAppTheme(theme:String)




        fun cancelSubscribe()

    }
}