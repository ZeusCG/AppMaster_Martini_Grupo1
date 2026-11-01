package com.example.appmaster_martini_grupo1.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentRepositoryTest {

    private val repository = ContentRepository()

    @Test
    fun entrega_lista_no_vacia() {
        assertTrue(repository.getContent().isNotEmpty())
    }

    @Test
    fun entrega_al_menos_una_receta_video_y_curso() {
        val categorias = repository.getContent().map { it.category }.toSet()
        assertTrue(categorias.contains("Chocolatería"))
        assertTrue(categorias.contains("Videos técnicos"))
        assertTrue(categorias.contains("Cursos"))
    }

    @Test
    fun busca_por_id_existente() {
        val primero = repository.getContent().first()
        assertEquals(primero, repository.getContentById(primero.id))
    }

    @Test
    fun id_inexistente_retorna_null() {
        assertEquals(null, repository.getContentById("no-existe"))
    }

    @Test
    fun todos_tienen_id_unico() {
        val ids = repository.getContent().map { it.id }
        assertEquals(ids.size, ids.toSet().size)
        assertNotNull(repository.getContentById(ids.first()))
    }
}