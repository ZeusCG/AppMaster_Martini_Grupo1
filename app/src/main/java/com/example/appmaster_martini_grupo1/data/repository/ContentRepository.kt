package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ContentDao
import com.example.appmaster_martini_grupo1.data.local.ContentSeed
import com.example.appmaster_martini_grupo1.data.local.toContent
import com.example.appmaster_martini_grupo1.data.local.toEntity
import com.example.appmaster_martini_grupo1.data.model.Content
import androidx.room3.RoomDatabase
import androidx.room3.withWriteTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class ContentRepository(
    private val contentDao: ContentDao? = null,
    private val favoriteRepository: FavoriteRepository? = null,
    private val database: RoomDatabase? = null
) {

    val isPersistent: Boolean get() = contentDao != null

    fun seed(): List<Content> = ContentSeed.items

    fun observeAll(): Flow<List<Content>> =
        contentDao?.observeAll()?.map { entities -> entities.map { it.toContent() } }
            ?: flowOf(ContentSeed.items)

    suspend fun getById(id: String): Content? =
        contentDao?.getById(id)?.toContent()
            ?: ContentSeed.items.firstOrNull { it.id == id }

    suspend fun upsert(content: Content) {
        contentDao?.upsert(content.toEntity())
    }

    suspend fun delete(id: String) {
        val dao = contentDao ?: return
        val favorites = favoriteRepository
        val db = database
        if (db != null && favorites != null) {
            db.withWriteTransaction {
                dao.deleteById(id)
                favorites.removeByContentId(id)
            }
        } else {
            dao.deleteById(id)
            favorites?.removeByContentId(id)
        }
    }
}
