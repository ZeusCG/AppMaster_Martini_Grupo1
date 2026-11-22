package com.example.appmaster_martini_grupo1.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {

    @Query("SELECT * FROM contents ORDER BY title ASC")
    fun observeAll(): Flow<List<ContentEntity>>

    @Query("SELECT * FROM contents ORDER BY title ASC")
    suspend fun getAll(): List<ContentEntity>

    @Query("SELECT * FROM contents WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): ContentEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ContentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ContentEntity>)

    @Query("DELETE FROM contents WHERE id = :id")
    suspend fun deleteById(id: String)
}
