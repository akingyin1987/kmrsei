package com.zlcdgroup.mrsei.presenter

import com.akingyin.base.IBaseView
import com.akingyin.base.IPresenter

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 13:47
 * @version V1.0
 */
interface UserAuthContract {

    interface  View  : IBaseView {


    }


    interface   Presenter : IPresenter<View> {



        fun cancelSubscribe()

    }
}