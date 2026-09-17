package com.alexander.astrafiles.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

private class PaletteColors(
    val windowBackground: Color,
    val contentBackground: Color,
    val toolbarBackground: Color,
    val titleBarBackground: Color,
    val sidebarBackground: Color,
    val hoverOverlay: Color,
    val selectionOverlay: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val iconTint: Color
)

object FinderPalette {
    var isDark by mutableStateOf(false)

    private val light = PaletteColors(
        windowBackground = Color(0xFFF6F6F6),
        contentBackground = Color(0xFFFFFFFF),
        toolbarBackground = Color(0xFFECECEC),
        titleBarBackground = Color(0xFFE4E4E4),
        sidebarBackground = Color(0xFFEDEDF0),
        hoverOverlay = Color(0x14000000),
        selectionOverlay = Color(0x332D7BF6),
        borderSubtle = Color(0xFFD9D9D9),
        textPrimary = Color(0xFF1D1D1F),
        textSecondary = Color(0xFF5B5B5E),
        textTertiary = Color(0xFF9A9A9C),
        accent = Color(0xFF2D7BF6),
        iconTint = Color(0xFF5B98F5)
    )

    private val dark = PaletteColors(
        windowBackground = Color(0xFF232323),
        contentBackground = Color(0xFF1E1E1E),
        toolbarBackground = Color(0xFF2A2A2A),
        titleBarBackground = Color(0xFF2E2E2E),
        sidebarBackground = Color(0xFF262626),
        hoverOverlay = Color(0x1AFFFFFF),
        selectionOverlay = Color(0x4D3D8BFF),
        borderSubtle = Color(0xFF3A3A3A),
        textPrimary = Color(0xFFF0F0F0),
        textSecondary = Color(0xFFB0B0B0),
        textTertiary = Color(0xFF7A7A7A),
        accent = Color(0xFF3D8BFF),
        iconTint = Color(0xFF6FA6FF)
    )

    private val current: PaletteColors get() = if (isDark) dark else light

    val windowBackground: Color get() = current.windowBackground
    val contentBackground: Color get() = current.contentBackground
    val toolbarBackground: Color get() = current.toolbarBackground
    val titleBarBackground: Color get() = current.titleBarBackground
    val sidebarBackground: Color get() = current.sidebarBackground
    val hoverOverlay: Color get() = current.hoverOverlay
    val selectionOverlay: Color get() = current.selectionOverlay
    val borderSubtle: Color get() = current.borderSubtle
    val textPrimary: Color get() = current.textPrimary
    val textSecondary: Color get() = current.textSecondary
    val textTertiary: Color get() = current.textTertiary
    val accent: Color get() = current.accent
    val iconTint: Color get() = current.iconTint

    val trafficRed = Color(0xFFFF5F57)
    val trafficYellow = Color(0xFFFEBC2E)
    val trafficGreen = Color(0xFF28C840)
}
