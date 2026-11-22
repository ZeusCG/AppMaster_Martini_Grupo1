package com.example.appmaster_martini_grupo1.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

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

    @POST("posts")
    suspend fun enviar(@Body request: ContactoRequest): Response<ContactoResponse>
}
