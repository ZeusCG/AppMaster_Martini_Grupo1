package com.example.appmaster_martini_grupo1.data.repository

import com.example.appmaster_martini_grupo1.data.local.SolicitudContactoDao
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import com.example.appmaster_martini_grupo1.data.remote.ContactoApi
import com.example.appmaster_martini_grupo1.data.remote.ContactoRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ContactoRepository(
    private val contactoApi: ContactoApi? = null,
    private val solicitudDao: SolicitudContactoDao? = null
) {

    /** true cuando hay BD Room detrás (en la app); false en tests sin DAO. */
    val isPersistent: Boolean get() = solicitudDao != null

    fun observeHistory(userId: Long): Flow<List<SolicitudContacto>> =
        solicitudDao?.observeByUser(userId) ?: flowOf(emptyList())

    suspend fun getHistory(userId: Long): List<SolicitudContacto> =
        solicitudDao?.getByUser(userId) ?: emptyList()

    /**
     * Guarda la solicitud en Room como "pendiente" y luego la envía por POST.
     * Si el POST responde OK, la marca "enviada"; si falla, queda "pendiente"
     * (no se pierde: el historial la conserva).
     */
    suspend fun send(userId: Long, asunto: String, mensaje: String): SolicitudContacto {
        val pendiente = SolicitudContacto(userId = userId, asunto = asunto, mensaje = mensaje)
        val id = solicitudDao?.insert(pendiente) ?: 0L
        val guardada = pendiente.copy(id = id)

        val api = contactoApi ?: return guardada
        return try {
            val response = api.enviar(ContactoRequest(title = asunto, body = mensaje, userId = userId))
            if (response.isSuccessful) {
                val enviada = guardada.copy(estado = SolicitudContacto.ESTADO_ENVIADA)
                solicitudDao?.update(enviada)
                enviada
            } else {
                guardada
            }
        } catch (_: Exception) {
            guardada
        }
    }

    suspend fun delete(id: Long) {
        solicitudDao?.deleteById(id)
    }
}
