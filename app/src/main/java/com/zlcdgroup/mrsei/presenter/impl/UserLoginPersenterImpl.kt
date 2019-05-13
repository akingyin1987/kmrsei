package com.zlcdgroup.mrsei.presenter.impl

import com.akingyin.base.BasePresenter
import com.akingyin.base.call.ApiCallBack
import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.PersonRepository
import com.zlcdgroup.mrsei.data.source.remote.api.LoginServerApi
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 14:38
 * @version V1.0
 */
class UserLoginPersenterImpl @Inject constructor(var personRepository: PersonRepository): BasePresenter<UserLoginContract.View>(), UserLoginContract.Presenter {


    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var retrofit : Retrofit

    override fun getListPersons(): List<PersonEntity> {
        return  personRepository.getAllPersons()
    }

    override fun delectOutTowMothsPersons() {
        personRepository.delectOutTowMonth()
    }

    override fun getLastPerson(): PersonEntity? {
        return  personRepository.getLastPerson()
    }

    override fun login(name: String, pass: String) {


        var loginServerApi =  retrofit.create(LoginServerApi::class.java)
        if(name.isEmpty()){
            mRootView!!.showError("用户名不可为空！")
            return
        }
        if(pass.isEmpty()){
            mRootView!!.showError("密码不可为空！")
            return
        }
        mRootView!!.showLoadDialog("登录中..")

       personRepository.login(name,pass, object : ApiCallBack<LoginResultModel> {
           override fun call(resultModel: LoginResultModel) {
               mRootView!!.hideLoadDialog()
           }

           override fun onError(msg: String) {
                mRootView?.let {
                    it.hideLoadDialog()
                    it.showError(msg)
                    it.goToMainActivity()
                }



           }
       })
    }

    override fun cancelSubscribe() {
        personRepository.cancelSubscribe()
    }
}