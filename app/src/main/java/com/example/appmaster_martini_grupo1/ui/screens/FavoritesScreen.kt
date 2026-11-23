package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.data.session.UserSession
import com.example.appmaster_martini_grupo1.ui.components.AppTopBar
import com.example.appmaster_martini_grupo1.ui.components.ContentCard
import com.example.appmaster_martini_grupo1.viewmodel.ContentUiState
import com.example.appmaster_martini_grupo1.viewmodel.ContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    onItemClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ContentViewModel = viewModel(factory = ContentViewModel.factory(LocalContext.current))
) {
    val content by viewModel.content.collectAsStateWithLifecycle()
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.observeFavorites() }

    val favorites = content.filter { it.id in favoriteIds }

    Scaffold(
        topBar = { AppTopBar(title = "Mis favoritos", onBack = onBack) }
    ) { innerPadding ->
        when (val state = uiState) {
            ContentUiState.Loading -> CenteredMessage(innerPadding) { CircularProgressIndicator() }
            is ContentUiState.Error -> CenteredMessage(innerPadding) { Text(state.message) }
            is ContentUiState.Success -> {
                if (favorites.isEmpty()) {
                    CenteredMessage(innerPadding) {
                        Text(
                            text = if (UserSession.userId.value == null) {
                                "Inicia sesión para guardar y ver tus favoritos."
                            } else {
                                "Aún no tienes favoritos. Marca contenido con el corazón en el catálogo."
                            },
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(favorites, key = { it.id }) { item ->
                            ContentCard(
                                content = item,
                                onClick = { onItemClick(item.id) },
                                isFavorite = true,
                                onToggleFavorite = { viewModel.toggleFavorite(item.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CenteredMessage(padding: androidx.compose.foundation.layout.PaddingValues, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
