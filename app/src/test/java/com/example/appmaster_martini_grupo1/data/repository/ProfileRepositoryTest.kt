package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ProfileDao
import com.example.appmaster_martini_grupo1.data.model.Profile
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProfileRepositoryTest {

    private val profile = Profile(userId = 1L, displayName = "Ana", email = "ana@test.cl")

    @Test
    fun get_by_user_devuelve_el_perfil() = runBlocking {
        val dao = mockk<ProfileDao>()
        coEvery { dao.getByUserId(1L) } returns profile
        val repository = ProfileRepository(dao)

        assertEquals(profile, repository.getByUserId(1L))
    }

    @Test
    fun get_by_user_inexistente_devuelve_null() = runBlocking {
        val dao = mockk<ProfileDao>()
        coEvery { dao.getByUserId(99L) } returns null
        val repository = ProfileRepository(dao)

        assertNull(repository.getByUserId(99L))
    }

    @Test
    fun save_llama_al_dao() = runBlocking {
        val dao = mockk<ProfileDao>(relaxed = true)
        coEvery { dao.upsert(profile) } just Runs
        val repository = ProfileRepository(dao)

        repository.save(profile)

        coVerify { dao.upsert(profile) }
    }

    @Test
    fun observe_by_user_emite_el_perfil() = runBlocking {
        val dao = mockk<ProfileDao>()
        every { dao.observeByUserId(1L) } returns flowOf(profile)
        val repository = ProfileRepository(dao)

        assertEquals(profile, repository.observeByUserId(1L).first())
    }
}
