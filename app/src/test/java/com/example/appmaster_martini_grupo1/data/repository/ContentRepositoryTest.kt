package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ContentDao
import com.example.appmaster_martini_grupo1.data.local.ContentEntity
import com.example.appmaster_martini_grupo1.data.local.FavoriteDao
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Favorite
import com.example.appmaster_martini_grupo1.data.model.Recipe
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentRepositoryTest {

    private class FakeContentDao : ContentDao {
        private val items = MutableStateFlow<List<ContentEntity>>(emptyList())

        override fun observeAll(): Flow<List<ContentEntity>> = items
        override suspend fun getAll(): List<ContentEntity> = items.value
        override suspend fun getById(id: String): ContentEntity? =
            items.value.firstOrNull { it.id == id }

        override suspend fun upsert(item: ContentEntity) {
            items.value = items.value.filterNot { it.id == item.id } + item
        }

        override suspend fun insertAll(items: List<ContentEntity>) {
            this.items.value = this.items.value + items
        }

        override suspend fun deleteById(id: String) {
            items.value = items.value.filterNot { it.id == id }
        }
    }

    private fun receta(id: String) = Recipe(
        id = id,
        title = "Receta $id",
        description = "Descripción $id",
        category = "Chocolatería",
        portions = 4,
        ingredients = listOf("Chocolate"),
        steps = listOf("Derretir")
    )

    private class FakeFavoriteDao : FavoriteDao {
        private val items = MutableStateFlow<List<Favorite>>(emptyList())

        override suspend fun insert(favorite: Favorite) {
            if (items.value.none { it.userId == favorite.userId && it.contentId == favorite.contentId }) {
                items.value = items.value + favorite
            }
        }

        override suspend fun deleteById(userId: Long, contentId: String) {
            items.value = items.value.filterNot { it.userId == userId && it.contentId == contentId }
        }

        override suspend fun deleteByContentId(contentId: String) {
            items.value = items.value.filterNot { it.contentId == contentId }
        }

        override fun observeFavoriteIds(userId: Long): Flow<List<String>> =
            items.map { list -> list.filter { it.userId == userId }.map { it.contentId } }

        override suspend fun getFavoriteIds(userId: Long): List<String> =
            items.value.filter { it.userId == userId }.map { it.contentId }
    }

    @Test
    fun sin_dao_entrega_la_semilla() = runBlocking {
        val repository = ContentRepository()

        val items = repository.observeAll().first()

        assertTrue(items.isNotEmpty())
        assertTrue(repository.getById(items.first().id) != null)
    }

    @Test
    fun con_dao_observa_lo_que_se_guarda() = runBlocking {
        val repository = ContentRepository(FakeContentDao())

        repository.upsert(receta("r-1"))

        val items: List<Content> = repository.observeAll().first()
        assertEquals(1, items.size)
        assertEquals("r-1", items.first().id)
    }

    @Test
    fun upsert_actualiza_si_ya_existe() = runBlocking {
        val repository = ContentRepository(FakeContentDao())
        repository.upsert(receta("r-1"))

        repository.upsert(receta("r-1").copy(title = "Título nuevo"))

        val items = repository.observeAll().first()
        assertEquals(1, items.size)
        assertEquals("Título nuevo", items.first().title)
    }

    @Test
    fun elimina_por_id() = runBlocking {
        val repository = ContentRepository(FakeContentDao())
        repository.upsert(receta("r-1"))

        repository.delete("r-1")

        assertTrue(repository.observeAll().first().isEmpty())
    }

    @Test
    fun get_by_id_inexistente_retorna_null() = runBlocking {
        val repository = ContentRepository(FakeContentDao())

        assertNull(repository.getById("no-existe"))
    }

    @Test
    fun delete_delega_en_el_dao() = runBlocking {
        val dao = mockk<ContentDao>(relaxed = true)
        val repository = ContentRepository(dao)

        repository.delete("r-1")

        coVerify { dao.deleteById("r-1") }
    }

    @Test
    fun al_eliminar_contenido_se_quitan_sus_favoritos() = runBlocking {
        val favorites = FavoriteRepository(FakeFavoriteDao())
        val repository = ContentRepository(FakeContentDao(), favorites)
        repository.upsert(receta("r-1"))
        favorites.add(1L, "r-1")
        assertTrue(favorites.getFavoriteIds(1L).contains("r-1"))

        repository.delete("r-1")

        assertTrue(favorites.getFavoriteIds(1L).isEmpty())
    }

    @Test
    fun get_by_id_con_dao_mapea_la_entidad() = runBlocking {
        val repository = ContentRepository(FakeContentDao())
        repository.upsert(receta("r-9"))

        val obtenido = repository.getById("r-9")

        assertEquals("r-9", obtenido?.id)
        assertEquals("Receta r-9", obtenido?.title)
    }
}
