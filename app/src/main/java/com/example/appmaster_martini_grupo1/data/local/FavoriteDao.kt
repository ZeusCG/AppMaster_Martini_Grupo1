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

    @Query("DELETE FROM favorites WHERE contentId = :contentId")
    suspend fun deleteById(contentId: String)

    @Query("SELECT contentId FROM favorites")
    fun observeFavoriteIds(): Flow<List<String>>

    @Query("SELECT contentId FROM favorites")
    suspend fun getFavoriteIds(): List<String>
}