package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.UserDao
import com.example.appmaster_martini_grupo1.data.model.User
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun register(user: User): Long = userDao.insert(user)

    suspend fun authenticate(email: String, password: String): User? =
        userDao.authenticate(email, password)

    suspend fun findByEmail(email: String): User? = userDao.findByEmail(email)

    suspend fun getById(id: Long): User? = userDao.findById(id)

    fun observeAll(): Flow<List<User>> = userDao.observeAll()
}
