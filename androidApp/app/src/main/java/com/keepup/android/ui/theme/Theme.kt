package com.keepup.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val ColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    background = Background,
    onBackground = OnBackground,
    surface = SurfaceVariant,
    onSurface = OnBackground,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnBackground
)

@Composable
fun KeepUpTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ColorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * Glass morphism effect modifier matching iOS design
 */
fun Modifier.glassMorphism() = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.1f),
                Color.White.copy(alpha = 0.05f)
            )
        ),
        shape = RoundedCornerShape(16.dp)
    )
    .border(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    )

/**
 * Dark gradient background matching iOS
 */
fun Modifier.darkGradientBackground() = this
    .background(
        brush = Brush.verticalGradient(
            colors = listOf(
                BackgroundStart,
                BackgroundEnd
            )
        )
    )
