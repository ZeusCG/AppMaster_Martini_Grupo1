package com.example.appmaster_martini_grupo1.viewmodel

import com.example.appmaster_martini_grupo1.MainDispatcherRule
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Course
import com.example.appmaster_martini_grupo1.data.model.Video
import com.example.appmaster_martini_grupo1.data.repository.ContentRepository
import io.mockk.coEvery
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

    @Test
    fun onFieldChange_actualiza_todos_los_campos() {
        viewModel.startCreate()
        viewModel.onTypeChange(ContentType.RECIPE)
        viewModel.onFieldChange(ContentField.TITLE, "T")
        viewModel.onFieldChange(ContentField.CATEGORY, "C")
        viewModel.onFieldChange(ContentField.DESCRIPTION, "D")
        viewModel.onFieldChange(ContentField.PORTIONS, "4")
        viewModel.onFieldChange(ContentField.INGREDIENTS, "azúcar\nharina")
        viewModel.onFieldChange(ContentField.STEPS, "mezclar\nhornear")
        viewModel.onFieldChange(ContentField.DURATION_MINUTES, "10")
        viewModel.onFieldChange(ContentField.URL, "http://x")
        viewModel.onFieldChange(ContentField.DURATION_HOURS, "2")
        viewModel.onFieldChange(ContentField.LEVEL, "Básico")
        viewModel.onFieldChange(ContentField.LESSONS, "uno\ndos")

        val form = viewModel.uiState.value.form
        assertEquals("T", form.title)
        assertEquals("azúcar\nharina", form.ingredients)
        assertEquals("mezclar\nhornear", form.steps)
        assertEquals("10", form.durationMinutes)
        assertEquals("http://x", form.url)
        assertEquals("2", form.durationHours)
        assertEquals("Básico", form.level)
        assertEquals("uno\ndos", form.lessons)
    }

    @Test
    fun descripcion_demasiado_larga_marca_error() {
        val total = viewModel.uiState.value.items.size
        viewModel.startCreate()
        viewModel.onFieldChange(ContentField.TITLE, "Torta")
        viewModel.onFieldChange(ContentField.CATEGORY, "Postres")
        viewModel.onFieldChange(ContentField.DESCRIPTION, "x".repeat(ManageContentViewModel.MAX_DESCRIPTION + 1))

        viewModel.save()

        assertNotNull(viewModel.uiState.value.form.descriptionError)
        assertEquals(total, viewModel.uiState.value.items.size)
    }

    @Test
    fun porciones_invalidas_marcan_error() {
        viewModel.startCreate()
        viewModel.onTypeChange(ContentType.RECIPE)
        viewModel.onFieldChange(ContentField.TITLE, "Torta")
        viewModel.onFieldChange(ContentField.CATEGORY, "Postres")
        viewModel.onFieldChange(ContentField.PORTIONS, "cero")

        viewModel.save()

        assertNotNull(viewModel.uiState.value.form.portionsError)
    }

    @Test
    fun duracion_invalida_de_video_marca_error() {
        viewModel.startCreate()
        viewModel.onTypeChange(ContentType.VIDEO)
        viewModel.onFieldChange(ContentField.TITLE, "Video")
        viewModel.onFieldChange(ContentField.CATEGORY, "Técnicas")
        viewModel.onFieldChange(ContentField.DURATION_MINUTES, "abc")

        viewModel.save()

        assertNotNull(viewModel.uiState.value.form.durationError)
    }

    @Test
    fun enlace_invalido_de_video_marca_error() {
        viewModel.startCreate()
        viewModel.onTypeChange(ContentType.VIDEO)
        viewModel.onFieldChange(ContentField.TITLE, "Video")
        viewModel.onFieldChange(ContentField.CATEGORY, "Técnicas")
        viewModel.onFieldChange(ContentField.URL, "ftp://no")

        viewModel.save()

        assertNotNull(viewModel.uiState.value.form.urlError)
    }

    @Test
    fun crear_video_guarda_sus_campos() {
        val total = viewModel.uiState.value.items.size
        viewModel.startCreate()
        viewModel.onTypeChange(ContentType.VIDEO)
        viewModel.onFieldChange(ContentField.TITLE, "Templado del chocolate")
        viewModel.onFieldChange(ContentField.CATEGORY, "Técnicas")
        viewModel.onFieldChange(ContentField.DURATION_MINUTES, "12")
        viewModel.onFieldChange(ContentField.URL, "https://video.test/templado")

        viewModel.save()

        val creado = viewModel.uiState.value.items.first { it.title == "Templado del chocolate" } as Video
        assertEquals(12, creado.durationMinutes)
        assertEquals("https://video.test/templado", creado.url)
        assertEquals(total + 1, viewModel.uiState.value.items.size)
    }

    @Test
    fun crear_curso_guarda_sus_campos() {
        viewModel.startCreate()
        viewModel.onTypeChange(ContentType.COURSE)
        viewModel.onFieldChange(ContentField.TITLE, "Curso de bombones")
        viewModel.onFieldChange(ContentField.CATEGORY, "Cursos")
        viewModel.onFieldChange(ContentField.DURATION_HOURS, "8")
        viewModel.onFieldChange(ContentField.LEVEL, "Intermedio")
        viewModel.onFieldChange(ContentField.LESSONS, "Intro\nMoldeo")

        viewModel.save()

        val creado = viewModel.uiState.value.items.first { it.title == "Curso de bombones" } as Course
        assertEquals(8, creado.durationHours)
        assertEquals("Intermedio", creado.level)
        assertEquals(listOf("Intro", "Moldeo"), creado.lessons)
    }

    @Test
    fun startEdit_de_un_video_carga_su_tipo_y_campos() {
        val video = viewModel.uiState.value.items.first { it is Video } as Video

        viewModel.startEdit(video.id)

        val form = viewModel.uiState.value.form
        assertEquals(ContentType.VIDEO, form.type)
        assertEquals(video.durationMinutes.toString(), form.durationMinutes)
        assertEquals(video.url, form.url)
    }

    @Test
    fun startEdit_de_un_curso_carga_su_tipo_y_campos() {
        val curso = viewModel.uiState.value.items.first { it is Course } as Course

        viewModel.startEdit(curso.id)

        val form = viewModel.uiState.value.form
        assertEquals(ContentType.COURSE, form.type)
        assertEquals(curso.level, form.level)
        assertEquals(curso.lessons.joinToString("\n"), form.lessons)
    }

    @Test
    fun startEdit_con_id_inexistente_no_cambia_el_formulario() {
        viewModel.startCreate()

        viewModel.startEdit("no-existe")

        assertNull(viewModel.uiState.value.form.id)
    }

    @Test
    fun save_que_falla_en_el_repositorio_avisa() = runTest {
        val repository = mockk<ContentRepository>(relaxed = true)
        every { repository.isPersistent } returns true
        every { repository.seed() } returns emptyList()
        every { repository.observeAll() } returns flowOf(emptyList<Content>())
        coEvery { repository.upsert(any()) } throws RuntimeException("falló")
        val vm = ManageContentViewModel(repository)
        vm.startCreate()
        vm.onFieldChange(ContentField.TITLE, "Torta")
        vm.onFieldChange(ContentField.CATEGORY, "Postres")

        vm.save()

        assertTrue(vm.uiState.value.message!!.contains("No se pudo guardar"))
    }

    @Test
    fun delete_que_falla_en_el_repositorio_avisa() = runTest {
        val repository = mockk<ContentRepository>(relaxed = true)
        every { repository.isPersistent } returns true
        every { repository.seed() } returns emptyList()
        every { repository.observeAll() } returns flowOf(emptyList<Content>())
        coEvery { repository.delete(any()) } throws RuntimeException("falló")
        val vm = ManageContentViewModel(repository)

        vm.delete("x-1")

        assertTrue(vm.uiState.value.message!!.contains("No se pudo eliminar"))
    }
}
