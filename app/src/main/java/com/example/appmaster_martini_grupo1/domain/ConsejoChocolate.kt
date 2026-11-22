package com.example.appmaster_martini_grupo1.domain

/**
 * Regla de negocio del equipo (no es norma técnica): con humedad alta o calor,
 * el chocolate se trabaja peor. Se usa para dar un consejo al usuario.
 */
object ConsejoChocolate {

    const val AVISO = "Precaución al almacenar y trabajar chocolate"
    const val FAVORABLE = "Condiciones favorables"

    private const val HUMEDAD_ALTA = 70
    private const val TEMPERATURA_ALTA = 25.0

    fun evaluar(temperatura: Double, humedad: Int): String =
        if (humedad >= HUMEDAD_ALTA || temperatura >= TEMPERATURA_ALTA) AVISO else FAVORABLE
}
