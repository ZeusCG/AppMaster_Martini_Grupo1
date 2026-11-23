package com.example.appmaster_martini_grupo1.ui.screens

import kotlinx.coroutines.delay
import com.example.appmaster_martini_grupo1.data.session.UserSession
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.data.model.defaultInterests
import com.example.appmaster_martini_grupo1.platform.GalleryResult
import com.example.appmaster_martini_grupo1.platform.openAppSettings
import com.example.appmaster_martini_grupo1.platform.rememberBitmapFromUri
import com.example.appmaster_martini_grupo1.platform.rememberGalleryLauncher
import com.example.appmaster_martini_grupo1.ui.components.AppTopBar
import com.example.appmaster_martini_grupo1.ui.components.AuthTextField
import com.example.appmaster_martini_grupo1.ui.components.InterestChips
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme
import com.example.appmaster_martini_grupo1.viewmodel.ProfileUiState
import com.example.appmaster_martini_grupo1.viewmodel.ProfileViewModel


@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.factory(LocalContext.current)),
    userId: Long = 0L,
    onBack: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var galleryMessage by remember { mutableStateOf<String?>(null) }

    val photo = rememberBitmapFromUri(state.photoUri)

    val pickImage = rememberGalleryLauncher { result ->
        when (result) {
            is GalleryResult.Picked -> {
                galleryMessage = null
                viewModel.onPhotoPicked(result.uri)
            }
            GalleryResult.PermissionDenied ->
                galleryMessage = "Sin permiso no se pueden leer imágenes. Actívalo en Ajustes."
            GalleryResult.Cancelled -> Unit

        }
    }

    val sessionId by UserSession.userId.collectAsStateWithLifecycle()
    val effectiveId = sessionId ?: userId

    if (effectiveId == 0L) {
        Scaffold(topBar = { AppTopBar(title = "Mi perfil", onBack = onBack) }) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Inicia sesión para ver y guardar tu perfil.")
            }
        }
        return
    }

    LaunchedEffect(effectiveId) {
        viewModel.loadProfile(effectiveId)
    }

    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) {
            delay(1000)
            onBack()
        }
    }

    ProfileContent(
        state = state,
        photo = photo,
        galleryMessage = galleryMessage,
        onDisplayNameChange = viewModel::onDisplayNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPhoneChange = viewModel::onPhoneChange,
        onToggleInterest = viewModel::toggleInterest,
        onPickImage = pickImage,
        onRemovePhoto = viewModel::onPhotoRemoved,
        onOpenSettings = { openAppSettings(context) },
        onSaveClick = viewModel::saveProfile,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileContent(
    state: ProfileUiState,
    photo: ImageBitmap?,
    galleryMessage: String?,
    onDisplayNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onToggleInterest: (String) -> Unit,
    onPickImage: () -> Unit,
    onRemovePhoto: () -> Unit,
    onOpenSettings: () -> Unit,
    onSaveClick: () -> Unit,
    onBack: () -> Unit = {}
) {
    Scaffold(
        topBar = { AppTopBar(title = "Mi perfil", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfilePhotoHeader(
                photo = photo,
                hasPhoto = state.photoUri != null,
                onPickImage = onPickImage,
                onRemovePhoto = onRemovePhoto
            )

            ProfileGalleryMessage(
                message = galleryMessage,
                onOpenSettings = onOpenSettings
            )

            ProfileFields(
                state = state,
                onDisplayNameChange = onDisplayNameChange,
                onEmailChange = onEmailChange,
                onPhoneChange = onPhoneChange
            )

            ProfileInterestsSection(
                selectedInterests = state.selectedInterests,
                onToggleInterest = onToggleInterest
            )

            ProfileMessages(
                savedMessage = state.savedMessage,
                error = state.error
            )

            ProfileSaveButton(
                isSaving = state.isSaving,
                onSaveClick = onSaveClick
            )
        }
    }
}

@Composable
private fun ProfilePhotoHeader(
    photo: ImageBitmap?,
    hasPhoto: Boolean,
    onPickImage: () -> Unit,
    onRemovePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (photo != null) {
            Image(
                bitmap = photo,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Sin foto de perfil",
                modifier = Modifier.size(88.dp)
            )
        }
        Column {
            Text("Foto de perfil", style = MaterialTheme.typography.titleMedium)
            TextButton(onClick = onPickImage) { Text("Elegir de la galería") }
            if (hasPhoto) {
                TextButton(onClick = onRemovePhoto) { Text("Quitar foto") }
            }
        }
    }
}

@Composable
private fun ProfileGalleryMessage(
    message: String?,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (message == null) return
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        TextButton(onClick = onOpenSettings) { Text("Abrir ajustes") }
    }
}

@Composable
private fun ProfileFields(
    state: ProfileUiState,
    onDisplayNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
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
    }
}

@Composable
private fun ProfileInterestsSection(
    selectedInterests: Set<String>,
    onToggleInterest: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Mis intereses", style = MaterialTheme.typography.titleMedium)
        InterestChips(
            interests = defaultInterests,
            selectedIds = selectedInterests,
            onToggle = onToggleInterest
        )
    }
}

@Composable
private fun ProfileMessages(
    savedMessage: String?,
    error: String?
) {
    ProfileMessage(
        message = savedMessage,
        icon = Icons.Filled.CheckCircle,
        iconContentDescription = "Guardado correctamente",
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
    ProfileMessage(
        message = error,
        icon = Icons.Filled.Warning,
        iconContentDescription = "Error",
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer
    )
}

@Composable
private fun ProfileMessage(
    message: String?,
    icon: ImageVector,
    iconContentDescription: String,
    containerColor: Color,
    contentColor: Color
) {
    AnimatedVisibility(
        visible = message != null,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        ConfirmBanner(
            message = message.orEmpty(),
            icon = icon,
            iconContentDescription = iconContentDescription,
            containerColor = containerColor,
            contentColor = contentColor
        )
    }
}

@Composable
private fun ProfileSaveButton(
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onSaveClick,
        enabled = !isSaving,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Text("Guardar cambios")
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

@Preview(showBackground = true, name = "Perfil con foto")
@Composable
private fun ProfileContentPreview() {
    AppMaster_Martini_Grupo1Theme {
        ProfileContent(
            state = ProfileUiState(
                displayName = "Ana Pérez",
                email = "ana@test.cl",
                phone = "+56912345678",
                photoUri = "content://demo/foto.jpg",
                selectedInterests = setOf("recetas", "panaderia"),
                savedMessage = "Perfil guardado correctamente"
            ),
            photo = null,
            galleryMessage = null,
            onDisplayNameChange = {},
            onEmailChange = {},
            onPhoneChange = {},
            onToggleInterest = {},
            onPickImage = {},
            onRemovePhoto = {},
            onOpenSettings = {},
            onSaveClick = {}
        )
    }
}