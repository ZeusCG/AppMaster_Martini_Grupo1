package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

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

class ContactoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ContactoUiState())
    val uiState: StateFlow<ContactoUiState> = _uiState.asStateFlow()

    fun onSubjectChange(value: String) {
        _uiState.update { it.copy(form = it.form.copy(subject = value, subjectError = null)) }
    }

    fun onMessageChange(value: String) {
        _uiState.update { it.copy(form = it.form.copy(message = value, messageError = null)) }
    }

    fun send() {
    }

    fun deleteRequest(id: Long) {
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer { ContactoViewModel() }
        }
    }
}
