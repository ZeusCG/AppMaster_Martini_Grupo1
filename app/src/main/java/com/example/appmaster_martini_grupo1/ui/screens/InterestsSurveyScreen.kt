package com.example.appmaster_martini_grupo1.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.data.model.defaultInterests
import com.example.appmaster_martini_grupo1.ui.components.InterestChips
import com.example.appmaster_martini_grupo1.ui.theme.AppMaster_Martini_Grupo1Theme
import com.example.appmaster_martini_grupo1.viewmodel.ProfileUiState
import com.example.appmaster_martini_grupo1.viewmodel.ProfileViewModel

@Composable
fun InterestsSurveyScreen(
    viewModel: ProfileViewModel = viewModel(),
    userId: Long = 0L,
    onDone: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) { viewModel.loadProfile(userId) }
    // Cuando el guardado tiene éxito, avisa a quien navega.
    LaunchedEffect(state.savedMessage) {
        if (state.savedMessage != null) onDone()
    }

    InterestsSurveyContent(
        state = state,
        onToggleInterest = viewModel::toggleInterest,
        onSaveClick = viewModel::saveProfile,
        onSkip = onDone
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterestsSurveyContent(
    state: ProfileUiState,
    onToggleInterest: (String) -> Unit,
    onSaveClick: () -> Unit,
    onSkip: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Tus intereses") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("¿Qué te interesa?", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Elige tus temas favoritos para personalizar las recetas, videos y cursos.",
                style = MaterialTheme.typography.bodyMedium
            )

            InterestChips(
                interests = defaultInterests,
                selectedIds = state.selectedInterests,
                onToggle = onToggleInterest
            )

            AnimatedVisibility(
                visible = state.error != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(text = state.error.orEmpty(), color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = onSaveClick,
                enabled = !state.isSaving && state.selectedInterests.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Guardar intereses")
                }
            }

            TextButton(onClick = onSkip, modifier = Modifier.fillMaxWidth()) {
                Text("Omitir por ahora")
            }
        }
    }
}

@Preview(showBackground = true, name = "Encuesta con selección")
@Composable
private fun InterestsSurveyContentPreview() {
    AppMaster_Martini_Grupo1Theme {
        InterestsSurveyContent(
            state = ProfileUiState(selectedInterests = setOf("recetas", "postres")),
            onToggleInterest = {},
            onSaveClick = {},
            onSkip = {}
        )
    }
}