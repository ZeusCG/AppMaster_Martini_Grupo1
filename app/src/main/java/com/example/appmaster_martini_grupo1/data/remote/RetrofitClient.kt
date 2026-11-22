package com.example.appmaster_martini_grupo1.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** Construye los clientes HTTP (Retrofit + Gson + logs). Sin inyección de dependencias, para que sea explicable. */
object RetrofitClient {

    private const val BASE_URL_CLIMA = "https://api.open-meteo.com/"
    private const val BASE_URL_CONTACTO = "https://jsonplaceholder.typicode.com/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private fun retrofit(baseUrl: String): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val climaApi: ClimaApi = retrofit(BASE_URL_CLIMA).create(ClimaApi::class.java)
    val contactoApi: ContactoApi = retrofit(BASE_URL_CONTACTO).create(ContactoApi::class.java)
}
