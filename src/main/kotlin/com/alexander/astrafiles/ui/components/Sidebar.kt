package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.alexander.astrafiles.core.FavoriteIcon
import com.alexander.astrafiles.core.FavoriteLocation
import com.alexander.astrafiles.core.VolumeEntry
import com.alexander.astrafiles.ui.theme.FinderPalette
import java.nio.file.Path

private fun iconFor(favoriteIcon: FavoriteIcon): ImageVector = when (favoriteIcon) {
    FavoriteIcon.Home -> Icons.Filled.Home
    FavoriteIcon.Desktop -> Icons.Filled.Computer
    FavoriteIcon.Documents -> Icons.AutoMirrored.Filled.InsertDriveFile
    FavoriteIcon.Downloads -> Icons.Filled.Folder
    FavoriteIcon.Pictures -> Icons.Filled.Image
    FavoriteIcon.Music -> Icons.Filled.MusicNote
    FavoriteIcon.Videos -> Icons.Filled.Movie
}

@Composable
fun Sidebar(
    favorites: List<FavoriteLocation>,
    volumes: List<VolumeEntry>,
    currentDirectory: Path,
    onNavigate: (Path) -> Unit
) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .fillMaxWidth()
            .background(FinderPalette.sidebarBackground)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        SectionHeader("Favorites")
        for (favorite in favorites) {
            SidebarRow(
                label = favorite.label,
                icon = iconFor(favorite.icon),
                selected = currentDirectory == favorite.path,
                onClick = { onNavigate(favorite.path) }
            )
        }

        if (volumes.isNotEmpty()) {
            SectionHeader("Locations")
            for (volume in volumes) {
                SidebarRow(
                    label = volume.label,
                    icon = Icons.Filled.Computer,
                    selected = currentDirectory == volume.path,
                    onClick = { onNavigate(volume.path) }
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = FinderPalette.textTertiary,
        modifier = Modifier.padding(start = 8.dp, top = 10.dp, bottom = 4.dp)
    )
}

@Composable
private fun SidebarRow(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = when {
        selected -> FinderPalette.selectionOverlay
        hovered -> FinderPalette.hoverOverlay
        else -> androidx.compose.ui.graphics.Color.Transparent
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(background)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) FinderPalette.accent else FinderPalette.textSecondary,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (selected) FinderPalette.accent else FinderPalette.textPrimary
        )
    }
}
