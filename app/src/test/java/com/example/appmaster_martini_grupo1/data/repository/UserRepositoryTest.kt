package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.UserDao
import com.example.appmaster_martini_grupo1.data.model.User
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UserRepositoryTest {

    private class FakeUserDao : UserDao {
        private val users = MutableStateFlow<List<User>>(emptyList())
        private var nextId = 1L

        override suspend fun insert(user: User): Long {
            val id = nextId++
            users.value = users.value + user.copy(id = id)
            return id
        }

        override suspend fun findByEmail(email: String): User? =
            users.value.firstOrNull { it.email == email }

        override suspend fun findById(id: Long): User? =
            users.value.firstOrNull { it.id == id }

        override fun observeAll(): Flow<List<User>> = users
    }

    @Test
    fun register_guarda_la_password_hasheada_no_en_texto_plano() = runBlocking {
        val dao = FakeUserDao()
        val repository = UserRepository(dao)

        val id = repository.register(User(name = "Ana", email = "ana@test.cl", password = "Martini2026"))

        val guardado = dao.findById(id)!!
        assertNotEquals("Martini2026", guardado.password)
        assertEquals("ana@test.cl", guardado.email)
    }

    @Test
    fun authenticate_acepta_la_password_correcta() = runBlocking {
        val repository = UserRepository(FakeUserDao())
        repository.register(User(name = "Ana", email = "ana@test.cl", password = "Martini2026"))

        val resultado = repository.authenticate("ana@test.cl", "Martini2026")

        assertTrue(resultado != null)
        assertEquals("ana@test.cl", resultado?.email)
    }

    @Test
    fun authenticate_rechaza_la_password_incorrecta() = runBlocking {
        val repository = UserRepository(FakeUserDao())
        repository.register(User(name = "Ana", email = "ana@test.cl", password = "Martini2026"))

        assertNull(repository.authenticate("ana@test.cl", "ClaveMala1"))
    }

    @Test
    fun register_hashea_antes_de_insertar_con_el_dao() = runBlocking {
        val dao = mockk<UserDao>()
        coEvery { dao.insert(any()) } returns 7L
        val repository = UserRepository(dao)

        val id = repository.register(User(name = "Ana", email = "ana@test.cl", password = "Martini2026"))

        assertEquals(7L, id)
        coVerify { dao.insert(match { it.password != "Martini2026" && it.email == "ana@test.cl" }) }
    }

    @Test
    fun authenticate_rechaza_correo_inexistente() = runBlocking {
        val repository = UserRepository(FakeUserDao())

        assertNull(repository.authenticate("nadie@test.cl", "Martini2026"))
    }
}
