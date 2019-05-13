package com.zlcdgroup.mrsei.presenter.impl

import com.zlcdgroup.mrsei.data.entity.PersonEntity
import com.zlcdgroup.mrsei.data.source.PersonRepository
import com.zlcdgroup.mrsei.presenter.UserLoginContract
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations



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

    lateinit var userLoginPersenterImpl: UserLoginPersenterImpl

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userLoginPersenterImpl = UserLoginPersenterImpl(personRepository)
        userLoginPersenterImpl.attachView(view)

        `when`(view.showError("")).then {
            println("test--->>>>>>>")
        }
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getListPersons() {
        var  list = mutableListOf<PersonEntity>()
        var personEntity = PersonEntity()
        personEntity.personAccount="test"

        `when`(personRepository.getAllPersons()).thenReturn(list)
       var personEntity1 = Mockito.verify(userLoginPersenterImpl.getListPersons()).last()
        assert(personEntity1.personAccount.equals("test"))
    }

    @Test
    fun delectOutTowMothsPersons() {
    }

    @Test
    fun getLastPerson() {
    }

    @Test
    fun login() {
    }
}