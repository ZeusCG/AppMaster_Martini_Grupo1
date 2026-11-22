package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.appmaster_martini_grupo1.ui.components.AppTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(onBack: () -> Unit = {}) {
    Scaffold(
        topBar = { AppTopBar(title = "Información", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Master Martini", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Versión 1.0", style = MaterialTheme.typography.bodyMedium)

            SectionLabel("Qué hace la app")
            Text(
                "Entrega recetas, videos técnicos y cursos de Master Martini Chile, y permite guardar perfil, " +
                        "intereses, favoritos y solicitudes de contacto con un asesor.",
                style = MaterialTheme.typography.bodyMedium
            )

            SectionLabel("APIs externas")
            Text(
                "Clima del taller: datos de Open-Meteo (GET).\n" +
                        "Solicitudes de contacto: JSONPlaceholder (POST, demostración; el historial real se guarda en el teléfono).",
                style = MaterialTheme.typography.bodyMedium
            )

            SectionLabel("Créditos")
            Text("Weather data by Open-Meteo.com", style = MaterialTheme.typography.bodyMedium)
            Text("App desarrollada por Grupo 1 · DSY1105", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}
