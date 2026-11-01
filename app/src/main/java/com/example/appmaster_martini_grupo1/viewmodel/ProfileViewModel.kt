package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.local.AppDatabase
import com.example.appmaster_martini_grupo1.data.local.ProfileDao
import com.example.appmaster_martini_grupo1.data.model.Profile
import com.example.appmaster_martini_grupo1.domain.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Estado de la pantalla de perfil, con un error por campo y confirmaciones. */
data class ProfileUiState(
    val userId: Long = 0L,
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
    val photoUri: String? = null,              // foto de perfil (recurso nativo: galería)
    val selectedInterests: Set<String> = emptySet(),
    val displayNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val isSaving: Boolean = false,
    val savedMessage: String? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val profileDao: ProfileDao? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun onDisplayNameChange(value: String) =
        _uiState.update { it.copy(displayName = value, displayNameError = null, savedMessage = null) }

    fun onEmailChange(value: String) =
        _uiState.update { it.copy(email = value, emailError = null, savedMessage = null) }

    fun onPhoneChange(value: String) =
        _uiState.update { it.copy(phone = value, phoneError = null, savedMessage = null) }

    fun onPhotoPicked(uri: String) =
        _uiState.update { it.copy(photoUri = uri, savedMessage = null) }

    fun onPhotoRemoved() =
        _uiState.update { it.copy(photoUri = null, savedMessage = null) }

    fun toggleInterest(id: String) = _uiState.update { state ->
        val nuevos = if (id in state.selectedInterests) {
            state.selectedInterests - id
        } else {
            state.selectedInterests + id
        }
        state.copy(selectedInterests = nuevos, savedMessage = null)
    }

    fun loadProfile(userId: Long) {
        _uiState.update { it.copy(userId = userId) }
        val dao = profileDao ?: return
        viewModelScope.launch {
            val p = dao.getByUserId(userId) ?: return@launch
            _uiState.update {
                it.copy(
                    displayName = p.displayName,
                    email = p.email,
                    phone = p.phone,
                    photoUri = p.photoUri,
                    selectedInterests = parseInterests(p.interestsCsv)
                )
            }
        }
    }

    private fun validate(): Boolean {
        val s = _uiState.value
        val nameError = if (s.displayName.trim().length < 2) "Ingresa tu nombre" else null
        val emailError = if (!Validators.isValidEmail(s.email)) "Ingresa un correo válido" else null
        val phoneError = if (s.phone.isNotBlank() && !Validators.isValidPhone(s.phone)) "Teléfono inválido" else null
        _uiState.update { it.copy(displayNameError = nameError, emailError = emailError, phoneError = phoneError) }
        return listOf(nameError, emailError, phoneError).all { it == null }
    }

    fun saveProfile() {
        if (!validate()) return
        val dao = profileDao
        if (dao == null) {
            _uiState.update { it.copy(error = "No se pudo conectar con el almacenamiento local. Intenta nuevamente.") }
            return
        }
        _uiState.update { it.copy(isSaving = true, savedMessage = null, error = null) }
        viewModelScope.launch {
            try {
                val s = _uiState.value
                dao.upsert(
                    Profile(
                        userId = s.userId,
                        displayName = s.displayName.trim(),
                        email = s.email.trim(),
                        phone = s.phone.trim(),
                        photoUri = s.photoUri,
                        interestsCsv = s.selectedInterests.joinToString(",")
                    )
                )
                _uiState.update { it.copy(isSaving = false, savedMessage = "Perfil guardado correctamente") }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "No se pudo guardar el perfil. Intenta nuevamente.") }
            }
        }
    }

    private fun parseInterests(csv: String): Set<String> =
        csv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()

    companion object {
        /** Inyecta el DAO real desde AppDatabase (Room). */
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                ProfileViewModel(profileDao = db.profileDao())
            }
        }
    }
}