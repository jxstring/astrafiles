package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreateNewFolder
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexander.astrafiles.core.ViewMode
import com.alexander.astrafiles.ui.theme.FinderPalette

@Composable
fun Toolbar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    viewMode: ViewMode,
    showHidden: Boolean,
    searchQuery: String,
    iconSize: Int,
    isDarkTheme: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onSetViewMode: (ViewMode) -> Unit,
    onToggleHidden: () -> Unit,
    onNewFolder: () -> Unit,
    onNewFile: () -> Unit,
    onIconSizeChange: (Int) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onToggleTheme: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(FinderPalette.toolbarBackground)
            .border(width = 1.dp, color = FinderPalette.borderSubtle)
            .padding(horizontal = 10.dp, vertical = 7.dp)
    ) {
        ToolbarIconButton(Icons.AutoMirrored.Filled.ArrowBack, "Back", enabled = canGoBack, onClick = onBack)
        ToolbarIconButton(Icons.AutoMirrored.Filled.ArrowForward, "Forward", enabled = canGoForward, onClick = onForward)

        Spacer(modifier = Modifier.width(6.dp))

        NewItemMenu(onNewFolder = onNewFolder, onNewFile = onNewFile)

        Spacer(modifier = Modifier.width(2.dp))

        ViewModeSegmentedControl(viewMode = viewMode, onSetViewMode = onSetViewMode)

        ToolbarIconButton(
            icon = Icons.Filled.VisibilityOff,
            description = "Toggle Hidden Files",
            enabled = true,
            tinted = showHidden,
            onClick = onToggleHidden
        )

        if (viewMode == ViewMode.Icons) {
            Spacer(modifier = Modifier.width(4.dp))
            IconSizeSlider(iconSize = iconSize, onIconSizeChange = onIconSizeChange)
        }

        Spacer(modifier = Modifier.weight(1f))
        SearchField(searchQuery, onSearchQueryChange, modifier = Modifier.width(220.dp))

        Spacer(modifier = Modifier.width(4.dp))

        ToolbarIconButton(
            icon = if (isDarkTheme) Icons.Filled.LightMode else Icons.Filled.DarkMode,
            description = if (isDarkTheme) "Switch to light appearance" else "Switch to dark appearance",
            enabled = true,
            onClick = onToggleTheme
        )
    }
}

@Composable
private fun IconSizeSlider(iconSize: Int, onIconSizeChange: (Int) -> Unit) {
    Slider(
        value = iconSize.toFloat(),
        onValueChange = { onIconSizeChange(it.toInt()) },
        valueRange = 48f..128f,
        colors = SliderDefaults.colors(
            thumbColor = FinderPalette.accent,
            activeTrackColor = FinderPalette.accent,
            inactiveTrackColor = FinderPalette.borderSubtle
        ),
        modifier = Modifier.width(90.dp)
    )
}

@Composable
private fun NewItemMenu(onNewFolder: () -> Unit, onNewFile: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        ToolbarIconButton(Icons.Filled.Add, "New", enabled = true, onClick = { expanded = true })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("New Folder") },
                leadingIcon = { Icon(Icons.Filled.CreateNewFolder, contentDescription = null) },
                onClick = {
                    expanded = false
                    onNewFolder()
                }
            )
            DropdownMenuItem(
                text = { Text("New File") },
                leadingIcon = { Icon(Icons.AutoMirrored.Filled.InsertDriveFile, contentDescription = null) },
                onClick = {
                    expanded = false
                    onNewFile()
                }
            )
        }
    }
}

@Composable
private fun ViewModeSegmentedControl(viewMode: ViewMode, onSetViewMode: (ViewMode) -> Unit) {
    Row(
        modifier = Modifier
            .background(FinderPalette.borderSubtle, RoundedCornerShape(6.dp))
            .padding(2.dp)
    ) {
        SegmentButton(
            icon = Icons.Filled.GridView,
            selected = viewMode == ViewMode.Icons,
            onClick = { onSetViewMode(ViewMode.Icons) }
        )
        SegmentButton(
            icon = Icons.AutoMirrored.Filled.ViewList,
            selected = viewMode == ViewMode.List,
            onClick = { onSetViewMode(ViewMode.List) }
        )
    }
}

@Composable
private fun SegmentButton(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(26.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(if (selected) FinderPalette.windowBackground else Color.Transparent)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) FinderPalette.accent else FinderPalette.textSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun ToolbarIconButton(
    icon: ImageVector,
    description: String,
    enabled: Boolean,
    tinted: Boolean = false,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .size(30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (hovered && enabled) FinderPalette.hoverOverlay else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick, enabled = enabled, interactionSource = interactionSource) {
            Icon(
                imageVector = icon,
                contentDescription = description,
                tint = when {
                    !enabled -> FinderPalette.textTertiary
                    tinted -> FinderPalette.accent
                    else -> FinderPalette.textPrimary
                },
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(FinderPalette.windowBackground, RoundedCornerShape(8.dp))
            .border(width = 1.dp, color = FinderPalette.borderSubtle, shape = RoundedCornerShape(8.dp))
            .padding(horizontal = 9.dp, vertical = 7.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = FinderPalette.textSecondary,
            modifier = Modifier.size(17.dp).padding(end = 7.dp)
        )
        Box(contentAlignment = Alignment.CenterStart) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 13.sp, color = FinderPalette.textPrimary),
                cursorBrush = SolidColor(FinderPalette.accent)
            )
            if (value.isEmpty()) {
                Text("Search", color = FinderPalette.textTertiary, fontSize = 13.sp)
            }
        }
    }
}
