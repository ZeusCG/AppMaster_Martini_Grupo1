package com.example.appmaster_martini_grupo1.data.session

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Sesión del usuario.
 * H-015: se persiste el id en SharedPreferences para que sobreviva al cierre del proceso
 * (antes vivía solo en memoria y había que volver a iniciar sesión al reabrir la app).
 */
object UserSession {
    private const val PREFS = "appmaster_session"
    private const val KEY_USER_ID = "user_id"

    private var prefs: SharedPreferences? = null

    val userId = MutableStateFlow<Long?>(null)

    /** Se llama al arrancar la app; sin esto la sesión queda solo en memoria (p. ej. en tests). */
    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val guardado = prefs?.getLong(KEY_USER_ID, -1L) ?: -1L
        userId.value = guardado.takeIf { it > 0 }
    }

    fun set(id: Long) {
        userId.value = id
        prefs?.edit()?.putLong(KEY_USER_ID, id)?.apply()
    }

    fun clear() {
        userId.value = null
        prefs?.edit()?.remove(KEY_USER_ID)?.apply()
    }
}
