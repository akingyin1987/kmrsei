package com.zlcdgroup.mrsei.presenter.impl

import com.akingyin.base.call.ApiCallBack
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.PersonRepository
import com.zlcdgroup.mrsei.data.source.remote.model.LoginResultModel
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.*


/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/5/13 17:09
 */
class UserLoginPersenterImplTest {

    @Mock
    lateinit var personRepository: PersonRepository

    @Mock
    lateinit var view: UserLoginContract.View

    @Captor
    lateinit var callBack: ArgumentCaptor<ApiCallBack<LoginResultModel>>

    lateinit var userLoginPersenterImpl: UserLoginPersenterImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userLoginPersenterImpl = UserLoginPersenterImpl(personRepository)
        userLoginPersenterImpl.attachView(view)


    }

    @After
    fun tearDown() {
    }

    @Test
    fun getListPersons() {
        val  list = mutableListOf<PersonEntity>()
        val personEntity = PersonEntity()
        personEntity.personAccount="test"
        list.add(personEntity)
        Mockito.`when`(personRepository.getAllPersons()).thenReturn(list)

        println(personRepository.getAllPersons()[0].personAccount)
      //  Mockito.verify(personRepository.getAllPersons())[0]
    }

    @Test
    fun delectOutTowMothsPersons() {

    }

    @Test
    fun getLastPerson() {
    }

    @Test
    fun login() {
        userLoginPersenterImpl.login("test","")
        Mockito.verify(userLoginPersenterImpl.mRootView)?.showError("密码不可为空！")
        userLoginPersenterImpl.login("","22")
        Mockito.verify(userLoginPersenterImpl.mRootView)?.showError("用户名不可为空！")
        userLoginPersenterImpl.login("test","22")
        val captor = argumentCaptor<ApiCallBack<LoginResultModel>>()
        val name  = argumentCaptor<String>()
        val pass = argumentCaptor<String>()
        Mockito.verify(userLoginPersenterImpl.personRepository).login(name.capture() ,pass.capture(), captor.capture() )
        assert(name.firstValue.equals("test"))
        println("name=${name.lastValue}:${pass.lastValue}")
        println(captor.allValues)
    }
}