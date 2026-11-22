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

    /** Guarda la contraseña con sal (nunca en texto plano) y devuelve el id del nuevo usuario. */
    suspend fun register(user: User): Long {
        val hash = hashPassword(user.password)
        return insertWithHash(user, hash)
    }

    /** H-013: PBKDF2 es costoso; se calcula fuera del hilo principal. */
    suspend fun hashPassword(password: String): String =
        withContext(Dispatchers.Default) { PasswordHasher.hash(password) }

    /**
     * Inserta el usuario con la contraseña ya hasheada. Se separa el hash del insert
     * para poder envolver varias escrituras en una transacción sin cambiar de dispatcher dentro de ella.
     */
    suspend fun insertWithHash(user: User, hashedPassword: String): Long {
        val normalizado = user.copy(email = normalize(user.email))
        return userDao.insert(normalizado.copy(password = hashedPassword))
    }

    /** Busca por correo (normalizado) y verifica la contraseña contra el hash almacenado. */
    suspend fun authenticate(email: String, password: String): User? {
        val user = userDao.findByEmail(normalize(email)) ?: return null
        val ok = withContext(Dispatchers.Default) { PasswordHasher.verify(password, user.password) }
        return if (ok) user else null
    }

    suspend fun findByEmail(email: String): User? = userDao.findByEmail(normalize(email))

    suspend fun getById(id: Long): User? = userDao.findById(id)

    fun observeAll(): Flow<List<User>> = userDao.observeAll()

    /** H-012: los correos se guardan y se buscan en minúsculas y sin espacios. */
    private fun normalize(email: String): String = email.trim().lowercase()
}
