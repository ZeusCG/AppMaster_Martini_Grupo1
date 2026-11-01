package com.example.appmaster_martini_grupo1.viewmodel

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileViewModelTest {

    private val viewModel = ProfileViewModel()

    @Test
    fun nombreCortoMarcaErrorDeNombre() {
        viewModel.onDisplayNameChange("A")
        viewModel.onEmailChange("ana@test.cl")
        viewModel.saveProfile()

        assertNotNull(viewModel.uiState.value.displayNameError)
    }

    @Test
    fun correoInvalidoMarcaErrorDeEmail() {
        viewModel.onDisplayNameChange("Ana Pérez")
        viewModel.onEmailChange("correo-malo")
        viewModel.saveProfile()

        assertNotNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun telefonoInvalidoMarcaErrorDeTelefono() {
        viewModel.onDisplayNameChange("Ana Pérez")
        viewModel.onEmailChange("ana@test.cl")
        viewModel.onPhoneChange("123")
        viewModel.saveProfile()

        assertNotNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun datosValidosNoMuestranErroresDeCampo() {
        viewModel.onDisplayNameChange("Ana Pérez")
        viewModel.onEmailChange("ana@test.cl")
        viewModel.onPhoneChange("+56912345678")
        viewModel.saveProfile()

        assertNull(viewModel.uiState.value.displayNameError)
        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.phoneError)
    }

    @Test
    fun toggleInterestAgregaYQuita() {
        viewModel.toggleInterest("recetas")
        assertTrue(viewModel.uiState.value.selectedInterests.contains("recetas"))

        viewModel.toggleInterest("recetas")
        assertTrue(viewModel.uiState.value.selectedInterests.isEmpty())
    }

    @Test
    fun loadProfileConDaoNuloNoRompe() {
        viewModel.loadProfile(1L)

        assertNotNull(viewModel.uiState.value)
    }
}
