package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.ui.components.AuthTextField
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme
import com.example.appmaster_martini_grupo1.viewmodel.AuthUiState
import com.example.appmaster_martini_grupo1.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onRegisterSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onRegisterSuccess()
    }

    RegisterContent(
        state = state,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneChange = viewModel::onPhoneChange,
        onRutChange = viewModel::onRutChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegisterClick = viewModel::onRegisterClick,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterContent(
    state: AuthUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onRutChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Crear cuenta") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AuthTextField(
                value = state.name,
                onValueChange = onNameChange,
                label = "Nombre",
                error = state.nameError,
                icon = Icons.Filled.Person
            )
            AuthTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = "Correo electrónico",
                error = state.emailError,
                icon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            AuthTextField(
                value = state.phone,
                onValueChange = onPhoneChange,
                label = "Teléfono (opcional)",
                error = state.phoneError,
                icon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )
            AuthTextField(
                value = state.rut,
                onValueChange = onRutChange,
                label = "RUT (opcional)",
                error = state.rutError
            )
            AuthTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                error = state.passwordError,
                icon = Icons.Filled.Lock,
                isPassword = true
            )
            AuthTextField(
                value = state.confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Repetir contraseña",
                error = state.confirmPasswordError,
                icon = Icons.Filled.Lock,
                isPassword = true
            )

            state.authError?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onRegisterClick,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Registrarme")
                }
            }

            TextButton(onClick = onNavigateBack, modifier = Modifier.fillMaxWidth()) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

@Preview(showBackground = true, name = "Registro con errores")
@Composable
private fun RegisterContentPreview() {
    AppMaster_Martini_Grupo1Theme {
        RegisterContent(
            state = AuthUiState(
                name = "Ana",
                email = "ana@test.cl",
                password = "Clave123",
                confirmPassword = "Clave999",
                confirmPasswordError = "Las contraseñas no coinciden",
                rutError = "RUT inválido"
            ),
            onNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onRutChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onRegisterClick = {},
            onNavigateBack = {}
        )
    }
}