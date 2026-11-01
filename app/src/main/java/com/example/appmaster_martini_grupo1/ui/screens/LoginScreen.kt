package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.ui.platform.LocalContext
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
fun LoginScreen(
    viewModel: AuthViewModel = viewModel(factory = AuthViewModel.factory(LocalContext.current)),
    onNavigateToRegister: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onLoginSuccess()
    }

    LoginContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLoginClick = viewModel::onLoginClick,
        onNavigateToRegister = onNavigateToRegister
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Iniciar sesión") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Master Martini", style = MaterialTheme.typography.headlineSmall)

            AuthTextField(
                value = state.email,
                onValueChange = onEmailChange,
                label = "Correo electrónico",
                error = state.emailError,
                icon = Icons.Filled.Email,
                keyboardType = KeyboardType.Email
            )
            AuthTextField(
                value = state.password,
                onValueChange = onPasswordChange,
                label = "Contraseña",
                error = state.passwordError,
                icon = Icons.Filled.Lock,
                isPassword = true
            )

            state.authError?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onLoginClick,
                enabled = !state.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Ingresar")
                }
            }

            TextButton(onClick = onNavigateToRegister, modifier = Modifier.fillMaxWidth()) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}

@Preview(showBackground = true, name = "Login con errores")
@Composable
private fun LoginContentPreview() {
    AppMaster_Martini_Grupo1Theme {
        LoginContent(
            state = AuthUiState(
                email = "correo-malo",
                password = "",
                emailError = "Ingresa un correo válido",
                passwordError = "Ingresa tu contraseña"
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onNavigateToRegister = {}
        )
    }
}