package com.example.appmaster_martini_grupo1.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactoViewModelTest {

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
}
