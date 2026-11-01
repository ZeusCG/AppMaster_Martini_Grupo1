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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.example.appmaster_martini_grupo1.data.model.defaultInterests
import com.example.appmaster_martini_grupo1.ui.components.AuthTextField
import com.example.appmaster_martini_grupo1.ui.components.InterestChips
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme
import com.example.appmaster_martini_grupo1.viewmodel.ProfileUiState
import com.example.appmaster_martini_grupo1.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    userId: Long = 0L,
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    ProfileContent(
        state = state,
        onDisplayNameChange = viewModel::onDisplayNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneChange = viewModel::onPhoneChange,
        onToggleInterest = viewModel::toggleInterest,
        onSaveClick = viewModel::saveProfile
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: ProfileUiState,
    onDisplayNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onSaveClick: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Mi perfil") }) }
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
                value = state.displayName,
                onValueChange = onDisplayNameChange,
                label = "Nombre",
                error = state.displayNameError,
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
                label = "Teléfono",
                error = state.phoneError,
                icon = Icons.Filled.Phone,
                keyboardType = KeyboardType.Phone
            )

            Text("Mis intereses", style = MaterialTheme.typography.titleMedium)
            InterestChips(
                interests = defaultInterests,
                selectedIds = state.selectedInterests,
                onToggle = onToggleInterest
            )

            state.savedMessage?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.primary)
            }
            state.error?.let { msg ->
                Text(text = msg, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onSaveClick,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Guardar cambios")
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Perfil con intereses")
@Composable
private fun ProfileContentPreview() {
    AppMaster_Martini_Grupo1Theme {
        ProfileContent(
            state = ProfileUiState(
                displayName = "Ana Pérez",
                email = "ana@test.cl",
                phone = "+56912345678",
                selectedInterests = setOf("recetas", "panaderia")
            ),
            onDisplayNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onToggleInterest = {},
            onSaveClick = {}
        )
    }
}