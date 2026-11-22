package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.local.AppDatabase
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import com.example.appmaster_martini_grupo1.data.remote.RetrofitClient
import com.example.appmaster_martini_grupo1.data.repository.ContactoRepository
import com.example.appmaster_martini_grupo1.data.session.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ContactoForm(
    val subject: String = "",
    val message: String = "",
    val subjectError: String? = null,
    val messageError: String? = null
)

sealed interface SendState {
    data object Idle : SendState
    data object Sending : SendState
    data object Sent : SendState
    data class Error(val mensaje: String) : SendState
}

data class ContactoUiState(
    val form: ContactoForm = ContactoForm(),
    val sendState: SendState = SendState.Idle,
    val history: List<SolicitudContacto> = emptyList()
)

class ContactoViewModel(
    private val repository: ContactoRepository = ContactoRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactoUiState())
    val uiState: StateFlow<ContactoUiState> = _uiState.asStateFlow()

    init {
        observeHistory()
    }

    private fun observeHistory() {
        if (!repository.isPersistent) return
        val userId = UserSession.userId.value ?: return
        viewModelScope.launch {
            repository.observeHistory(userId).collect { list ->
                _uiState.update { it.copy(history = list) }
            }
        }
    }

    fun onSubjectChange(value: String) {
        _uiState.update { it.copy(form = it.form.copy(subject = value, subjectError = null), sendState = SendState.Idle) }
    }

    fun onMessageChange(value: String) {
        _uiState.update { it.copy(form = it.form.copy(message = value, messageError = null), sendState = SendState.Idle) }
    }

    fun send() {
        val validated = _uiState.value.form.validate()
        if (validated.hasErrors()) {
            _uiState.update { it.copy(form = validated) }
            return
        }
        val userId = UserSession.userId.value
        if (userId == null) {
            _uiState.update { it.copy(sendState = SendState.Error("Inicia sesión para enviar la solicitud.")) }
            return
        }
        _uiState.update { it.copy(form = validated, sendState = SendState.Sending) }
        viewModelScope.launch {
            val resultado = repository.send(userId, validated.subject.trim(), validated.message.trim())
            _uiState.update {
                if (resultado.estado == SolicitudContacto.ESTADO_ENVIADA) {
                    it.copy(form = ContactoForm(), sendState = SendState.Sent)
                } else {
                    it.copy(sendState = SendState.Error("No se pudo enviar. La solicitud quedó pendiente en el historial."))
                }
            }
        }
    }

    fun deleteRequest(id: Long) {
        _uiState.update { it.copy(history = it.history.filterNot { solicitud -> solicitud.id == id }) }
        if (repository.isPersistent) {
            viewModelScope.launch {
                // H-035: un fallo al borrar no debe cerrar la app.
                try {
                    repository.delete(id)
                } catch (_: Exception) {
                    // El historial se recargará al reabrir la pantalla.
                }
            }
        }
    }

    companion object {
        const val MAX_SUBJECT = 60
        const val MAX_MESSAGE = 500

        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                ContactoViewModel(
                    ContactoRepository(
                        contactoApi = RetrofitClient.contactoApi,
                        solicitudDao = db.solicitudContactoDao()
                    )
                )
            }
        }
    }
}

private fun ContactoForm.hasErrors(): Boolean = subjectError != null || messageError != null

private fun ContactoForm.validate(): ContactoForm {
    val subjectError = when {
        subject.isBlank() -> "Ingresa el asunto"
        subject.trim().length > ContactoViewModel.MAX_SUBJECT ->
            "Máximo ${ContactoViewModel.MAX_SUBJECT} caracteres"
        else -> null
    }
    val messageError = when {
        message.isBlank() -> "Ingresa el mensaje"
        message.trim().length > ContactoViewModel.MAX_MESSAGE ->
            "Máximo ${ContactoViewModel.MAX_MESSAGE} caracteres"
        else -> null
    }
    return copy(subjectError = subjectError, messageError = messageError)
}
