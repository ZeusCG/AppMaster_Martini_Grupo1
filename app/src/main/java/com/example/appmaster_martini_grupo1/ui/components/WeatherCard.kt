package com.example.appmaster_martini_grupo1.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmaster_martini_grupo1.R
import com.example.appmaster_martini_grupo1.viewmodel.ClimaUiState

/**
 * Tarjeta "Condiciones de tu taller hoy" (H-018). Consume solo el ViewModel de clima
 * (el crédito a Open-Meteo es obligatorio por el uso de su API).
 */
@Composable
fun WeatherCard(
    state: ClimaUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(R.string.weather_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            when (state) {
                ClimaUiState.Loading -> Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    Text("Cargando clima del taller…")
                }

                is ClimaUiState.Success -> {
                    Text("Temperatura: ${state.temperatura} °C")
                    Text("Humedad: ${state.humedad} %")
                    Text(state.consejo, style = MaterialTheme.typography.bodyMedium)
                    if (state.hora.isNotBlank()) {
                        val prefijo = if (state.esUltimoDato) "Último dato guardado de las" else "Actualizado a las"
                        Text(
                            text = "$prefijo ${state.hora}",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                is ClimaUiState.Error -> {
                    Text(state.mensaje, style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onRetry) { Text("Reintentar") }
                }
            }
            Text(
                text = stringResource(R.string.weather_credit),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}
