package com.example.appmaster_martini_grupo1.data.session

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow

object UserSession {
    private const val PREFS = "appmaster_session"
    private const val KEY_USER_ID = "user_id"

    private var prefs: SharedPreferences? = null

    val userId = MutableStateFlow<Long?>(null)

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
