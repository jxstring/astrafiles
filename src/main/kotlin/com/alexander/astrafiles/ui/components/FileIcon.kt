package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.automirrored.filled.TextSnippet
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.alexander.astrafiles.core.FileKind
import com.alexander.astrafiles.ui.theme.FinderPalette

private fun iconFor(kind: FileKind): ImageVector = when (kind) {
    FileKind.Folder -> Icons.Filled.Folder
    FileKind.Image -> Icons.Filled.Image
    FileKind.Document -> Icons.Filled.Description
    FileKind.Spreadsheet -> Icons.Filled.TableChart
    FileKind.Archive -> Icons.Filled.Archive
    FileKind.Audio -> Icons.Filled.Audiotrack
    FileKind.Video -> Icons.Filled.Movie
    FileKind.Code -> Icons.Filled.Code
    FileKind.Executable -> Icons.Filled.Terminal
    FileKind.PlainText -> Icons.AutoMirrored.Filled.TextSnippet
    FileKind.Unknown -> Icons.AutoMirrored.Filled.InsertDriveFile
}

private fun tintFor(kind: FileKind): Color = when (kind) {
    FileKind.Folder -> FinderPalette.iconTint
    else -> FinderPalette.textSecondary
}

@Composable
fun FileIcon(kind: FileKind, size: Int, modifier: Modifier = Modifier) {
    Icon(
        imageVector = iconFor(kind),
        contentDescription = null,
        tint = tintFor(kind),
        modifier = modifier.size(size.dp)
    )
}

@Composable
fun FileIconBadge(kind: FileKind, modifier: Modifier = Modifier) {
    Icon(
        imageVector = iconFor(kind),
        contentDescription = null,
        tint = tintFor(kind),
        modifier = modifier.size(18.dp)
    )
}
