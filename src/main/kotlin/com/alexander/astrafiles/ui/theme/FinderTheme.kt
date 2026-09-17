package com.alexander.astrafiles.ui.theme

import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
private fun currentColorScheme(): ColorScheme {
    val palette = FinderPalette
    return if (palette.isDark) {
        darkColorScheme(
            primary = palette.accent,
            onPrimary = palette.contentBackground,
            primaryContainer = palette.accent,
            onPrimaryContainer = palette.contentBackground,
            secondary = palette.accent,
            onSecondary = palette.contentBackground,
            secondaryContainer = palette.accent,
            onSecondaryContainer = palette.contentBackground,
            background = palette.contentBackground,
            onBackground = palette.textPrimary,
            surface = palette.contentBackground,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.toolbarBackground,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.borderSubtle,
            outlineVariant = palette.borderSubtle,
            error = Color(0xFFFF6B6B),
            onError = palette.contentBackground,
            surfaceContainer = palette.toolbarBackground,
            surfaceContainerLow = palette.windowBackground,
            surfaceContainerHigh = palette.titleBarBackground,
            surfaceContainerHighest = palette.titleBarBackground
        )
    } else {
        lightColorScheme(
            primary = palette.accent,
            onPrimary = palette.contentBackground,
            primaryContainer = palette.accent,
            onPrimaryContainer = palette.contentBackground,
            secondary = palette.accent,
            onSecondary = palette.contentBackground,
            secondaryContainer = palette.accent,
            onSecondaryContainer = palette.contentBackground,
            background = palette.contentBackground,
            onBackground = palette.textPrimary,
            surface = palette.contentBackground,
            onSurface = palette.textPrimary,
            surfaceVariant = palette.toolbarBackground,
            onSurfaceVariant = palette.textSecondary,
            outline = palette.borderSubtle,
            outlineVariant = palette.borderSubtle,
            error = Color(0xFFD32F2F),
            onError = palette.contentBackground,
            surfaceContainer = palette.toolbarBackground,
            surfaceContainerLow = palette.windowBackground,
            surfaceContainerHigh = palette.titleBarBackground,
            surfaceContainerHighest = palette.titleBarBackground
        )
    }
}

private val finderShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(10.dp),
    large = RoundedCornerShape(14.dp),
    extraLarge = RoundedCornerShape(18.dp)
)

private val finderTypography = Typography(
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Normal),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 12.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 11.sp, fontWeight = FontWeight.Medium),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 17.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 15.sp, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 13.sp, fontWeight = FontWeight.Medium)
)

object FlatPressIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode {
        return FlatPressIndicationNode(interactionSource)
    }

    override fun equals(other: Any?): Boolean = other is FlatPressIndication
    override fun hashCode(): Int = System.identityHashCode(this)
}

private class FlatPressIndicationNode(private val interactionSource: InteractionSource) : Modifier.Node(), DrawModifierNode {
    private var pressed by mutableStateOf(false)

    override fun onAttach() {
        coroutineScope.launch {
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> pressed = true
                    is PressInteraction.Release, is PressInteraction.Cancel -> pressed = false
                    else -> Unit
                }
            }
        }
    }

    override fun ContentDrawScope.draw() {
        drawContent()
        if (pressed) {
            drawRect(color = FinderPalette.hoverOverlay)
        }
    }
}

@Composable
fun FinderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = currentColorScheme(),
        shapes = finderShapes,
        typography = finderTypography,
        content = content
    )
}
