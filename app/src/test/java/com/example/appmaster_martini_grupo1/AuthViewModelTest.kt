package com.example.appmaster_martini_grupo1.viewmodel

import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AuthViewModelTest {

    @Test
    fun correoInvalidoMuestraErrorDeEmail() {
        val vm = AuthViewModel()
        vm.onEmailChange("no-es-correo")
        vm.onPasswordChange("Clave123")
        vm.onLoginClick()

        assertNotNull(vm.uiState.value.emailError)
        assertNull(vm.uiState.value.passwordError)
    }

    @Test
    fun registroConPasswordsDistintasMarcaConfirmacion() {
        val vm = AuthViewModel()
        vm.onNameChange("Ana")
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")
        vm.onConfirmPasswordChange("Clave999")
        vm.onRegisterClick()

        assertNotNull(vm.uiState.value.confirmPasswordError)
        assertNull(vm.uiState.value.emailError)
    }
}