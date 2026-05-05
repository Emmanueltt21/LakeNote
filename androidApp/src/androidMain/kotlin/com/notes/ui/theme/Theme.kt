package com.notes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = SandyBrown,
    onPrimary = PrussianBlue,
    primaryContainer = VanillaCustard,
    onPrimaryContainer = PrussianBlue,
    secondary = LightGold,
    onSecondary = PrussianBlue,
    tertiary = FieryTerracotta,
    onTertiary = VanillaCustard,
    background = VanillaCustard,
    onBackground = PrussianBlue,
    surface = VanillaCustard,
    onSurface = PrussianBlue,
    error = FieryTerracotta,
    onError = VanillaCustard,
    outline = PrussianBlue.copy(alpha = 0.15f)
)

@Composable
fun NotesTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
