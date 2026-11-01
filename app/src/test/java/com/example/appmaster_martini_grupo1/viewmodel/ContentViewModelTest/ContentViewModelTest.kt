package com.example.appmaster_martini_grupo1.viewmodel.ContentViewModelTest

import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.viewmodel.ContentViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentViewModelTest {

    private val viewModel = ContentViewModel()

    @Test
    fun expone_lista_al_inicializar() {
        assertTrue(viewModel.content.value.isNotEmpty())
    }

    @Test
    fun busca_contenido_por_id() {
        val id = viewModel.content.value.first().id
        assertEquals(id, viewModel.getContentById(id)?.id)
    }

    @Test
    fun id_inexistente_retorna_null() {
        assertEquals(null, viewModel.getContentById("no-existe"))
    }

    @Test
    fun expone_recetas_videos_y_cursos() {
        val tipos = viewModel.content.value.map { it::class.simpleName }
        assertTrue(tipos.contains("Recipe"))
        assertTrue(viewModel.content.value.any { it is Recipe })
    }
}