package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.SolicitudContactoDao
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import com.example.appmaster_martini_grupo1.data.remote.ContactoApi
import com.example.appmaster_martini_grupo1.data.remote.ContactoRequest
import com.example.appmaster_martini_grupo1.data.remote.ContactoResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.match
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

class ContactoRepositoryTest {

    private enum class Comportamiento { OK, ERROR_HTTP, EXCEPCION }

    private class FakeContactoApi(private val comportamiento: Comportamiento) : ContactoApi {
        override suspend fun enviar(request: ContactoRequest): Response<ContactoResponse> =
            when (comportamiento) {
                Comportamiento.OK -> Response.success(
                    ContactoResponse(id = 101, title = request.title, body = request.body, userId = request.userId)
                )

                Comportamiento.ERROR_HTTP -> Response.error(500, "error".toResponseBody(null))
                Comportamiento.EXCEPCION -> throw IOException("sin red")
            }
    }

    private class FakeSolicitudDao : SolicitudContactoDao {
        private val items = MutableStateFlow<List<SolicitudContacto>>(emptyList())
        private var nextId = 1L

        override fun observeByUser(userId: Long): Flow<List<SolicitudContacto>> =
            items.map { list -> list.filter { it.userId == userId }.sortedByDescending { it.fecha } }

        override suspend fun getByUser(userId: Long): List<SolicitudContacto> =
            items.value.filter { it.userId == userId }.sortedByDescending { it.fecha }

        override suspend fun insert(solicitud: SolicitudContacto): Long {
            val id = nextId++
            items.value = items.value + solicitud.copy(id = id)
            return id
        }

        override suspend fun update(solicitud: SolicitudContacto) {
            items.value = items.value.map { if (it.id == solicitud.id) solicitud else it }
        }

        override suspend fun deleteById(id: Long) {
            items.value = items.value.filterNot { it.id == id }
        }
    }

    @Test
    fun send_exitoso_guarda_como_enviada() = runBlocking {
        val dao = FakeSolicitudDao()
        val repository = ContactoRepository(FakeContactoApi(Comportamiento.OK), dao)

        val resultado = repository.send(userId = 1L, asunto = "Consulta", mensaje = "Hola")

        assertEquals(SolicitudContacto.ESTADO_ENVIADA, resultado.estado)
        val historial = repository.getHistory(1L)
        assertEquals(1, historial.size)
        assertEquals(SolicitudContacto.ESTADO_ENVIADA, historial.first().estado)
    }

    @Test
    fun send_con_error_http_queda_pendiente() = runBlocking {
        val dao = FakeSolicitudDao()
        val repository = ContactoRepository(FakeContactoApi(Comportamiento.ERROR_HTTP), dao)

        val resultado = repository.send(userId = 1L, asunto = "Consulta", mensaje = "Hola")

        assertEquals(SolicitudContacto.ESTADO_PENDIENTE, resultado.estado)
        assertEquals(SolicitudContacto.ESTADO_PENDIENTE, repository.getHistory(1L).first().estado)
    }

    @Test
    fun send_con_excepcion_queda_pendiente() = runBlocking {
        val dao = FakeSolicitudDao()
        val repository = ContactoRepository(FakeContactoApi(Comportamiento.EXCEPCION), dao)

        val resultado = repository.send(userId = 1L, asunto = "Consulta", mensaje = "Hola")

        assertEquals(SolicitudContacto.ESTADO_PENDIENTE, resultado.estado)
    }

    @Test
    fun sin_api_queda_pendiente() = runBlocking {
        val repository = ContactoRepository(solicitudDao = FakeSolicitudDao())

        val resultado = repository.send(userId = 1L, asunto = "Consulta", mensaje = "Hola")

        assertEquals(SolicitudContacto.ESTADO_PENDIENTE, resultado.estado)
    }

    @Test
    fun delete_quita_del_historial() = runBlocking {
        val dao = FakeSolicitudDao()
        val repository = ContactoRepository(FakeContactoApi(Comportamiento.OK), dao)
        repository.send(userId = 1L, asunto = "Consulta", mensaje = "Hola")
        val id = repository.getHistory(1L).first().id

        repository.delete(id)

        assertTrue(repository.getHistory(1L).isEmpty())
    }

    @Test
    fun send_marca_enviada_usando_el_api_y_el_dao() = runBlocking {
        val api = mockk<ContactoApi>()
        val dao = mockk<SolicitudContactoDao>(relaxed = true)
        coEvery { dao.insert(any()) } returns 5L
        coEvery { api.enviar(any()) } returns Response.success(
            ContactoResponse(id = 101, title = "a", body = "b", userId = 1L)
        )
        val repository = ContactoRepository(api, dao)

        val resultado = repository.send(userId = 1L, asunto = "a", mensaje = "b")

        assertEquals(SolicitudContacto.ESTADO_ENVIADA, resultado.estado)
        coVerify { api.enviar(any()) }
        coVerify { dao.update(match { it.estado == SolicitudContacto.ESTADO_ENVIADA }) }
    }
}
