package com.example.appmaster_martini_grupo1.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import com.example.appmaster_martini_grupo1.data.model.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Query("DELETE FROM favorites WHERE userId = :userId AND contentId = :contentId")
    suspend fun deleteById(userId: Long, contentId: String)

    @Query("DELETE FROM favorites WHERE contentId = :contentId")
    suspend fun deleteByContentId(contentId: String)

    @Query("SELECT contentId FROM favorites WHERE userId = :userId")
    fun observeFavoriteIds(userId: Long): Flow<List<String>>

    @Query("SELECT contentId FROM favorites WHERE userId = :userId")
    suspend fun getFavoriteIds(userId: Long): List<String>
}