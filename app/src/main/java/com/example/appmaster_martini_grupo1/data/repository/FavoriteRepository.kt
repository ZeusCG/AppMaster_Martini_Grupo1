package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.FavoriteDao
import com.example.appmaster_martini_grupo1.data.model.Favorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class FavoriteRepository(
    private val favoriteDao: FavoriteDao? = null
) {

    fun observeFavoriteIds(): Flow<List<String>> =
        favoriteDao?.observeFavoriteIds() ?: emptyFlow()

    suspend fun getFavoriteIds(): List<String> =
        favoriteDao?.getFavoriteIds() ?: emptyList()

    suspend fun add(contentId: String) {
        favoriteDao?.insert(Favorite(contentId = contentId))
    }

    suspend fun remove(contentId: String) {
        favoriteDao?.deleteById(contentId)
    }
}