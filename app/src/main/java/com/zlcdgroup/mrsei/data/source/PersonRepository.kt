package com.zlcdgroup.mrsei.data.source

import com.akingyin.base.call.ApiCallBack
import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.local.PersonLocalSource
import com.zlcdgroup.mrsei.data.source.remote.PersonRemoteSource
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import com.zlcdgroup.mrsei.di.qualifier.Local
import com.zlcdgroup.mrsei.di.qualifier.Remote
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 16:05
 * @version V1.0
 */
class PersonRepository @Inject constructor(@Local var personLocalSource: PersonLocalSource, @Remote var personRemoteSource: PersonRemoteSource) : IPersonSource {


    override fun getAllPersons(): List<PersonEntity> {
      return  personLocalSource.getAllPersons()
    }

    override fun delectOutTowMonth() {
        personLocalSource.delectOutTowMonth()
    }

    override fun savePerson(personEntity: PersonEntity) {
        personLocalSource.savePerson(personEntity)
    }

    override fun delectPerson(personEntity: PersonEntity) {
        personLocalSource.delectPerson(personEntity)
    }

    override fun getLastPerson(): PersonEntity? {
        return  personLocalSource.getLastPerson()
    }

    override fun login(name: String, pass: String, callBack: ApiCallBack<LoginResultModel>) {
        personRemoteSource.login(name,pass,callBack)
    }

    override fun logOut() {
    }

    override fun cancelSubscribe() {
        personRemoteSource.cancelSubscribe()
        personLocalSource.cancelSubscribe()
    }
}