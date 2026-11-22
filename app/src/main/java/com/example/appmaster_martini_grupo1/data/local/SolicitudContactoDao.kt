package com.example.appmaster_martini_grupo1.data.local

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Update
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import kotlinx.coroutines.flow.Flow

@Dao
interface SolicitudContactoDao {

    @Query("SELECT * FROM solicitudes_contacto WHERE userId = :userId ORDER BY fecha DESC")
    fun observeByUser(userId: Long): Flow<List<SolicitudContacto>>

    @Query("SELECT * FROM solicitudes_contacto WHERE userId = :userId ORDER BY fecha DESC")
    suspend fun getByUser(userId: Long): List<SolicitudContacto>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(solicitud: SolicitudContacto): Long

    @Update
    suspend fun update(solicitud: SolicitudContacto)

    @Query("DELETE FROM solicitudes_contacto WHERE id = :id")
    suspend fun deleteById(id: Long)
}
