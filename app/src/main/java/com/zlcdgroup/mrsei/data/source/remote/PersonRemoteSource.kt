package com.zlcdgroup.mrsei.data.source.remote

import com.akingyin.base.call.ApiCallBack
import com.akingyin.base.net.RetrofitUtils
import com.akingyin.base.net.config.CommonConstants.imei
import com.akingyin.base.net.mode.ApiHost
import com.akingyin.base.net.okhttp.OkHttpUtils
import com.akingyin.base.rx.RxUtil
import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.IPersonSource
import com.zlcdgroup.mrsei.data.source.remote.api.LoginServerApi
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import com.zlcdgroup.mrsei.utils.RQ
import io.reactivex.disposables.Disposable
import javax.inject.Inject



/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 15:15
 * @version V1.0
 */
class PersonRemoteSource @Inject constructor()  : IPersonSource{


    override fun getAllPersons(): List<PersonEntity> {
        return emptyList()
    }

    override fun delectOutTowMonth() {
    }

    override fun savePerson(personEntity: PersonEntity) {
    }

    override fun delectPerson(personEntity: PersonEntity) {
    }

    override fun getLastPerson(): PersonEntity? {
        return null
    }

     var disposable :Disposable?=null
    override fun login(name: String, pass: String, callBack: ApiCallBack<LoginResultModel>) {
        try {
            var  serverApi = RetrofitUtils.createApi(LoginServerApi::class.java,OkHttpUtils.getOkHttpClientBuilder().build(),ApiHost.getHttp())
            val dataMap =  mutableMapOf<String ,String>()
            dataMap.put("account",name)
            dataMap.put("password", pass)
            val json = RQ.getJsonData("zlcd_mrmsei_login", "", imei, dataMap.toMap())
            disposable =   serverApi.login(json,RQ.getToken(json)).compose(RxUtil.IO_Main())
                  .subscribe({
                     if(it.status == 0){
                      callBack.call(it.data)
                  }else{
                      callBack.onError(it.msg)
                  }
                  },{
                      it.printStackTrace()
                      callBack.onError(it.message)
                  })

        }catch (e:Exception){
            e.printStackTrace()
            callBack.onError("出错了"+e.message)
        }

    }

    override fun logOut() {
    }

    override fun cancelSubscribe() {
        if(null != disposable && !disposable!!.isDisposed){
            disposable!!.dispose()
        }
    }
}