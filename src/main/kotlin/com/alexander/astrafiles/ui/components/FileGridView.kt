package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.foundation.PointerMatcher
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexander.astrafiles.core.FileEntry
import com.alexander.astrafiles.ui.theme.FinderPalette
import java.nio.file.Path

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FileGridView(
    entries: List<FileEntry>,
    selection: List<Path>,
    iconSize: Int,
    onSelect: (FileEntry, Boolean) -> Unit,
    onOpen: (FileEntry) -> Unit,
    onContextMenu: (FileEntry) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = (iconSize + 40).dp),
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        items(entries, key = { it.path.toString() }) { entry ->
            GridItem(
                entry = entry,
                selected = selection.contains(entry.path),
                iconSize = iconSize,
                onSelect = { additive -> onSelect(entry, additive) },
                onOpen = { onOpen(entry) },
                onContextMenu = { onContextMenu(entry) }
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun GridItem(
    entry: FileEntry,
    selected: Boolean,
    iconSize: Int,
    onSelect: (Boolean) -> Unit,
    onOpen: () -> Unit,
    onContextMenu: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val background = when {
        selected -> FinderPalette.selectionOverlay
        hovered -> FinderPalette.hoverOverlay
        else -> Color.Transparent
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .width((iconSize + 32).dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onSelect(false) },
                onDoubleClick = onOpen
            )
            .onClick(matcher = PointerMatcher.mouse(PointerButton.Secondary)) {
                onContextMenu()
            }
            .padding(8.dp)
    ) {
        FileIcon(kind = entry.kind, size = iconSize)
        Text(
            text = entry.name,
            fontSize = 12.sp,
            color = FinderPalette.textPrimary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
