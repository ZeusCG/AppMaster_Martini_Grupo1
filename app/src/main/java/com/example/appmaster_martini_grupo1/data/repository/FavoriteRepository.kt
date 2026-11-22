package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.FavoriteDao
import com.example.appmaster_martini_grupo1.data.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FavoriteRepository(
    private val favoriteDao: FavoriteDao? = null
) {

    fun observeFavoriteIds(userId: Long): Flow<List<String>> =
        favoriteDao?.observeFavoriteIds(userId) ?: emptyFlow()

    suspend fun getFavoriteIds(userId: Long): List<String> =
        favoriteDao?.getFavoriteIds(userId) ?: emptyList()

    suspend fun add(userId: Long, contentId: String) {
        favoriteDao?.insert(Favorite(userId = userId, contentId = contentId))
    }

    suspend fun remove(userId: Long, contentId: String) {
        favoriteDao?.deleteById(userId, contentId)
    }

    suspend fun removeByContentId(contentId: String) {
        favoriteDao?.deleteByContentId(contentId)
    }
}