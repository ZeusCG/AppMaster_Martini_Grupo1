package com.example.appmaster_martini_grupo1.ui.screens

import android.net.Uri
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.appmaster_martini_grupo1.R
import com.example.appmaster_martini_grupo1.data.model.Content
import com.example.appmaster_martini_grupo1.data.model.Course
import com.example.appmaster_martini_grupo1.data.model.Recipe
import com.example.appmaster_martini_grupo1.data.model.Video
import com.example.appmaster_martini_grupo1.data.session.UserSession
import com.example.appmaster_martini_grupo1.platform.rememberBitmapFromUri
import com.example.appmaster_martini_grupo1.ui.components.AppTopBar
import com.example.appmaster_martini_grupo1.ui.components.FavoriteButton
import com.example.appmaster_martini_grupo1.viewmodel.ContentViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    contentId: String,
    onBack: () -> Unit,
    viewModel: ContentViewModel = viewModel(factory = ContentViewModel.factory(LocalContext.current))
) {
    val items by viewModel.content.collectAsStateWithLifecycle()
    val content = items.firstOrNull { it.id == contentId }
    val favoriteIds by viewModel.favoriteIds.collectAsStateWithLifecycle()

    val userId by UserSession.userId.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.observeFavorites() }

    val photoFile = remember(contentId) {
        val nombreSeguro = contentId.replace(Regex("[^A-Za-z0-9._-]"), "_")
        File(File(context.filesDir, "content_photos"), "$nombreSeguro.jpg")
    }
    var capturedPhotoUri by remember(contentId) {
        mutableStateOf(
            if (photoFile.exists()) Uri.fromFile(photoFile).toString() else null
        )
    }
    var showCamera by remember { mutableStateOf(false) }

    if (showCamera) {
        CameraCaptureScreen(
            onPhotoTaken = { uri ->
                scope.launch {
                    capturedPhotoUri = withContext(Dispatchers.IO) {
                        guardarFotoEnAlmacen(context, uri, photoFile)
                    }
                    showCamera = false
                }
            },
            onClose = { showCamera = false }
        )
        return
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Detalle",
                onBack = onBack,
                actions = {
                    FavoriteButton(
                        isFavorite = contentId in favoriteIds,
                        onToggle = {
                            if (userId == null) {
                                scope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Debes iniciar sesión para guardar favoritos"
                                    )
                                }
                            } else {
                                viewModel.toggleFavorite(contentId)
                            }
                        },
                        inactiveColor = MaterialTheme.colorScheme.onSecondary
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (content == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Contenido no encontrado")
            }
        } else {
            val visibleState = remember {
                MutableTransitionState(false).apply { targetState = true }
            }
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 8 }
            ) {
                DetailContent(
                    content = content,
                    photoUri = capturedPhotoUri,
                    onTakePhoto = { showCamera = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    content: Content,
    photoUri: String?,
    onTakePhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Imagen de ${content.title}",
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentScale = ContentScale.Fit
        )
        Text(
            text = content.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = content.category,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content.description,
            style = MaterialTheme.typography.bodyMedium
        )
        HorizontalDivider()

        when (content) {
            is Recipe -> RecipeDetail(content)
            is Video -> VideoDetail(content)
            is Course -> CourseDetail(content)
            else -> Unit
        }

        HorizontalDivider()
        SectionTitle("Foto de referencia")
        val bitmap = rememberBitmapFromUri(photoUri)
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Foto tomada con la cámara",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                contentScale = ContentScale.Crop
            )
        } else {
            Text(
                text = "Aún no has adjuntado una foto.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(onClick = onTakePhoto, modifier = Modifier.fillMaxWidth()) {
            Text(if (photoUri == null) "Tomar foto con la cámara" else "Reemplazar foto")
        }
    }
}

@Composable
private fun RecipeDetail(recipe: Recipe) {
    SectionTitle("Porciones: ${recipe.portions}")
    SectionTitle("Ingredientes")
    recipe.ingredients.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
    SectionTitle("Preparación")
    recipe.steps.forEachIndexed { index, step ->
        Text("${index + 1}. $step", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun VideoDetail(video: Video) {
    SectionTitle("Duración: ${video.durationMinutes} minutos")
    Text("Enlace: ${video.url}", style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun CourseDetail(course: Course) {
    SectionTitle("Duración: ${course.durationHours} horas")
    SectionTitle("Nivel: ${course.level}")
    SectionTitle("Contenidos")
    course.lessons.forEach { Text("• $it", style = MaterialTheme.typography.bodyMedium) }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold
    )
}


private fun guardarFotoEnAlmacen(
    context: android.content.Context,
    origen: String,
    destino: File
): String {
    return runCatching {
        destino.parentFile?.mkdirs()
        context.contentResolver.openInputStream(Uri.parse(origen))?.use { input ->
            destino.outputStream().use { output -> input.copyTo(output) }
        }
        Uri.fromFile(destino).toString()
    }.getOrDefault(origen)
}