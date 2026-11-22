package com.example.appmaster_martini_grupo1.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/** Cuerpo que espera JSONPlaceholder para "crear" un post. */
data class ContactoRequest(
    @SerializedName("title") val title: String,
    @SerializedName("body") val body: String,
    @SerializedName("userId") val userId: Long
)

data class ContactoResponse(
    @SerializedName("id") val id: Long?,
    @SerializedName("title") val title: String?,
    @SerializedName("body") val body: String?,
    @SerializedName("userId") val userId: Long?
)

interface ContactoApi {

    /** Envía la solicitud de contacto. Responde 201 Created (y no persiste nada en el servidor). */
    @POST("posts")
    suspend fun enviar(@Body request: ContactoRequest): Response<ContactoResponse>
}
