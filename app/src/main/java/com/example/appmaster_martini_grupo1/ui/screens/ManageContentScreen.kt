package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.ui.components.AuthTextField
import com.example.appmaster_martini_grupo1.ui.components.BackButton
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme
import com.example.appmaster_martini_grupo1.viewmodel.ContentField
import com.example.appmaster_martini_grupo1.viewmodel.ContentForm
import com.example.appmaster_martini_grupo1.viewmodel.ContentType
import com.example.appmaster_martini_grupo1.viewmodel.ManageContentUiState
import com.example.appmaster_martini_grupo1.viewmodel.ManageContentViewModel

@Composable
fun ManageContentScreen(
    onBack: () -> Unit = {},
    viewModel: ManageContentViewModel = viewModel(factory = ManageContentViewModel.factory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showForm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.message) {
        uiState.message?.let { snackbarHostState.showSnackbar(it) }
    }

    ManageContentContent(
        state = uiState,
        showForm = showForm,
        snackbarHostState = snackbarHostState,
        onCreate = {
            viewModel.startCreate()
            showForm = true
        },
        onEdit = { id ->
            viewModel.startEdit(id)
            showForm = true
        },
        onCancelForm = {
            viewModel.startCreate()
            showForm = false
        },
        onTypeChange = viewModel::onTypeChange,
        onFieldChange = viewModel::onFieldChange,
        onSave = {
            viewModel.save()
            val f = viewModel.uiState.value.form
            if (f.title.isBlank() && f.titleError == null) {
                showForm = false
            }
        },
        onDelete = viewModel::delete,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageContentContent(
    state: ManageContentUiState,
    showForm: Boolean,
    snackbarHostState: SnackbarHostState,
    onCreate: () -> Unit,
    onEdit: (String) -> Unit,
    onCancelForm: () -> Unit,
    onTypeChange: (ContentType) -> Unit,
    onFieldChange: (ContentField, String) -> Unit,
    onSave: () -> Unit,
    onDelete: (String) -> Unit,
    onBack: () -> Unit
) {
    var pendingDelete by remember { mutableStateOf<Content?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestionar contenido") },
                navigationIcon = { BackButton(onClick = onBack) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedVisibility(
                visible = showForm,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                ContentFormSection(
                    form = state.form,
                    onTypeChange = onTypeChange,
                    onFieldChange = onFieldChange,
                    onSave = onSave,
                    onCancel = onCancelForm
                )
            }

            if (!showForm) {
                Button(onClick = onCreate, modifier = Modifier.fillMaxWidth()) {
                    Text("Crear contenido")
                }

                Text(
                    "Contenido (${state.items.size})",
                    style = MaterialTheme.typography.titleLarge
                )

                if (state.items.isEmpty()) {
                    Text(
                        "No hay contenido. Crea el primero.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    state.items.forEach { item ->
                        ManageItemRow(
                            item = item,
                            onEdit = { onEdit(item.id) },
                            onDelete = { pendingDelete = item }
                        )
                    }
                }
            }
        }
    }

    pendingDelete?.let { item ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Eliminar contenido") },
            text = { Text("¿Eliminar \"${item.title}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(item.id)
                    pendingDelete = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun ContentFormSection(
    form: ContentForm,
    onTypeChange: (ContentType) -> Unit,
    onFieldChange: (ContentField, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Text(
        text = if (form.id == null) "Nuevo contenido" else "Editar contenido",
        style = MaterialTheme.typography.titleLarge
    )

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ContentType.values().forEach { type ->
            FilterChip(
                selected = form.type == type,
                onClick = { onTypeChange(type) },
                label = { Text(typeLabel(type)) }
            )
        }
    }

    AuthTextField(
        value = form.title,
        onValueChange = { onFieldChange(ContentField.TITLE, it) },
        label = "Título",
        error = form.titleError
    )
    AuthTextField(
        value = form.category,
        onValueChange = { onFieldChange(ContentField.CATEGORY, it) },
        label = "Categoría",
        error = form.categoryError
    )
    OutlinedTextField(
        value = form.description,
        onValueChange = { onFieldChange(ContentField.DESCRIPTION, it) },
        label = { Text("Descripción") },
        modifier = Modifier.fillMaxWidth(),
        isError = form.descriptionError != null,
        minLines = 3,
        supportingText = { form.descriptionError?.let { Text(it) } }
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
            OutlinedTextField(
                value = form.ingredients,
                onValueChange = { onFieldChange(ContentField.INGREDIENTS, it) },
                label = { Text("Ingredientes (uno por línea)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            OutlinedTextField(
                value = form.steps,
                onValueChange = { onFieldChange(ContentField.STEPS, it) },
                label = { Text("Pasos (uno por línea)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }

        ContentType.VIDEO -> {
            AuthTextField(
                value = form.durationMinutes,
                onValueChange = { onFieldChange(ContentField.DURATION_MINUTES, it) },
                label = "Duración (minutos)",
                error = form.durationError,
                keyboardType = KeyboardType.Number
            )
            AuthTextField(
                value = form.url,
                onValueChange = { onFieldChange(ContentField.URL, it) },
                label = "Enlace (URL)",
                error = form.urlError,
                keyboardType = KeyboardType.Uri
            )
        }

        ContentType.COURSE -> {
            AuthTextField(
                value = form.durationHours,
                onValueChange = { onFieldChange(ContentField.DURATION_HOURS, it) },
                label = "Duración (horas)",
                error = form.durationError,
                keyboardType = KeyboardType.Number
            )
            AuthTextField(
                value = form.level,
                onValueChange = { onFieldChange(ContentField.LEVEL, it) },
                label = "Nivel",
                error = form.levelError
            )
            OutlinedTextField(
                value = form.lessons,
                onValueChange = { onFieldChange(ContentField.LESSONS, it) },
                label = { Text("Lecciones (una por línea)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(onClick = onSave, modifier = Modifier.weight(1f)) {
            Text(if (form.id == null) "Crear" else "Guardar")
        }
        OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) {
            Text("Cancelar")
        }
    }
}

@Composable
private fun ManageItemRow(
    item: Content,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.category,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Filled.Edit, contentDescription = "Editar")
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

private fun typeLabel(type: ContentType): String = when (type) {
    ContentType.RECIPE -> "Receta"
    ContentType.VIDEO -> "Video"
    ContentType.COURSE -> "Curso"
}

@Preview(showBackground = true)
@Composable
fun ManageContentContentPreview() {
    AppMaster_Martini_Grupo1Theme {
        ManageContentContent(
            state = ManageContentUiState(
                items = listOf(
                    Recipe(
                        id = "recipe-1",
                        title = "Ganache de chocolate",
                        description = "Base cremosa para rellenos",
                        category = "Chocolatería",
                        portions = 12,
                        ingredients = emptyList(),
                        steps = emptyList()
                    )
                )
            ),
            showForm = false,
            snackbarHostState = remember { SnackbarHostState() },
            onCreate = {},
            onEdit = {},
            onCancelForm = {},
            onTypeChange = {},
            onFieldChange = { _, _ -> },
            onSave = {},
            onDelete = {},
            onBack = {}
        )
    }
}