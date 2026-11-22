package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.R
import com.example.appmaster_martini_grupo1.data.session.UserSession
import com.example.appmaster_martini_grupo1.ui.components.ClimaCard
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme
import com.example.appmaster_martini_grupo1.viewmodel.ClimaUiState
import com.example.appmaster_martini_grupo1.viewmodel.ClimaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onGoCatalog: () -> Unit = {},
    onGoFavorites: () -> Unit = {},
    onGoManage: () -> Unit = {},
    onGoContact: () -> Unit = {},
    onGoProfile: () -> Unit = {},
    onGoInfo: () -> Unit = {},
    onGoLogin: () -> Unit = {},
    onLogout: () -> Unit = {},
    climaViewModel: ClimaViewModel = viewModel(factory = ClimaViewModel.factory(LocalContext.current))
) {
    val userId by UserSession.userId.collectAsStateWithLifecycle()
    val climaState by climaViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { climaViewModel.load() }

    HomeContent(
        userId = userId,
        climaState = climaState,
        onRetryClima = { climaViewModel.load() },
        onGoCatalog = onGoCatalog,
        onGoFavorites = onGoFavorites,
        onGoManage = onGoManage,
        onGoContact = onGoContact,
        onGoProfile = onGoProfile,
        onGoInfo = onGoInfo,
        onGoLogin = onGoLogin,
        onLogout = onLogout
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    userId: Long?,
    climaState: ClimaUiState,
    onRetryClima: () -> Unit,
    onGoCatalog: () -> Unit,
    onGoFavorites: () -> Unit,
    onGoManage: () -> Unit,
    onGoContact: () -> Unit,
    onGoProfile: () -> Unit,
    onGoInfo: () -> Unit,
    onGoLogin: () -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Master Martini") },
                actions = {
                    if (userId == null) {
                        TextButton(onClick = onGoLogin) { Text("Iniciar sesión") }
                    } else {
                        TextButton(onClick = onLogout) { Text("Cerrar Sesión") }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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

            ClimaCard(state = climaState, onRetry = onRetryClima)

            Button(onClick = onGoCatalog, modifier = Modifier.fillMaxWidth()) {
                Text("Ver catálogo")
            }
            Button(onClick = onGoManage, modifier = Modifier.fillMaxWidth()) {
                Text("Gestionar contenido")
            }
            Button(onClick = onGoContact, modifier = Modifier.fillMaxWidth()) {
                Text("Contactar a un asesor")
            }
            Button(onClick = onGoFavorites, modifier = Modifier.fillMaxWidth()) {
                Text("Mis favoritos")
            }
            Button(onClick = onGoProfile, modifier = Modifier.fillMaxWidth()) {
                Text("Mi perfil")
            }
            Button(onClick = onGoInfo, modifier = Modifier.fillMaxWidth()) {
                Text("Informacion")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppMaster_Martini_Grupo1Theme {
        HomeContent(
            userId = null,
            climaState = ClimaUiState.Success(
                temperatura = 18.0,
                humedad = 50,
                consejo = "Condiciones favorables",
                hora = "12:00"
            ),
            onRetryClima = {},
            onGoCatalog = {},
            onGoFavorites = {},
            onGoManage = {},
            onGoContact = {},
            onGoProfile = {},
            onGoInfo = {},
            onGoLogin = {},
            onLogout = {}
        )
    }
}