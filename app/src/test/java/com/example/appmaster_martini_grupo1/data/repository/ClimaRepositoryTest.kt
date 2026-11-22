package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ClimaDao
import com.example.appmaster_martini_grupo1.data.local.ClimaEntity
import com.example.appmaster_martini_grupo1.data.remote.ClimaApi
import com.example.appmaster_martini_grupo1.data.remote.ClimaCurrent
import com.example.appmaster_martini_grupo1.data.remote.ClimaResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ClimaRepositoryTest {

    private class FakeClimaApi(
        private val respuesta: ClimaResponse? = null,
        private val falla: Boolean = false
    ) : ClimaApi {
        override suspend fun getClima(latitude: Double, longitude: Double): ClimaResponse {
            if (falla) throw RuntimeException("sin red")
            return respuesta ?: error("respuesta no configurada")
        }
    }

    private class FakeClimaDao : ClimaDao {
        private var item: ClimaEntity? = null
        override suspend fun get(): ClimaEntity? = item
        override suspend fun save(clima: ClimaEntity) {
            item = clima
        }
    }

    private fun respuestaOk() = ClimaResponse(
        ClimaCurrent(time = "2026-11-22T02:30", temperature = 15.5, humidity = 63)
    )

    @Test
    fun traduce_la_respuesta_a_clima_actual() = runBlocking {
        val repository = ClimaRepository(FakeClimaApi(respuestaOk()))

        val clima = repository.getClima()

        assertEquals(15.5, clima.temperatura, 0.0)
        assertEquals(63, clima.humedad)
        assertEquals("2026-11-22T02:30", clima.hora)
    }

    @Test
    fun respuesta_sin_current_lanza_error() {
        val repository = ClimaRepository(FakeClimaApi(ClimaResponse(current = null)))

        assertThrows(Exception::class.java) {
            runBlocking { repository.getClima() }
        }
    }

    @Test
    fun sin_api_lanza_error() {
        val repository = ClimaRepository()

        assertThrows(Exception::class.java) {
            runBlocking { repository.getClima() }
        }
    }

    @Test
    fun al_consultar_guarda_el_ultimo_clima() = runBlocking {
        val dao = FakeClimaDao()
        val repository = ClimaRepository(FakeClimaApi(respuestaOk()), dao)

        repository.getClima()

        val ultimo = repository.getUltimoClima()
        assertEquals(15.5, ultimo?.temperatura ?: -1.0, 0.0)
        assertEquals(63, ultimo?.humedad)
    }

    @Test
    fun sin_internet_devuelve_el_ultimo_clima_guardado() = runBlocking {
        val dao = FakeClimaDao()
        ClimaRepository(FakeClimaApi(respuestaOk()), dao).getClima()

        // Cae la red: nueva API que falla, mismo cache.
        val repository = ClimaRepository(FakeClimaApi(falla = true), dao)

        val resultado = repository.obtenerClima()

        assertTrue(resultado.desdeCache)
        assertEquals(15.5, resultado.clima.temperatura, 0.0)
    }

    @Test
    fun con_red_disponible_no_marca_cache() = runBlocking {
        val repository = ClimaRepository(FakeClimaApi(respuestaOk()), FakeClimaDao())

        val resultado = repository.obtenerClima()

        assertFalse(resultado.desdeCache)
    }

    @Test
    fun sin_internet_ni_cache_lanza_error() {
        val repository = ClimaRepository(FakeClimaApi(falla = true), FakeClimaDao())

        assertThrows(Exception::class.java) {
            runBlocking { repository.obtenerClima() }
        }
    }

    @Test
    fun sin_clima_guardado_devuelve_null() = runBlocking {
        val repository = ClimaRepository(FakeClimaApi(respuestaOk()), FakeClimaDao())

        assertNull(repository.getUltimoClima())
    }

    @Test
    fun consulta_a_traves_del_api_con_mockk() = runBlocking {
        val api = mockk<ClimaApi>()
        coEvery { api.getClima(any(), any()) } returns respuestaOk()
        val repository = ClimaRepository(api)

        val clima = repository.getClima()

        assertEquals(15.5, clima.temperatura, 0.0)
        coVerify { api.getClima(any(), any()) }
    }
}
