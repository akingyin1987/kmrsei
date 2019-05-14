package com.zlcdgroup.mrsei.data.source.local

import com.akingyin.base.call.ApiCallBack
import com.akingyin.base.utils.DateUtil
import com.zlcdgroup.mrsei.data.db.dao.PersonEntityDao
import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.IPersonSource
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import javax.inject.Inject

/**
 * @ Description:
 * @author king
 * @ Date 2018/10/8 14:45
 * @version V1.0
 */
class PersonLocalSource @Inject constructor( var  personEntityDao: PersonEntityDao) : IPersonSource {

    override fun getAllPersons(): List<PersonEntity> {
        return  personEntityDao.queryBuilder().orderDesc(PersonEntityDao.Properties.LoginTime).list()
    }

    override fun delectOutTowMonth() =
            personEntityDao.queryBuilder().where(PersonEntityDao.Properties.LoginTime.lt(DateUtil.getNowTimeMills()-60*DateUtil.DAY)).buildDelete().executeDeleteWithoutDetachingEntities()

    override fun savePerson(personEntity: PersonEntity) {
        personEntityDao.save(personEntity)
     }

    override fun delectPerson(personEntity: PersonEntity) {
        personEntityDao.delete(personEntity)
    }

    override fun getLastPerson(): PersonEntity? {
       return personEntityDao.queryBuilder().orderDesc(PersonEntityDao.Properties.LoginTime).limit(1).build().list().run {
            if(size>0){
                this[0]
            }else{
                null
            }
        }
//        val   persons = personEntityDao.queryBuilder().orderDesc(PersonEntityDao.Properties.LoginTime).build().list()
//        if(persons.size>0){
//            return persons[0]
//        }
//        return null
    }

    override fun login(name: String, pass: String, callBack: ApiCallBack<LoginResultModel>) {
    }

    override fun logOut() {
    }

    override fun cancelSubscribe() {
    }
}