package com.example.appmaster_martini_grupo1.viewmodel.ContentViewModelTest

import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.viewmodel.ContentViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun buscar_por_texto_reduce_la_lista() {
        val total = viewModel.content.value.size
        viewModel.onQueryChange("ganache")

        val resultado = viewModel.content.value
        assertEquals(1, resultado.size)
        assertEquals("recipe-001", resultado.first().id)
        assertTrue(resultado.size < total)
    }

    @Test
    fun filtro_por_categoria_muestra_solo_esa_categoria() {
        viewModel.onCategorySelected("Cursos")

        val resultado = viewModel.content.value
        assertTrue(resultado.isNotEmpty())
        assertTrue(resultado.all { it.category == "Cursos" })
    }

    @Test
    fun busqueda_sin_resultados_deja_lista_vacia() {
        viewModel.onQueryChange("zzzz-no-existe")

        assertTrue(viewModel.content.value.isEmpty())
    }

    @Test
    fun limpiar_filtros_vuelve_a_la_lista_completa() {
        val total = viewModel.content.value.size
        viewModel.onCategorySelected("Cursos")
        viewModel.onQueryChange("ganache")

        viewModel.onCategorySelected(null)
        viewModel.onQueryChange("")

        assertEquals(total, viewModel.content.value.size)
    }

    @Test
    fun categorias_no_tienen_duplicados_ni_vacios() {
        val categorias = viewModel.categories
        assertFalse(categorias.isEmpty())
        assertEquals(categorias.size, categorias.toSet().size)
    }

    @Test
    fun favoritos_inician_vacios() {
        assertTrue(viewModel.favoriteIds.value.isEmpty())
        assertFalse(viewModel.isFavorite("recipe-001"))
    }
}