package com.example.appmaster_martini_grupo1.viewmodel

import com.example.appmaster_martini_grupo1.MainDispatcherRule
import com.example.appmaster_martini_grupo1.data.model.Profile
import com.example.appmaster_martini_grupo1.data.repository.ProfileRepository
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

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


    @Test
    fun loadProfile_carga_los_datos() = runTest {
        val repository = mockk<ProfileRepository>()
        coEvery { repository.getByUserId(1L) } returns Profile(
            userId = 1L, displayName = "Ana", email = "ana@test.cl", phone = "+56912345678"
        )
        val vm = ProfileViewModel(repository)

        vm.loadProfile(1L)

        assertEquals("Ana", vm.uiState.value.displayName)
        assertEquals("ana@test.cl", vm.uiState.value.email)
    }

    @Test
    fun saveProfile_guarda_en_el_repositorio() = runTest {
        val repository = mockk<ProfileRepository>(relaxed = true)
        coEvery { repository.save(any()) } just Runs
        val vm = ProfileViewModel(repository)
        vm.onDisplayNameChange("Ana")
        vm.onEmailChange("ana@test.cl")
        vm.onPhoneChange("+56912345678")

        vm.saveProfile()

        coVerify { repository.save(any()) }
        assertEquals("Perfil guardado correctamente", vm.uiState.value.savedMessage)
    }

    @Test
    fun onPhotoPicked_y_removed_actualizan_la_foto() {
        viewModel.onPhotoPicked("content://foto.jpg")
        assertEquals("content://foto.jpg", viewModel.uiState.value.photoUri)

        viewModel.onPhotoRemoved()
        assertNull(viewModel.uiState.value.photoUri)
    }

    @Test
    fun saveProfile_sin_repositorio_avisa() {
        viewModel.onDisplayNameChange("Ana")
        viewModel.onEmailChange("ana@test.cl")

        viewModel.saveProfile()

        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun loadProfile_con_error_avisa() = runTest {
        val repository = mockk<ProfileRepository>()
        coEvery { repository.getByUserId(any()) } throws RuntimeException("boom")
        val vm = ProfileViewModel(repository)

        vm.loadProfile(1L)

        assertNotNull(vm.uiState.value.error)
    }

    @Test
    fun saveProfile_con_error_avisa() = runTest {
        val repository = mockk<ProfileRepository>()
        coEvery { repository.save(any()) } throws RuntimeException("boom")
        val vm = ProfileViewModel(repository)
        vm.onDisplayNameChange("Ana")
        vm.onEmailChange("ana@test.cl")

        vm.saveProfile()

        assertNotNull(vm.uiState.value.error)
    }

    @Test
    fun loadProfile_carga_los_intereses() = runTest {
        val repository = mockk<ProfileRepository>()
        coEvery { repository.getByUserId(1L) } returns Profile(
            userId = 1L, displayName = "Ana", email = "ana@test.cl",
            phone = "+56912345678", interestsCsv = "recetas, cursos"
        )
        val vm = ProfileViewModel(repository)

        vm.loadProfile(1L)

        assertEquals(setOf("recetas", "cursos"), vm.uiState.value.selectedInterests)
    }
}
