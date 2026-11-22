package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.ClimaDao
import com.example.appmaster_martini_grupo1.data.local.ClimaEntity
import com.example.appmaster_martini_grupo1.data.model.ClimaActual
import com.example.appmaster_martini_grupo1.data.model.ClimaResultado
import com.example.appmaster_martini_grupo1.data.remote.ClimaApi

class ClimaRepository(
    private val climaApi: ClimaApi? = null,
    private val climaDao: ClimaDao? = null
) {

    /** Consulta la red, guarda el resultado como último clima y lo devuelve. Lanza si falla. */
    suspend fun getClima(): ClimaActual {
        val api = climaApi ?: error("ClimaApi no disponible")
        val respuesta = api.getClima(LATITUD, LONGITUD)
        val actual = respuesta.current ?: error("Respuesta de clima vacía")
        val temperatura = actual.temperature ?: error("Sin temperatura")
        val humedad = actual.humidity ?: error("Sin humedad")
        val clima = ClimaActual(temperatura = temperatura, humedad = humedad, hora = actual.time.orEmpty())
        climaDao?.save(
            ClimaEntity(temperatura = clima.temperatura, humedad = clima.humedad, hora = clima.hora)
        )
        return clima
    }

    /** Último clima guardado (puede ser null si nunca se consultó). */
    suspend fun getUltimoClima(): ClimaActual? =
        climaDao?.get()?.let {
            ClimaActual(temperatura = it.temperatura, humedad = it.humedad, hora = it.hora)
        }

    /** Intenta la red; si falla, cae al último clima guardado (marcado como cache). */
    suspend fun obtenerClima(): ClimaResultado = try {
        ClimaResultado(getClima(), desdeCache = false)
    } catch (e: Exception) {
        val ultimo = getUltimoClima() ?: throw e
        ClimaResultado(ultimo, desdeCache = true)
    }

    companion object {
        // Coordenadas fijas de Santiago (no se pide permiso de ubicación).
        const val LATITUD = -33.45
        const val LONGITUD = -70.66
    }
}
