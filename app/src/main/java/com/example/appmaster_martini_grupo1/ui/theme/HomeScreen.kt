package com.example.appmaster_martini_grupo1.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appmaster_martini_grupo1.R


@OptIn(ExperimentalMaterial3Api::class)     // Permite usar TopAppBar aunque sea experimental
@Composable                                               // Esta función dibuja UI y se recalcula si cambia el estado
fun HomeScreen() {                                        // Punto de entrada de la pantalla (sin parámetros)

    Scaffold(                                             // Estructura general de la pantalla
        topBar = {                                        // Zona superior de la app
            TopAppBar(title = { Text("Mi app Kotlin") })  // Barra con el título "Mi app Kotlin"
        }
    ) { innerPadding ->                                   // Contenido; innerPadding evita que el contenido quede bajo la barra
        Column(                                           // Columna: apila los hijos hacia abajo
            modifier = Modifier                           // Se-chainean modificadores (el orden importa)
                .padding(innerPadding)     // Aplica el margen que dejó el Scaffold
                .fillMaxSize()                            // La columna ocupa todo el espacio
                .padding(16.dp),                     // Espacio interno de 16 dp en los 4 lados
            verticalArrangement = Arrangement.spacedBy(20.dp)  // 20 dp de separación entre cada hijo
        ) {
            Text(text = "¡Bienbenido!")                   // Texto estático de bienvenida

            Button(onClick = {/* accion futura */ }) {    // Botón; onClick queda vacío por ahora
                Text("Presióname")                        // Etiqueta del botón
            }

            Image(                                        // Muestra la imagen del logo
                painter = painterResource(id = R.drawable.logo),  // Carga res/drawable/logo
                contentDescription = "Logo App",          // Texto de accesibilidad para lectores de pantalla
                modifier = Modifier                       // Modificadores de la imagen
                    .fillMaxWidth()                       // Ancho completo
                    .height(150.dp),                      // Alto fijo de 150 dp
                contentScale = ContentScale.Fit           // Escala la imagen sin deformarla, ajustándola al espacio
            )
        }
    }
}

@Preview(showBackground = true)                           // Habilita el fondo por defecto en el preview de Android Studio
@Composable                                               // También es UI, para poder previsualizarla
fun HomeScreenPreview() {                                 // Función exclusiva del preview; no se ejecuta en la app real
    AppMaster_Martini_Grupo1Theme {                       // Envuelve la pantalla con el tema (colores, tipografía)
        HomeScreen()                                      // Renderiza la pantalla dentro del tema
    }
}