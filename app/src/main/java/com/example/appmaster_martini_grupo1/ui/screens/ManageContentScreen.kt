package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.ui.components.AppTopBar
import com.example.appmaster_martini_grupo1.ui.components.AuthTextField
import com.example.appmaster_martini_grupo1.viewmodel.ContentField
import com.example.appmaster_martini_grupo1.viewmodel.ContentForm
import com.example.appmaster_martini_grupo1.viewmodel.ContentType
import com.example.appmaster_martini_grupo1.viewmodel.ManageContentViewModel

/**
 * Pantalla "Gestionar contenido" (H-005): lista con editar/eliminar (con confirmación)
 * y formulario de crear/editar con validación por campo. Consume solo el ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageContentScreen(
    onBack: () -> Unit = {},
    viewModel: ManageContentViewModel = viewModel(factory = ManageContentViewModel.factory(LocalContext.current))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var showForm by remember { mutableStateOf(false) }
    var confirmDeleteId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = { AppTopBar(title = "Gestionar contenido", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!showForm && state.message != null) {
                Text(state.message.orEmpty(), color = MaterialTheme.colorScheme.primary)
            }

            if (showForm) {
                ContentFormView(
                    form = state.form,
                    onTypeChange = viewModel::onTypeChange,
                    onFieldChange = viewModel::onFieldChange,
                    onSave = {
                        viewModel.save()
                        // Si no quedaron errores por campo, el guardado fue válido: cierra el formulario.
                        if (!viewModel.uiState.value.form.hasFieldErrors()) showForm = false
                    },
                    onCancel = { showForm = false }
                )
            } else {
                Button(
                    onClick = { viewModel.startCreate(); showForm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear contenido")
                }

                if (state.items.isEmpty()) {
                    Text("Aún no hay contenido. Crea el primero con el botón de arriba.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items, key = { it.id }) { item ->
                            ManageContentRow(
                                item = item,
                                onEdit = { viewModel.startEdit(item.id); showForm = true },
                                onDelete = { confirmDeleteId = item.id }
                            )
                        }
                    }
                }
            }
        }
    }

    val idToDelete = confirmDeleteId
    if (idToDelete != null) {
        AlertDialog(
            onDismissRequest = { confirmDeleteId = null },
            title = { Text("Eliminar contenido") },
            text = { Text("¿Seguro que quieres eliminar este contenido? También se quitará de favoritos.") },
            confirmButton = {
                TextButton(onClick = { viewModel.delete(idToDelete); confirmDeleteId = null }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteId = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun ManageContentRow(
    item: Content,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(item.category, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        }
        IconButton(onClick = onEdit) {
            Icon(Icons.Filled.Edit, contentDescription = "Editar ${item.title}")
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar ${item.title}")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ContentFormView(
    form: ContentForm,
    onTypeChange: (ContentType) -> Unit,
    onFieldChange: (ContentField, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = if (form.id == null) "Nuevo contenido" else "Editar contenido",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ContentType.entries.forEach { type ->
                FilterChip(
                    selected = form.type == type,
                    onClick = { onTypeChange(type) },
                    label = { Text(type.label()) }
                )
            }
        }

        AuthTextField(
            value = form.title,
            onValueChange = { onFieldChange(ContentField.TITLE, it) },
            label = "Título",
            error = form.titleError,
            icon = Icons.Filled.Edit
        )
        AuthTextField(
            value = form.category,
            onValueChange = { onFieldChange(ContentField.CATEGORY, it) },
            label = "Categoría",
            error = form.categoryError
        )
        MultilineField(
            label = "Descripción",
            value = form.description,
            onValueChange = { onFieldChange(ContentField.DESCRIPTION, it) },
            error = form.descriptionError
        )

        when (form.type) {
            ContentType.RECIPE -> {
                AuthTextField(
                    value = form.portions,
                    onValueChange = { onFieldChange(ContentField.PORTIONS, it) },
                    label = "Porciones",
                    error = form.portionsError,
                    keyboardType = KeyboardType.Number
                )
                MultilineField(
                    label = "Ingredientes (uno por línea)",
                    value = form.ingredients,
                    onValueChange = { onFieldChange(ContentField.INGREDIENTS, it) }
                )
                MultilineField(
                    label = "Preparación (un paso por línea)",
                    value = form.steps,
                    onValueChange = { onFieldChange(ContentField.STEPS, it) }
                )
            }

            ContentType.VIDEO -> {
                AuthTextField(
                    value = form.durationMinutes,
                    onValueChange = { onFieldChange(ContentField.DURATION_MINUTES, it) },
                    label = "Duración en minutos",
                    error = form.durationError,
                    keyboardType = KeyboardType.Number
                )
                AuthTextField(
                    value = form.url,
                    onValueChange = { onFieldChange(ContentField.URL, it) },
                    label = "Enlace del video",
                    error = form.urlError,
                    keyboardType = KeyboardType.Uri
                )
            }

            ContentType.COURSE -> {
                AuthTextField(
                    value = form.durationHours,
                    onValueChange = { onFieldChange(ContentField.DURATION_HOURS, it) },
                    label = "Duración en horas",
                    error = form.durationError,
                    keyboardType = KeyboardType.Number
                )
                AuthTextField(
                    value = form.level,
                    onValueChange = { onFieldChange(ContentField.LEVEL, it) },
                    label = "Nivel",
                    error = form.levelError
                )
                MultilineField(
                    label = "Lecciones (una por línea)",
                    value = form.lessons,
                    onValueChange = { onFieldChange(ContentField.LESSONS, it) }
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(onClick = onSave, modifier = Modifier.weight(1f)) { Text("Guardar") }
            OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("Cancelar") }
        }
    }
}

@Composable
private fun MultilineField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        minLines = 3,
        isError = error != null,
        supportingText = if (error != null) {
            { Text(error.orEmpty()) }
        } else null
    )
}

private fun ContentType.label(): String = when (this) {
    ContentType.RECIPE -> "Receta"
    ContentType.VIDEO -> "Video"
    ContentType.COURSE -> "Curso"
}

private fun ContentForm.hasFieldErrors(): Boolean =
    titleError != null || categoryError != null || descriptionError != null ||
            portionsError != null || durationError != null || urlError != null || levelError != null
