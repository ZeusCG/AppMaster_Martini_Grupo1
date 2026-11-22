package com.example.appmaster_martini_grupo1.viewmodel

import com.example.appmaster_martini_grupo1.MainDispatcherRule
import com.example.appmaster_martini_grupo1.data.model.User
import com.example.appmaster_martini_grupo1.data.repository.ProfileRepository
import com.example.appmaster_martini_grupo1.data.repository.UserRepository
import com.example.appmaster_martini_grupo1.data.session.UserSession
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @After
    fun tearDown() {
        UserSession.clear()
    }


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

    @Test
    fun registroConPasswordDebilMarcaErrorDePassword() {
        val vm = AuthViewModel()
        vm.onNameChange("Ana")
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("123")
        vm.onConfirmPasswordChange("123")
        vm.onRegisterClick()

        assertNotNull(vm.uiState.value.passwordError)
    }

    @Test
    fun registroConRutInvalidoMarcaErrorDeRut() {
        val vm = AuthViewModel()
        vm.onNameChange("Ana")
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")
        vm.onConfirmPasswordChange("Clave123")
        vm.onRutChange("12345678-4")
        vm.onRegisterClick()

        assertNotNull(vm.uiState.value.rutError)
    }


    @Test
    fun login_exitoso_marca_la_sesion() = runTest {
        val userRepository = mockk<UserRepository>()
        val profileRepository = mockk<ProfileRepository>(relaxed = true)
        coEvery { userRepository.authenticate("ana@test.cl", "Clave123") } returns User(
            id = 1L, name = "Ana", email = "ana@test.cl", password = "hash"
        )
        val vm = AuthViewModel(userRepository, profileRepository)
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")

        vm.onLoginClick()

        assertTrue(vm.uiState.value.isLoggedIn)
    }

    @Test
    fun login_con_credenciales_malas_marca_error() = runTest {
        val userRepository = mockk<UserRepository>()
        val profileRepository = mockk<ProfileRepository>(relaxed = true)
        coEvery { userRepository.authenticate(any(), any()) } returns null
        val vm = AuthViewModel(userRepository, profileRepository)
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")

        vm.onLoginClick()

        assertNotNull(vm.uiState.value.authError)
    }

    @Test
    fun registro_exitoso_crea_usuario_y_perfil() = runTest {
        val userRepository = mockk<UserRepository>()
        val profileRepository = mockk<ProfileRepository>(relaxed = true)
        coEvery { userRepository.hashPassword(any()) } returns "hash"
        coEvery { userRepository.insertWithHash(any(), any()) } returns 1L
        coEvery { profileRepository.save(any()) } just Runs
        val vm = AuthViewModel(userRepository, profileRepository)
        vm.onNameChange("Ana")
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")
        vm.onConfirmPasswordChange("Clave123")

        vm.onRegisterClick()

        assertTrue(vm.uiState.value.isLoggedIn)
        coVerify { userRepository.insertWithHash(any(), "hash") }
        coVerify { profileRepository.save(any()) }
    }

    @Test
    fun login_sin_repositorio_avisa() {
        val vm = AuthViewModel()
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")

        vm.onLoginClick()

        assertNotNull(vm.uiState.value.authError)
    }

    @Test
    fun registro_sin_repositorio_avisa() {
        val vm = AuthViewModel()
        completaRegistro(vm)

        vm.onRegisterClick()

        assertNotNull(vm.uiState.value.authError)
    }

    @Test
    fun login_que_lanza_excepcion_avisa() = runTest {
        val userRepository = mockk<UserRepository>()
        coEvery { userRepository.authenticate(any(), any()) } throws RuntimeException("boom")
        val vm = AuthViewModel(userRepository, mockk<ProfileRepository>(relaxed = true))

        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")
        vm.onLoginClick()

        assertNotNull(vm.uiState.value.authError)
    }

    @Test
    fun registro_con_correo_duplicado_avisa() = runTest {
        val userRepository = mockk<UserRepository>()
        coEvery { userRepository.hashPassword(any()) } returns "hash"
        coEvery { userRepository.insertWithHash(any(), any()) } throws
            RuntimeException("UNIQUE constraint failed: users.email")
        val vm = AuthViewModel(userRepository, mockk<ProfileRepository>(relaxed = true))
        completaRegistro(vm)

        vm.onRegisterClick()

        assertEquals("Ese correo ya está registrado", vm.uiState.value.authError)
    }

    @Test
    fun registro_que_falla_avisa_generico() = runTest {
        val userRepository = mockk<UserRepository>()
        coEvery { userRepository.hashPassword(any()) } returns "hash"
        coEvery { userRepository.insertWithHash(any(), any()) } throws RuntimeException("otro error")
        val vm = AuthViewModel(userRepository, mockk<ProfileRepository>(relaxed = true))
        completaRegistro(vm)

        vm.onRegisterClick()

        assertTrue(vm.uiState.value.authError!!.contains("No se pudo crear"))
    }

    @Test
    fun nombre_corto_marca_error_de_nombre() {
        val vm = AuthViewModel()
        vm.onNameChange("A")
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")
        vm.onConfirmPasswordChange("Clave123")

        vm.onRegisterClick()

        assertNotNull(vm.uiState.value.nameError)
    }

    @Test
    fun telefono_y_rut_invalidos_marcan_sus_errores() {
        val vm = AuthViewModel()
        completaRegistro(vm)
        vm.onPhoneChange("123")
        vm.onRutChange("12345678-4")

        vm.onRegisterClick()

        assertNotNull(vm.uiState.value.phoneError)
        assertNotNull(vm.uiState.value.rutError)
    }

    @Test
    fun clearAuthError_lo_limpia() = runTest {
        val userRepository = mockk<UserRepository>()
        coEvery { userRepository.authenticate(any(), any()) } returns null
        val vm = AuthViewModel(userRepository, mockk<ProfileRepository>(relaxed = true))
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")
        vm.onLoginClick()

        vm.clearAuthError()

        assertNull(vm.uiState.value.authError)
    }

    private fun completaRegistro(vm: AuthViewModel) {
        vm.onNameChange("Ana")
        vm.onEmailChange("ana@test.cl")
        vm.onPasswordChange("Clave123")
        vm.onConfirmPasswordChange("Clave123")
    }
}
