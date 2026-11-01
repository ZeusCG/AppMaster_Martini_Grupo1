package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

            // Confirmación de ÉXITO animada
            AnimatedVisibility(
                visible = state.savedMessage != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ConfirmBanner(
                    message = state.savedMessage.orEmpty(),
                    icon = Icons.Filled.CheckCircle,
                    iconContentDescription = "Guardado correctamente",
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Confirmación de ERROR animada
            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ConfirmBanner(
                    message = state.error.orEmpty(),
                    icon = Icons.Filled.Warning,
                    iconContentDescription = "Error",
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            Button(
                onClick = onSaveClick,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Guardar cambios")
                }
            }
        }
    }
}

@Composable
private fun ConfirmBanner(
    message: String,
    icon: ImageVector,
    iconContentDescription: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(imageVector = icon, contentDescription = iconContentDescription)
            Text(text = message, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Preview(showBackground = true, name = "Perfil con éxito")
@Composable
private fun ProfileContentSuccessPreview() {
    AppMaster_Martini_Grupo1Theme {
        ProfileContent(
            state = ProfileUiState(
                displayName = "Ana Pérez",
                email = "ana@test.cl",
                phone = "+56912345678",
                selectedInterests = setOf("recetas", "panaderia"),
                savedMessage = "Perfil guardado correctamente"
            ),
            onDisplayNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onToggleInterest = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Perfil con error")
@Composable
private fun ProfileContentErrorPreview() {
    AppMaster_Martini_Grupo1Theme {
        ProfileContent(
            state = ProfileUiState(
                displayName = "Ana Pérez",
                email = "ana@test.cl",
                error = "No se pudo guardar el perfil"
            ),
            onDisplayNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onToggleInterest = {},
            onSaveClick = {}
        )
    }
}