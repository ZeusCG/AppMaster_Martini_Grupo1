package com.example.appmaster_martini_grupo1.platform

import android.Manifest
import android.content.Context
import androidx.camera.core.ImageCapture
import java.io.File

/** Permiso requerido para usar la cámara. */
const val cameraPermission: String = Manifest.permission.CAMERA

/** Crea un archivo temporal (en el caché de la app) donde se guardará la foto capturada. */
fun createPhotoFile(context: Context): File =
    File.createTempFile("mm_photo_", ".jpg", context.cacheDir)

/** Opciones de salida de ImageCapture apuntando a un archivo. */
fun photoOutputOptions(file: File): ImageCapture.OutputFileOptions =
    ImageCapture.OutputFileOptions.Builder(file).build()
