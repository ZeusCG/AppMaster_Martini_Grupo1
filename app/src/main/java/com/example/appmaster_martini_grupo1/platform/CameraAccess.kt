package com.example.appmaster_martini_grupo1.platform

import android.Manifest
import android.content.Context
import androidx.camera.core.ImageCapture
import java.io.File

const val cameraPermission: String = Manifest.permission.CAMERA

fun createPhotoFile(context: Context): File =
    File.createTempFile("mm_photo_", ".jpg", context.cacheDir)

fun photoOutputOptions(file: File): ImageCapture.OutputFileOptions =
    ImageCapture.OutputFileOptions.Builder(file).build()
