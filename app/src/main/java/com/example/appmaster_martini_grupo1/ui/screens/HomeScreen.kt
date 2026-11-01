package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGoCatalog: () -> Unit = {},
    onGoFavorites: () -> Unit = {},
    onGoProfile: () -> Unit = {}
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Master Martini") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logotipo de Master Martini",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentScale = ContentScale.Fit
            )
            Text("¡Bienvenido!", style = MaterialTheme.typography.headlineSmall)

            Button(onClick = onGoCatalog, modifier = Modifier.fillMaxWidth()) {
                Text("Ver catálogo")
            }
            Button(onClick = onGoFavorites, modifier = Modifier.fillMaxWidth()) {
                Text("Mis favoritos")
            }
            Button(onClick = onGoProfile, modifier = Modifier.fillMaxWidth()) {
                Text("Mi perfil")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppMaster_Martini_Grupo1Theme {
        HomeScreen()
    }
}