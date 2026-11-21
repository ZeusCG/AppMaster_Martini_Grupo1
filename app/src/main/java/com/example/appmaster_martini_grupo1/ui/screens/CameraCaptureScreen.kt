package com.example.appmaster_martini_grupo1.ui.screens

import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.appmaster_martini_grupo1.platform.cameraPermission
import com.example.appmaster_martini_grupo1.platform.createPhotoFile
import com.example.appmaster_martini_grupo1.platform.openAppSettings
import com.example.appmaster_martini_grupo1.platform.photoOutputOptions
import com.example.appmaster_martini_grupo1.platform.rememberBitmapFromUri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraCaptureScreen(
    onPhotoTaken: (String) -> Unit, onClose: () -> Unit, modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, cameraPermission
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var permissionDenied by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var pendingPhotoUri by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasPermission = granted
        permissionDenied = !granted
    }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(cameraPermission)
    }

    Scaffold(
        modifier = modifier, topBar = {
            TopAppBar(
                title = { Text("Tomar foto") }, navigationIcon = {
                    TextButton(onClick = onClose) {
                        Text("Volver", color = MaterialTheme.colorScheme.onSecondary)
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = MaterialTheme.colorScheme.onSecondary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondary
                )
            )
        }) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                hasPermission -> {
                    CameraPreview(
                        onPhotoTaken = { pendingPhotoUri = it },
                        onError = { errorMessage = it }
                    )
                }

                permissionDenied -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Sin permiso de cámara no se pueden tomar fotos.",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Button(onClick = { openAppSettings(context) }) {
                            Text("Abrir ajustes")
                        }
                    }
                }

                else -> {
                    Text(
                        "Solicitando permiso de cámara…",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            errorMessage?.let { msg ->
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }

            pendingPhotoUri?.let { uri ->
                PhotoConfirmation(
                    uri = uri,
                    onAccept = { onPhotoTaken(uri) },
                    onRetake = { pendingPhotoUri = null }
                )
            }
        }
    }
}

@Composable
private fun CameraPreview(
    onPhotoTaken: (String) -> Unit, onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(CameraController.IMAGE_CAPTURE)
        }
    }

    DisposableEffect(lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        onDispose { controller.unbind() }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(), factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = controller
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            })

        Button(
            onClick = {
                val file = createPhotoFile(context)
                controller.takePicture(
                    photoOutputOptions(file),
                    ContextCompat.getMainExecutor(context),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            onPhotoTaken(Uri.fromFile(file).toString())
                        }

                        override fun onError(exception: ImageCaptureException) {
                            onError("No se pudo capturar la foto. Intenta nuevamente.")
                        }
                    })
            }, modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Text("Capturar")
        }
    }
}

@Composable
private fun PhotoConfirmation(
    uri: String,
    onAccept: () -> Unit,
    onRetake: () -> Unit
) {
    val bitmap = rememberBitmapFromUri(uri)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Foto tomada",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onRetake,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.tertiary,
                    containerColor = MaterialTheme.colorScheme.onTertiary)) {
                Text("Tomar otra")
            }
            Button(
                onClick = onAccept,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary)
                ) {
                Text("Usar foto")
            }
        }
    }
}