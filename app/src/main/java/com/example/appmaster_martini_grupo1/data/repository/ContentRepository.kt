package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ContentDao
import com.example.appmaster_martini_grupo1.data.local.ContentSeed
import com.example.appmaster_martini_grupo1.data.local.toContent
import com.example.appmaster_martini_grupo1.data.local.toEntity
import com.example.appmaster_martini_grupo1.data.model.Content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ContentRepository(
    private val contentDao: ContentDao? = null,
    private val favoriteRepository: FavoriteRepository? = null
) {

    /** true cuando hay BD Room detrás (en la app); false en tests sin DAO. */
    val isPersistent: Boolean get() = contentDao != null

    /** Contenido inicial en memoria (semilla), disponible al instante antes de que Room emita. */
    fun seed(): List<Content> = ContentSeed.items

    /** Observa el contenido guardado. Sin DAO (tests) entrega la semilla. */
    fun observeAll(): Flow<List<Content>> =
        contentDao?.observeAll()?.map { entities -> entities.map { it.toContent() } }
            ?: flowOf(ContentSeed.items)

    suspend fun getById(id: String): Content? =
        contentDao?.getById(id)?.toContent()
            ?: ContentSeed.items.firstOrNull { it.id == id }

    /** Inserta o actualiza (upsert). */
    suspend fun upsert(content: Content) {
        contentDao?.upsert(content.toEntity())
    }

    /** Elimina el contenido y, de paso, sus favoritos (para no dejar huérfanos). */
    suspend fun delete(id: String) {
        contentDao?.deleteById(id)
        favoriteRepository?.removeByContentId(id)
    }
}
