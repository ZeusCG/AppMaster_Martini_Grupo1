package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.UserDao
import com.example.appmaster_martini_grupo1.data.model.User
import com.example.appmaster_martini_grupo1.domain.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao
) {

    suspend fun register(user: User): Long {
        val hash = hashPassword(user.password)
        return insertWithHash(user, hash)
    }

    suspend fun hashPassword(password: String): String =
        withContext(Dispatchers.Default) { PasswordHasher.hash(password) }

    suspend fun insertWithHash(user: User, hashedPassword: String): Long {
        val normalizado = user.copy(email = normalize(user.email))
        return userDao.insert(normalizado.copy(password = hashedPassword))
    }

    suspend fun authenticate(email: String, password: String): User? {
        val user = userDao.findByEmail(normalize(email)) ?: return null
        val ok = withContext(Dispatchers.Default) { PasswordHasher.verify(password, user.password) }
        return if (ok) user else null
    }

    suspend fun findByEmail(email: String): User? = userDao.findByEmail(normalize(email))

    suspend fun getById(id: Long): User? = userDao.findById(id)

    fun observeAll(): Flow<List<User>> = userDao.observeAll()

    private fun normalize(email: String): String = email.trim().lowercase()
}
