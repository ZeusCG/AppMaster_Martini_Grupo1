package com.example.appmaster_martini_grupo1.data.remote

import com.google.gson.annotations.SerializedName

/** Respuesta de Open-Meteo para el endpoint de clima actual. */
data class ClimaResponse(
    @SerializedName("current") val current: ClimaCurrent?
)

data class ClimaCurrent(
    @SerializedName("time") val time: String?,
    @SerializedName("temperature_2m") val temperature: Double?,
    @SerializedName("relative_humidity_2m") val humidity: Int?
)
