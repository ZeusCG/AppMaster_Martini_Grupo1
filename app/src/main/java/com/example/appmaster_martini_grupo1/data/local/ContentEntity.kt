package com.example.appmaster_martini_grupo1.data.local

import androidx.room3.Entity
import androidx.room3.PrimaryKey

/**
 * Contenido del catálogo (recetas, videos y cursos) en una sola tabla.
 * El campo [type] indica el subtipo y las listas se guardan como texto (un ítem por línea).
 */
@Entity(tableName = "contents")
data class ContentEntity(
    @PrimaryKey val id: String,
    val type: String,
    val title: String,
    val description: String,
    val category: String,
    val portions: Int? = null,
    val ingredients: String? = null,
    val steps: String? = null,
    val durationMinutes: Int? = null,
    val url: String? = null,
    val durationHours: Int? = null,
    val level: String? = null,
    val lessons: String? = null
)
