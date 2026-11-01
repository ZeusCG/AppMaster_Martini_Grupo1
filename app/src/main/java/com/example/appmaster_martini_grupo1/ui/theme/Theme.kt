package com.example.appmaster_martini_grupo1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val MasterMartiniColorScheme = lightColorScheme(
    primary = MartiniGold,
    onPrimary = MartiniWhite,
    primaryContainer = MartiniGoldLight,
    onPrimaryContainer = MartiniGoldDark,
    secondary = MartiniGreenDark,
    onSecondary = MartiniWhite,
    secondaryContainer = MartiniGreenLight,
    onSecondaryContainer = MartiniGreenDark,
    tertiary = MartiniGreenAccent,
    onTertiary = MartiniWhite,
    error = MartiniRed,
    onError = MartiniWhite,
    background = MartiniWhite,
    onBackground = MartiniGreenDark,
    surface = MartiniWhite,
    onSurface = MartiniGreenDark,
    surfaceVariant = MartiniSurfaceVariant,
    onSurfaceVariant = MartiniOnSurfaceVariant,
    outline = MartiniOutline
)

@Composable
fun AppMaster_Martini_Grupo1Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MasterMartiniColorScheme,
        typography = Typography,
        content = content
    )
}