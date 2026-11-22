package com.example.appmaster_martini_grupo1.data.model

/** Clima actual del taller, ya traducido del JSON a algo simple para la app. */
data class ClimaActual(
    val temperatura: Double,
    val humedad: Int,
    val hora: String
)
