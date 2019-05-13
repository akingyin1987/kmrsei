package com.zlcdgroup.mrsei.presenter.impl

import com.akingyin.base.BasePresenter
import com.zlcdgroup.mrsei.presenter.UserAuthContract
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2019/5/10 13:48
 * @version V1.0
 */
class UserAuthPresenterImpl @Inject constructor() :BasePresenter<UserAuthContract.View>(),UserAuthContract.Presenter {



    override fun cancelSubscribe() {
    }


}