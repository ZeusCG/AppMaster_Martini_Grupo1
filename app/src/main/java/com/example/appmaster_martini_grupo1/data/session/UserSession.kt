package com.example.appmaster_martini_grupo1.data.session

import kotlinx.coroutines.flow.MutableStateFlow

object UserSession {
    val userId = MutableStateFlow<Long?>(null)

    fun set(id: Long) {
        userId.value = id
    }

    fun clear() {
        userId.value = null
    }
}