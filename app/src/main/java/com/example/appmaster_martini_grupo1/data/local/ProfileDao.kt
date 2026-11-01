package com.example.appmaster_martini_grupo1.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.example.appmaster_martini_grupo1.data.model.Profile
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(profile: Profile)

    @Update
    suspend fun update(profile: Profile)

    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: Long): Profile?

    @Query("SELECT * FROM profiles WHERE userId = :userId LIMIT 1")
    fun observeByUserId(userId: Long): Flow<Profile?>
}