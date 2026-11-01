package com.example.appmaster_martini_grupo1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MartiniPrimaryDark,
    onPrimary = MartiniOnPrimaryDark,
    primaryContainer = MartiniPrimaryContainerDark,
    onPrimaryContainer = MartiniOnPrimaryContainerDark,
    secondary = MartiniSecondaryDark,
    onSecondary = MartiniOnSecondaryDark,
    background = MartiniBackgroundDark,
    onBackground = MartiniOnBackgroundDark,
    surface = MartiniBackgroundDark,
    onSurface = MartiniOnBackgroundDark,
    error = MartiniErrorDark,
    onError = MartiniOnErrorDark,
    errorContainer = MartiniErrorContainerDark,
    onErrorContainer = MartiniOnErrorContainerDark
)

private val LightColorScheme = lightColorScheme(
    primary = MartiniPrimary,
    onPrimary = MartiniOnPrimary,
    primaryContainer = MartiniPrimaryContainer,
    onPrimaryContainer = MartiniOnPrimaryContainer,
    secondary = MartiniSecondary,
    onSecondary = MartiniOnSecondary,
    background = MartiniBackground,
    onBackground = MartiniOnBackground,
    surface = MartiniBackground,
    onSurface = MartiniOnBackground,
    error = MartiniError,
    onError = MartiniOnError,
    errorContainer = MartiniErrorContainer,
    onErrorContainer = MartiniOnErrorContainer
)

@Composable
fun AppMaster_Martini_Grupo1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color se deja APAGADO por defecto para conservar el contraste de la marca (RNF-02).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}