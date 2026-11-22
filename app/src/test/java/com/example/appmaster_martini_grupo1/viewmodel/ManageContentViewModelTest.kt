package com.example.appmaster_martini_grupo1.viewmodel

import com.example.appmaster_martini_grupo1.MainDispatcherRule
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.repository.ContentRepository
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ManageContentViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val viewModel = ManageContentViewModel()

    @Test
    fun inicia_con_el_contenido_de_la_semilla() {
        assertTrue(viewModel.uiState.value.items.isNotEmpty())
    }

    @Test
    fun startCreate_limpia_el_formulario() {
        viewModel.startCreate()
        viewModel.onFieldChange(ContentField.TITLE, "Título previo")

        viewModel.startCreate()

        assertEquals("", viewModel.uiState.value.form.title)
        assertNull(viewModel.uiState.value.form.id)
    }

    @Test
    fun save_sin_titulo_marca_error_y_no_crea() {
        val total = viewModel.uiState.value.items.size
        viewModel.startCreate()
        viewModel.onFieldChange(ContentField.CATEGORY, "Cursos")

        viewModel.save()

        assertNotNull(viewModel.uiState.value.form.titleError)
        assertEquals(total, viewModel.uiState.value.items.size)
    }

    @Test
    fun save_sin_categoria_marca_error() {
        viewModel.startCreate()
        viewModel.onFieldChange(ContentField.TITLE, "Torta de chocolate")

        viewModel.save()

        assertNotNull(viewModel.uiState.value.form.categoryError)
    }

    @Test
    fun titulo_demasiado_largo_marca_error() {
        viewModel.startCreate()
        viewModel.onFieldChange(ContentField.TITLE, "x".repeat(ManageContentViewModel.MAX_TITLE + 1))
        viewModel.onFieldChange(ContentField.CATEGORY, "Cursos")

        viewModel.save()

        assertNotNull(viewModel.uiState.value.form.titleError)
    }

    @Test
    fun save_crea_contenido() {
        val total = viewModel.uiState.value.items.size
        viewModel.startCreate()
        viewModel.onTypeChange(ContentType.RECIPE)
        viewModel.onFieldChange(ContentField.TITLE, "Brownie de chocolate")
        viewModel.onFieldChange(ContentField.CATEGORY, "Postres")
        viewModel.onFieldChange(ContentField.DESCRIPTION, "Brownie húmedo con nueces")
        viewModel.onFieldChange(ContentField.PORTIONS, "8")

        viewModel.save()

        val items = viewModel.uiState.value.items
        assertEquals(total + 1, items.size)
        assertTrue(items.any { it.title == "Brownie de chocolate" })
        assertNotNull(viewModel.uiState.value.message)
        assertEquals("", viewModel.uiState.value.form.title)
    }

    @Test
    fun startEdit_carga_el_formulario_con_los_datos() {
        val item = viewModel.uiState.value.items.first()

        viewModel.startEdit(item.id)

        assertEquals(item.id, viewModel.uiState.value.form.id)
        assertEquals(item.title, viewModel.uiState.value.form.title)
        assertEquals(item.category, viewModel.uiState.value.form.category)
    }

    @Test
    fun save_con_id_actualiza_sin_duplicar() {
        val item = viewModel.uiState.value.items.first()
        val total = viewModel.uiState.value.items.size
        viewModel.startEdit(item.id)
        viewModel.onFieldChange(ContentField.TITLE, "Título editado")

        viewModel.save()

        val items = viewModel.uiState.value.items
        assertEquals(total, items.size)
        assertEquals("Título editado", items.first { it.id == item.id }.title)
    }

    @Test
    fun delete_quita_el_item() {
        val item = viewModel.uiState.value.items.first()
        val total = viewModel.uiState.value.items.size

        viewModel.delete(item.id)

        val items = viewModel.uiState.value.items
        assertEquals(total - 1, items.size)
        assertFalse(items.any { it.id == item.id })
    }

    // --- Con repositorio mockeado (persistencia) ---

    private fun repositorioPersistente(): ContentRepository {
        val repository = mockk<ContentRepository>(relaxed = true)
        every { repository.isPersistent } returns true
        every { repository.seed() } returns emptyList()
        every { repository.observeAll() } returns flowOf(emptyList<Content>())
        return repository
    }

    @Test
    fun save_persiste_en_el_repositorio() = runTest {
        val repository = repositorioPersistente()
        val vm = ManageContentViewModel(repository)
        vm.startCreate()
        vm.onFieldChange(ContentField.TITLE, "Torta")
        vm.onFieldChange(ContentField.CATEGORY, "Postres")

        vm.save()

        coVerify { repository.upsert(any()) }
    }

    @Test
    fun delete_llama_al_repositorio() = runTest {
        val repository = repositorioPersistente()
        val vm = ManageContentViewModel(repository)

        vm.delete("x-1")

        coVerify { repository.delete("x-1") }
    }
}
