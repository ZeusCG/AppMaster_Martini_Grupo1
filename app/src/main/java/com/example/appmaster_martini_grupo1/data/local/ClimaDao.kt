package com.example.appmaster_martini_grupo1.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query

@Dao
interface ClimaDao {

    @Query("SELECT * FROM clima_cache WHERE id = 1 LIMIT 1")
    suspend fun get(): ClimaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(clima: ClimaEntity)
}
