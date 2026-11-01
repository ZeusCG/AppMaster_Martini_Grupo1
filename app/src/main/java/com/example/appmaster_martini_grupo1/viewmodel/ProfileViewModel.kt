package com.example.appmaster_martini_grupo1.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appmaster_martini_grupo1.data.local.ProfileDao
import com.example.appmaster_martini_grupo1.data.model.Profile
import com.example.appmaster_martini_grupo1.domain.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userId: Long = 0L,
    val displayName: String = "",
    val email: String = "",
    val phone: String = "",
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

    fun onDisplayNameChange(value: String) = _uiState.update { it.copy(displayName = value, displayNameError = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, emailError = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value, phoneError = null) }

    fun toggleInterest(id: String) = _uiState.update { state ->
        val nuevos = if (id in state.selectedInterests) {
            state.selectedInterests - id
        } else {
            state.selectedInterests + id
        }
        state.copy(selectedInterests = nuevos)
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
        val dao = profileDao ?: run {
            _uiState.update { it.copy(error = "Base de datos no inicializada (falta AppDatabase, ver §8 de 7B.1)") }
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
                        interestsCsv = s.selectedInterests.joinToString(",")
                    )
                )
                _uiState.update { it.copy(isSaving = false, savedMessage = "Perfil guardado") }
            } catch (_: Exception) {
                _uiState.update { it.copy(isSaving = false, error = "No se pudo guardar el perfil") }
            }
        }
    }

    private fun parseInterests(csv: String): Set<String> =
        csv.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
}