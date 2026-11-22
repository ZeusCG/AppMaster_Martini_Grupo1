package com.example.appmaster_martini_grupo1.data.model

/** Resultado del clima: el dato y si viene del último guardado (sin internet). */
data class ClimaResultado(
    val clima: ClimaActual,
    val desdeCache: Boolean
)
