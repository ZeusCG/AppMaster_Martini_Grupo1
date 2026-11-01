package com.example.appmaster_martini_grupo1.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.appmaster_martini_grupo1.data.local.AppDatabase
import com.example.appmaster_martini_grupo1.data.local.ProfileDao
import com.example.appmaster_martini_grupo1.data.local.UserDao
import com.example.appmaster_martini_grupo1.data.model.Profile
import com.example.appmaster_martini_grupo1.data.model.User
import com.example.appmaster_martini_grupo1.data.session.UserSession
import com.example.appmaster_martini_grupo1.domain.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val phone: String = "",
    val rut: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val phoneError: String? = null,
    val rutError: String? = null,
    val isLoading: Boolean = false,
    val authError: String? = null,
    val isLoggedIn: Boolean = false,
    val userId: Long? = null
)

class AuthViewModel(
    private val userDao: UserDao? = null,
    private val profileDao: ProfileDao? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
        val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null) }
    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value, emailError = null) }
    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value, passwordError = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, confirmPasswordError = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value, phoneError = null) }
    fun onRutChange(value: String) = _uiState.update { it.copy(rut = value, rutError = null) }
    fun clearAuthError() = _uiState.update { it.copy(authError = null) }

    private fun validateLogin(): Boolean {
        val s = _uiState.value
        val emailError = if (!Validators.isValidEmail(s.email)) "Ingresa un correo válido" else null
        val passwordError = if (s.password.isBlank()) "Ingresa tu contraseña" else null
        _uiState.update { it.copy(emailError = emailError, passwordError = passwordError) }
        return emailError == null && passwordError == null
    }

    private fun validateRegister(): Boolean {
        val s = _uiState.value
        val nameError = if (s.name.trim().length < 2) "Ingresa tu nombre" else null
        val emailError = if (!Validators.isValidEmail(s.email)) "Ingresa un correo válido" else null
        val passwordError = if (!Validators.isValidPassword(s.password)) "Mínimo 8 caracteres, con letra y número" else null
        val confirmError = if (s.confirmPassword != s.password) "Las contraseñas no coinciden" else null
        val phoneError = if (s.phone.isNotBlank() && !Validators.isValidPhone(s.phone)) "Teléfono inválido" else null
        val rutError = if (s.rut.isNotBlank() && !Validators.isValidRut(s.rut)) "RUT inválido" else null
        _uiState.update {
            it.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmError,
                phoneError = phoneError,
                rutError = rutError
            )
        }
        return listOf(nameError, emailError, passwordError, confirmError, phoneError, rutError).all { it == null }
    }

    fun onLoginClick() {
        if (!validateLogin()) return
        val dao = userDao
        if (dao == null) {
            _uiState.update { it.copy(authError = "No se pudo conectar con el almacenamiento local. Intenta nuevamente.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, authError = null) }
        viewModelScope.launch {
            val user = dao.authenticate(_uiState.value.email.trim(), _uiState.value.password)
            if (user == null) {
                _uiState.update { it.copy(isLoading = false, authError = "Correo o contraseña incorrectos") }
            } else {
                UserSession.set(user.id)
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true, userId = user.id) }
            }
        }
    }

    fun onRegisterClick() {
        if (!validateRegister()) return
        val userDaoRef = userDao
        val profileDaoRef = profileDao
        if (userDaoRef == null || profileDaoRef == null) {
            _uiState.update { it.copy(authError = "No se pudo conectar con el almacenamiento local. Intenta nuevamente.") }
            return
        }
        _uiState.update { it.copy(isLoading = true, authError = null) }
        viewModelScope.launch {
            try {
                val s = _uiState.value
                val newId = userDaoRef.insert(
                    User(
                        name = s.name.trim(),
                        email = s.email.trim(),
                        password = s.password,
                        phone = s.phone.trim(),
                        rut = s.rut.trim()
                    )
                )
                profileDaoRef.upsert(
                    Profile(
                        userId = newId,
                        displayName = s.name.trim(),
                        email = s.email.trim(),
                        phone = s.phone.trim(),
                    )
                )
                UserSession.set(newId)
                _uiState.update { it.copy(isLoading = false, isLoggedIn = true, userId = newId) }
            } catch (_: Exception) {
                _uiState.update { it.copy(isLoading = false, authError = "No se pudo crear la cuenta. Revisa los datos e intenta nuevamente.") }
            }
        }
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val db = AppDatabase.getInstance(context.applicationContext)
                AuthViewModel(userDao = db.userDao(), profileDao = db.profileDao())
            }
        }
    }
}