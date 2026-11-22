package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import com.example.appmaster_martini_grupo1.ui.components.AuthTextField
import com.example.appmaster_martini_grupo1.ui.components.BackButton
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme
import com.example.appmaster_martini_grupo1.viewmodel.ContactoUiState
import com.example.appmaster_martini_grupo1.viewmodel.ContactoViewModel
import com.example.appmaster_martini_grupo1.viewmodel.SendState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ContactScreen(
    onBack: () -> Unit = {},
    viewModel: ContactoViewModel = viewModel(factory = ContactoViewModel.factory(LocalContext.current))
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ContactContent(
        state = uiState,
        onSubjectChange = viewModel::onSubjectChange,
        onMessageChange = viewModel::onMessageChange,
        onSend = viewModel::send,
        onDelete = viewModel::deleteRequest,
        onBack = onBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactContent(
    state: ContactoUiState,
    onSubjectChange: (String) -> Unit,
    onMessageChange: (String) -> Unit,
    onSend: () -> Unit,
    onDelete: (Long) -> Unit,
    onBack: () -> Unit = {}
) {
    var pendingDelete by remember { mutableStateOf<SolicitudContacto?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Contactar a un asesor") },
                navigationIcon = { BackButton(onClick = onBack) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Enviar una solicitud", style = MaterialTheme.typography.titleLarge)

            AuthTextField(
                value = state.form.subject,
                onValueChange = onSubjectChange,
                label = "Asunto",
                error = state.form.subjectError
            )

            OutlinedTextField(
                value = state.form.message,
                onValueChange = onMessageChange,
                label = { Text("Mensaje") },
                modifier = Modifier.fillMaxWidth(),
                isError = state.form.messageError != null,
                minLines = 4,
                supportingText = {
                    state.form.messageError?.let { Text(it) }
                }
            )

            Button(
                onClick = onSend,
                enabled = state.sendState != SendState.Sending,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.sendState == SendState.Sending) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Enviar solicitud")
                }
            }

            AnimatedVisibility(
                visible = state.sendState is SendState.Sent || state.sendState is SendState.Error,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                when (val s = state.sendState) {
                    is SendState.Sent -> FeedbackBanner(
                        message = "Solicitud enviada correctamente.",
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )

                    is SendState.Error -> FeedbackBanner(
                        message = s.mensaje,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer
                    )

                    else -> Unit
                }
            }

            Text("Historial de solicitudes", style = MaterialTheme.typography.titleLarge)

            if (state.history.isEmpty()) {
                Text(
                    "Aún no has enviado solicitudes.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                state.history.forEach { solicitud ->
                    HistoryItem(
                        solicitud = solicitud,
                        onDelete = { pendingDelete = solicitud }
                    )
                }
            }
        }
    }

    pendingDelete?.let { solicitud ->
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Eliminar solicitud") },
            text = { Text("¿Eliminar la solicitud \"${solicitud.asunto}\"?") },
            confirmButton = {
                TextButton(onClick = {
                    onDelete(solicitud.id)
                    pendingDelete = null
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { pendingDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun HistoryItem(
    solicitud: SolicitudContacto,
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
                Text(solicitud.asunto, style = MaterialTheme.typography.titleMedium)
                Text(solicitud.mensaje, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "Estado: ${solicitud.estado} · ${formatFecha(solicitud.fecha)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (solicitud.estado == SolicitudContacto.ESTADO_ENVIADA) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Eliminar solicitud")
            }
        }
    }
}

@Composable
private fun FeedbackBanner(
    message: String,
    containerColor: Color,
    contentColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = containerColor,
        contentColor = contentColor
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun formatFecha(millis: Long): String =
    SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(millis))

@Preview(showBackground = true)
@Composable
fun ContactContentPreview() {
    AppMaster_Martini_Grupo1Theme {
        ContactContent(
            state = ContactoUiState(
                history = listOf(
                    SolicitudContacto(
                        id = 1, userId = 1,
                        asunto = "Consulta de precios",
                        mensaje = "¿Tienen catálogo 2026?",
                        estado = SolicitudContacto.ESTADO_ENVIADA
                    ),
                    SolicitudContacto(
                        id = 2, userId = 1,
                        asunto = "Curso de bombones",
                        mensaje = "¿Cuándo abre la inscripción?",
                        estado = SolicitudContacto.ESTADO_PENDIENTE
                    )
                )
            ),
            onSubjectChange = {},
            onMessageChange = {},
            onSend = {},
            onDelete = {}
        )
    }
}