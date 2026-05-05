package com.notes.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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


private val DarkColorScheme = darkColorScheme(
    primary = SandyBrown,
    onPrimary = PrussianBlue,
    primaryContainer = PrussianBlue,
    onPrimaryContainer = VanillaCustard,
    secondary = LightGold,
    onSecondary = PrussianBlue,
    tertiary = FieryTerracotta,
    onTertiary = VanillaCustard,
    background = Color(0xFF121212),
    onBackground = VanillaCustard,
    surface = Color(0xFF1E1E1E),
    onSurface = VanillaCustard,
    error = FieryTerracotta,
    onError = VanillaCustard,
    outline = VanillaCustard.copy(alpha = 0.15f)
)

@Composable
fun NotesTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
