package com.example.appmaster_martini_grupo1.data.repository


import com.example.appmaster_martini_grupo1.data.local.FavoriteDao
import com.example.appmaster_martini_grupo1.data.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FavoriteRepositoryTest {

    private class FakeFavoriteDao : FavoriteDao {
        private val ids = MutableStateFlow<List<String>>(emptyList())

        override suspend fun insert(favorite: Favorite) {
            if (favorite.contentId !in ids.value) {
                ids.value = ids.value + favorite.contentId
            }
        }

        override suspend fun deleteById(userId: Long, contentId: String) {
            ids.value = ids.value - contentId
        }

        override fun observeFavoriteIds(userId: Long): Flow<List<String>> = ids

        override suspend fun getFavoriteIds(userId: Long): List<String> = ids.value
    }

    @Test
    fun agrega_y_lista_favorito() = runBlocking {
        val repository = FavoriteRepository(FakeFavoriteDao())

        repository.add(1L, "recipe-001")

        assertTrue(repository.getFavoriteIds(1L).contains("recipe-001"))
        assertTrue(repository.observeFavoriteIds(1L).first().contains("recipe-001"))
    }

    @Test
    fun quita_favorito() = runBlocking {
        val repository = FavoriteRepository(FakeFavoriteDao())
        repository.add(1L, "recipe-001")

        repository.remove(1L, "recipe-001")

        assertFalse(repository.getFavoriteIds(1L).contains("recipe-001"))
    }

    @Test
    fun sin_dao_no_falla_y_devuelve_vacio() = runBlocking {
        val repository = FavoriteRepository()

        repository.add(1L, "recipe-001")

        assertTrue(repository.getFavoriteIds(1L).isEmpty())
    }
}