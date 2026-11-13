package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val RoyalLightColorScheme = lightColorScheme(
    primary = RoyalBluePrimary,
    onPrimary = Color.White,
    primaryContainer = RoyalSkyLight,
    onPrimaryContainer = RoyalBlueDeep,
    secondary = RoyalBlueBright,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEBFF),
    onSecondaryContainer = RoyalBlueDeep,
    tertiary = GoldAccent,
    onTertiary = Color.White,
    background = IceBackground,
    onBackground = TextPrimary,
    surface = CardWhite,
    onSurface = TextPrimary,
    surfaceVariant = SoftSlate,
    onSurfaceVariant = TextSecondary,
    outline = RoyalSkyBorder,
    error = ErrorRed,
    onError = Color.White
)

private val RoyalShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(22.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun RoyalBulletTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = RoyalLightColorScheme,
        typography = RoyalBulletTypography,
        shapes = RoyalShapes,
        content = content
    )
}
