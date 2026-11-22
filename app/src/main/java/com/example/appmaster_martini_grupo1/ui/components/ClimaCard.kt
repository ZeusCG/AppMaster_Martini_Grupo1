package com.example.appmaster_martini_grupo1.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appmaster_martini_grupo1.viewmodel.ClimaUiState

@Composable
fun ClimaCard(
    state: ClimaUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Condiciones de tu taller hoy",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            when (state) {
                is ClimaUiState.Loading -> {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Text("Cargando clima del taller…", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                is ClimaUiState.Success -> {
                    Text(
                        text = "Temperatura: ${state.temperatura} °C",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "Humedad: ${state.humedad} %",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = state.consejo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    if (state.esUltimoDato) {
                        Text(
                            text = "Último dato guardado (${state.hora}) — sin conexión",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else if (state.hora.isNotBlank()) {
                        Text(
                            text = "Actualizado: ${state.hora}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    Text(
                        text = "Weather data by Open-Meteo.com",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                is ClimaUiState.Error -> {
                    Text(
                        text = state.mensaje,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = onRetry) { Text("Reintentar") }
                }
            }
        }
    }
}