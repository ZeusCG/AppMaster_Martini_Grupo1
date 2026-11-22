package com.example.appmaster_martini_grupo1.data.remote

import com.example.appmaster_martini_grupo1.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Construye los clientes HTTP (Retrofit + Gson + logs). Sin inyección de dependencias, para que sea explicable. */
object RetrofitClient {

    private const val BASE_URL_CLIMA = "https://api.open-meteo.com/"
    private const val BASE_URL_CONTACTO = "https://jsonplaceholder.typicode.com/"

    private val logging = HttpLoggingInterceptor().apply {
        // H-020: logs solo en debug; en release no se dejan trazas de URLs/estados.
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        // H-019: timeouts explícitos (antes se usaban los valores por defecto de OkHttp).
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .callTimeout(20, TimeUnit.SECONDS)
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
