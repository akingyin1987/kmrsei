package com.zlcdgroup.mrsei.data.source

import com.zlcdgroup.mrsei.data.entity.UserEntity
import com.zlcdgroup.mrsei.data.source.local.UserLocalSource
import com.zlcdgroup.mrsei.data.source.remote.UserRemoteSource
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * @author king
 * @version V1.0
 * @ Description:
 * @ Date 2019/5/13 17:53
 */
class UserRepositoryTest {

    lateinit var userRepository: UserRepository

    @Mock
    lateinit var   userLocalSource :UserLocalSource

    @Mock
    lateinit var   userRemoteSource: UserRemoteSource

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        userRepository = UserRepository(userLocalSource,userRemoteSource)

    }

    @Test
    fun getUserList() {
    }

    @Test
    fun getUserById() {
    }

    @Test
    fun addUser() {
        var userEntity = UserEntity()
        userEntity.name="test"
        userEntity.age = 1
        userRepository.addUser(userEntity)
        println("id=${userEntity.id}")
    }

    @Test
    fun delectUser() {
    }

    @Test
    fun modeiyUser() {
    }
}