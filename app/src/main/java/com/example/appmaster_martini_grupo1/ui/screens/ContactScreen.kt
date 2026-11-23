package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.data.model.SolicitudContacto
import com.example.appmaster_martini_grupo1.data.session.UserSession
import com.example.appmaster_martini_grupo1.ui.components.AppTopBar
import com.example.appmaster_martini_grupo1.ui.components.AuthTextField
import com.example.appmaster_martini_grupo1.viewmodel.ContactoViewModel
import com.example.appmaster_martini_grupo1.viewmodel.SendState

@Composable
fun ContactScreen(
    onBack: () -> Unit = {},
    viewModel: ContactoViewModel = viewModel(factory = ContactoViewModel.factory(LocalContext.current))
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val userId by UserSession.userId.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { AppTopBar(title = "Contactar a un asesor", onBack = onBack) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .imePadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Escríbenos y te contactamos", style = MaterialTheme.typography.titleMedium)
            }

            if (userId == null) {
                item {
                    Text(
                        "Inicia sesión para enviar una solicitud y ver tu historial.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            item {
                AuthTextField(
                    value = state.form.subject,
                    onValueChange = viewModel::onSubjectChange,
                    label = "Asunto",
                    error = state.form.subjectError,
                    icon = Icons.Filled.Email
                )
            }

            item {
                OutlinedTextField(
                    value = state.form.message,
                    onValueChange = viewModel::onMessageChange,
                    label = { Text("Mensaje") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    isError = state.form.messageError != null,
                    supportingText = if (state.form.messageError != null) {
                        { Text(state.form.messageError.orEmpty()) }
                    } else null
                )
            }

            item {
                Button(
                    onClick = viewModel::send,
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
            }

            item {
                when (val send = state.sendState) {
                    SendState.Sent -> Text(
                        "Solicitud enviada. ¡Gracias por escribirnos!",
                        color = MaterialTheme.colorScheme.primary
                    )
                    is SendState.Error -> Text(send.mensaje, color = MaterialTheme.colorScheme.error)
                    SendState.Idle, SendState.Sending -> Unit
                }
            }

            item {
                Text("Historial de solicitudes", style = MaterialTheme.typography.titleMedium)
            }

            if (state.history.isEmpty()) {
                item {
                    Text("Aún no has enviado solicitudes.")
                }
            } else {
                items(state.history, key = { it.id }) { solicitud ->
                    ContactHistoryRow(
                        solicitud = solicitud,
                        onDelete = { viewModel.deleteRequest(solicitud.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactHistoryRow(
    solicitud: SolicitudContacto,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(solicitud.asunto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(solicitud.mensaje, style = MaterialTheme.typography.bodySmall, maxLines = 2)
            val enviada = solicitud.estado == SolicitudContacto.ESTADO_ENVIADA
            Text(
                text = if (enviada) "Enviada" else "Pendiente",
                style = MaterialTheme.typography.labelMedium,
                color = if (enviada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Filled.Delete, contentDescription = "Eliminar solicitud ${solicitud.asunto}")
        }
    }
}
