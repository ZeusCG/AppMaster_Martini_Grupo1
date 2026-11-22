package com.example.appmaster_martini_grupo1.viewmodel

import com.example.appmaster_martini_grupo1.MainDispatcherRule
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import com.example.appmaster_martini_grupo1.data.repository.ContactoRepository
import com.example.appmaster_martini_grupo1.data.session.UserSession
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ContactoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @After
    fun tearDown() {
        UserSession.clear()
    }


    private val viewModel = ContactoViewModel()

    @Test
    fun historial_inicia_vacio() {
        assertTrue(viewModel.uiState.value.history.isEmpty())
    }

    @Test
    fun send_sin_asunto_marca_error() {
        viewModel.onMessageChange("Quisiera información de coberturas")

        viewModel.send()

        assertNotNull(viewModel.uiState.value.form.subjectError)
        assertNull(viewModel.uiState.value.form.messageError)
    }

    @Test
    fun send_sin_mensaje_marca_error() {
        viewModel.onSubjectChange("Consulta de producto")

        viewModel.send()

        assertNotNull(viewModel.uiState.value.form.messageError)
    }

    @Test
    fun onSubjectChange_limpia_el_error() {
        viewModel.send()
        assertNotNull(viewModel.uiState.value.form.subjectError)

        viewModel.onSubjectChange("Consulta")

        assertNull(viewModel.uiState.value.form.subjectError)
    }

    @Test
    fun send_sin_sesion_no_envia() {
        viewModel.onSubjectChange("Consulta")
        viewModel.onMessageChange("Mensaje")

        viewModel.send()

        assertTrue(viewModel.uiState.value.sendState is SendState.Error)
        assertEquals("Consulta", viewModel.uiState.value.form.subject)
    }


    @Test
    fun send_exitoso_expone_sent() = runTest {
        UserSession.set(1L)
        val repository = mockk<ContactoRepository>(relaxed = true)
        every { repository.isPersistent } returns false
        coEvery { repository.send(1L, "Consulta", "Hola") } returns SolicitudContacto(
            id = 1L, userId = 1L, asunto = "Consulta", mensaje = "Hola",
            estado = SolicitudContacto.ESTADO_ENVIADA
        )
        val vm = ContactoViewModel(repository)

        vm.onSubjectChange("Consulta")
        vm.onMessageChange("Hola")
        vm.send()

        assertTrue(vm.uiState.value.sendState is SendState.Sent)
        assertEquals("", vm.uiState.value.form.subject)
    }

    @Test
    fun send_que_queda_pendiente_expone_error() = runTest {
        UserSession.set(1L)
        val repository = mockk<ContactoRepository>(relaxed = true)
        every { repository.isPersistent } returns false
        coEvery { repository.send(1L, "Consulta", "Hola") } returns SolicitudContacto(
            id = 1L, userId = 1L, asunto = "Consulta", mensaje = "Hola",
            estado = SolicitudContacto.ESTADO_PENDIENTE
        )
        val vm = ContactoViewModel(repository)

        vm.onSubjectChange("Consulta")
        vm.onMessageChange("Hola")
        vm.send()

        assertTrue(vm.uiState.value.sendState is SendState.Error)
    }

    @Test
    fun deleteRequest_quita_del_historial_y_borra_en_el_repositorio() = runTest {
        UserSession.set(1L)
        val item = SolicitudContacto(id = 9L, userId = 1L, asunto = "a", mensaje = "b")
        val repository = mockk<ContactoRepository>(relaxed = true)
        every { repository.isPersistent } returns true
        every { repository.observeHistory(1L) } returns flowOf(listOf(item))
        coEvery { repository.delete(9L) } just Runs
        val vm = ContactoViewModel(repository)

        assertTrue(vm.uiState.value.history.any { it.id == 9L })

        vm.deleteRequest(9L)

        assertTrue(vm.uiState.value.history.isEmpty())
        coVerify { repository.delete(9L) }
    }
}
