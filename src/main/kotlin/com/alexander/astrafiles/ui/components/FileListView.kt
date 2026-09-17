package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.onClick
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerButton
import androidx.compose.foundation.PointerMatcher
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexander.astrafiles.core.FileEntry
import com.alexander.astrafiles.core.SortField
import com.alexander.astrafiles.core.formatFileSize
import com.alexander.astrafiles.core.formatModifiedDate
import com.alexander.astrafiles.ui.theme.FinderPalette
import java.nio.file.Path

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
fun FileListView(
    entries: List<FileEntry>,
    selection: List<Path>,
    sortField: SortField,
    sortAscending: Boolean,
    onSelect: (FileEntry, Boolean) -> Unit,
    onOpen: (FileEntry) -> Unit,
    onContextMenu: (FileEntry) -> Unit,
    onSortChange: (SortField) -> Unit
) {
    Column {
        ColumnHeader(sortField = sortField, sortAscending = sortAscending, onSortChange = onSortChange)
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(entries, key = { it.path.toString() }) { entry ->
                ListRow(
                    entry = entry,
                    selected = selection.contains(entry.path),
                    onSelect = { additive -> onSelect(entry, additive) },
                    onOpen = { onOpen(entry) },
                    onContextMenu = { onContextMenu(entry) }
                )
            }
        }
    }
}

@Composable
private fun ColumnHeader(sortField: SortField, sortAscending: Boolean, onSortChange: (SortField) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(FinderPalette.toolbarBackground)
            .border(width = 1.dp, color = FinderPalette.borderSubtle)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        HeaderCell("Name", SortField.Name, sortField, sortAscending, onSortChange, weight = 1f)
        HeaderCell("Date Modified", SortField.DateModified, sortField, sortAscending, onSortChange, width = 150.dp)
        HeaderCell("Size", SortField.Size, sortField, sortAscending, onSortChange, width = 90.dp)
        HeaderCell("Kind", SortField.Kind, sortField, sortAscending, onSortChange, width = 110.dp)
    }
}

@Composable
private fun RowScope.HeaderCell(
    label: String,
    field: SortField,
    currentField: SortField,
    ascending: Boolean,
    onSortChange: (SortField) -> Unit,
    weight: Float? = null,
    width: androidx.compose.ui.unit.Dp? = null
) {
    val modifier = if (weight != null) Modifier.weight(weight) else Modifier.width(width ?: 100.dp)
    Row(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onSortChange(field) },
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (field == currentField) FinderPalette.textPrimary else FinderPalette.textSecondary
        )
        if (field == currentField) {
            Text(
                text = if (ascending) "\u25B2" else "\u25BC",
                fontSize = 9.sp,
                color = FinderPalette.textSecondary
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
private fun ListRow(
    entry: FileEntry,
    selected: Boolean,
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
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
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FileIconBadge(kind = entry.kind)
            Text(
                text = entry.name,
                fontSize = 13.sp,
                color = FinderPalette.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            text = formatModifiedDate(entry.modifiedAt),
            fontSize = 12.sp,
            color = FinderPalette.textSecondary,
            modifier = Modifier.width(150.dp)
        )
        Text(
            text = if (entry.isDirectory) "--" else formatFileSize(entry.sizeBytes),
            fontSize = 12.sp,
            color = FinderPalette.textSecondary,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = if (entry.isDirectory) "Folder" else entry.kind.name,
            fontSize = 12.sp,
            color = FinderPalette.textSecondary,
            modifier = Modifier.width(110.dp)
        )
    }
}
