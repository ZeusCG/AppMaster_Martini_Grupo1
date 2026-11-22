package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.UserDao
import com.example.appmaster_martini_grupo1.data.model.User
import com.example.appmaster_martini_grupo1.domain.PasswordHasher
import kotlinx.coroutines.flow.Flow

class UserRepository(
    private val userDao: UserDao
) {

    /** Guarda la contraseña con sal (nunca en texto plano) y devuelve el id del nuevo usuario. */
    suspend fun register(user: User): Long =
        userDao.insert(user.copy(password = PasswordHasher.hash(user.password)))

    /** Busca por correo y verifica la contraseña contra el hash almacenado. */
    suspend fun authenticate(email: String, password: String): User? {
        val user = userDao.findByEmail(email) ?: return null
        return if (PasswordHasher.verify(password, user.password)) user else null
    }

    suspend fun findByEmail(email: String): User? = userDao.findByEmail(email)

    suspend fun getById(id: Long): User? = userDao.findById(id)

    fun observeAll(): Flow<List<User>> = userDao.observeAll()
}
