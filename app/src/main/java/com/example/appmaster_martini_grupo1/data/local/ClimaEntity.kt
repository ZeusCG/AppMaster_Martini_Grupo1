package com.example.appmaster_martini_grupo1.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/** Último clima obtenido, guardado para poder mostrarlo sin internet. Una sola fila (id = 1). */
@Entity(tableName = "clima_cache")
data class ClimaEntity(
    @PrimaryKey val id: Int = 1,
    val temperatura: Double,
    val humedad: Int,
    val hora: String,
    val fecha: Long = System.currentTimeMillis()
)
