package com.example.appmaster_martini_grupo1.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface ClimaApi {

    /** Clima actual del taller (Santiago). Coordenadas fijas, sin permiso de ubicación. */
    @GET("v1/forecast?current=temperature_2m,relative_humidity_2m")
    suspend fun getClima(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): ClimaResponse
}
