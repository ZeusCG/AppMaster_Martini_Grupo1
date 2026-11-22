package com.example.appmaster_martini_grupo1.data.model

import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "solicitudes_contacto")
data class SolicitudContacto(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long,
    val asunto: String,
    val mensaje: String,
    val estado: String = ESTADO_PENDIENTE,
    val fecha: Long = System.currentTimeMillis()
) {
    companion object {
        const val ESTADO_PENDIENTE = "pendiente"
        const val ESTADO_ENVIADA = "enviada"
    }
}
