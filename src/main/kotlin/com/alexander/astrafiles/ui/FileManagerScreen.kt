package com.alexander.astrafiles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType
import com.alexander.astrafiles.core.FileEntry
import com.alexander.astrafiles.core.FileManagerState
import com.alexander.astrafiles.core.ViewMode
import com.alexander.astrafiles.ui.components.ConfirmationDialog
import com.alexander.astrafiles.ui.components.ContextMenuAction
import com.alexander.astrafiles.ui.components.EntryContextMenu
import com.alexander.astrafiles.ui.components.FileGridView
import com.alexander.astrafiles.ui.components.FileListView
import com.alexander.astrafiles.ui.components.GetInfoDialog
import com.alexander.astrafiles.ui.components.PathBar
import com.alexander.astrafiles.ui.components.RenameDialog
import com.alexander.astrafiles.ui.components.Sidebar
import com.alexander.astrafiles.ui.components.StatusBar
import com.alexander.astrafiles.ui.components.Toolbar
import com.alexander.astrafiles.ui.theme.FinderPalette

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FileManagerScreen(
    state: FileManagerState,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onOpenTextFile: (FileEntry) -> Unit
) {
    var renamingEntry by remember { mutableStateOf<FileEntry?>(null) }
    var infoEntry by remember { mutableStateOf<FileEntry?>(null) }
    var pendingTrashConfirmation by remember { mutableStateOf(false) }
    var contextMenuEntry by remember { mutableStateOf<FileEntry?>(null) }
    val focusRequester = remember { FocusRequester() }

    fun openEntry(entry: FileEntry) {
        if (!entry.isDirectory && entry.isTextEditable) {
            onOpenTextFile(entry)
        } else {
            state.open(entry)
        }
    }

    LaunchedEffect(state.justCreatedPath) {
        val createdPath = state.justCreatedPath ?: return@LaunchedEffect
        val createdEntry = state.entries.firstOrNull { it.path == createdPath }
        if (createdEntry != null) {
            renamingEntry = createdEntry
        }
        state.consumeJustCreated()
    }

    Surface(modifier = Modifier.fillMaxSize(), color = FinderPalette.contentBackground) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onKeyEvent { event ->
                    if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                    when (event.key) {
                        Key.Delete, Key.Backspace -> {
                            if (state.selection.isNotEmpty()) {
                                pendingTrashConfirmation = true
                                true
                            } else false
                        }
                        Key.Escape -> {
                            state.clearSelection()
                            true
                        }
                        Key.F2 -> {
                            val target = state.visibleEntries.firstOrNull { state.selection.contains(it.path) }
                            if (target != null) {
                                renamingEntry = target
                                true
                            } else false
                        }
                        else -> false
                    }
                }
        ) {
            Sidebar(
                favorites = state.favorites,
                volumes = state.volumes,
                currentDirectory = state.currentDirectory,
                onNavigate = { state.navigateTo(it) }
            )
            Column(modifier = Modifier.fillMaxSize().background(FinderPalette.contentBackground)) {
                Toolbar(
                    canGoBack = state.canGoBack,
                    canGoForward = state.canGoForward,
                    viewMode = state.viewMode,
                    showHidden = state.showHidden,
                    searchQuery = state.searchQuery,
                    iconSize = state.iconSize,
                    isDarkTheme = isDarkTheme,
                    onBack = state::goBack,
                    onForward = state::goForward,
                    onSetViewMode = { state.viewMode = it },
                    onToggleHidden = state::toggleHidden,
                    onNewFolder = state::createFolder,
                    onNewFile = state::createFile,
                    onIconSizeChange = { state.iconSize = it },
                    onSearchQueryChange = { state.searchQuery = it },
                    onToggleTheme = onToggleTheme
                )
                PathBar(currentDirectory = state.currentDirectory, onNavigate = { state.navigateTo(it) })

                Column(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                        if (state.viewMode == ViewMode.Icons) {
                            FileGridView(
                                entries = state.visibleEntries,
                                selection = state.selection,
                                iconSize = state.iconSize,
                                onSelect = { entry, additive -> state.select(entry, additive) },
                                onOpen = { openEntry(it) },
                                onContextMenu = { entry ->
                                    state.select(entry, additive = false)
                                    contextMenuEntry = entry
                                }
                            )
                        } else {
                            FileListView(
                                entries = state.visibleEntries,
                                selection = state.selection,
                                sortField = state.sortField,
                                sortAscending = state.sortAscending,
                                onSelect = { entry, additive -> state.select(entry, additive) },
                                onOpen = { openEntry(it) },
                                onContextMenu = { entry ->
                                    state.select(entry, additive = false)
                                    contextMenuEntry = entry
                                },
                                onSortChange = { field ->
                                    if (state.sortField == field) {
                                        state.sortAscending = !state.sortAscending
                                    } else {
                                        state.sortField = field
                                        state.sortAscending = true
                                    }
                                }
                            )
                        }

                        contextMenuEntry?.let { entry ->
                            EntryContextMenu(
                                expanded = true,
                                onDismiss = { contextMenuEntry = null },
                                actions = listOf(
                                    ContextMenuAction("Open") { openEntry(entry) },
                                    ContextMenuAction("Rename") { renamingEntry = entry },
                                    ContextMenuAction("Get Info") { infoEntry = entry },
                                    ContextMenuAction("Copy") { state.copySelection() },
                                    ContextMenuAction("Cut") { state.cutSelection() },
                                    ContextMenuAction("Paste") { state.pasteIntoCurrentDirectory() },
                                    ContextMenuAction("Move to Trash") { pendingTrashConfirmation = true }
                                )
                            )
                        }
                    }
                    StatusBar(
                        itemCount = state.visibleEntries.size,
                        selectedCount = state.selection.size,
                        statusMessage = state.statusMessage
                    )
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    renamingEntry?.let { entry ->
        RenameDialog(
            initialName = entry.name,
            onConfirm = { newName ->
                state.rename(entry, newName)
                renamingEntry = null
            },
            onDismiss = { renamingEntry = null }
        )
    }

    infoEntry?.let { entry ->
        GetInfoDialog(entry = entry, onDismiss = { infoEntry = null })
    }

    if (pendingTrashConfirmation) {
        ConfirmationDialog(
            title = "Move to Trash",
            message = "Are you sure you want to move ${state.selection.size} item(s) to the Trash?",
            confirmLabel = "Move to Trash",
            onConfirm = {
                state.moveSelectionToTrash()
                pendingTrashConfirmation = false
            },
            onDismiss = { pendingTrashConfirmation = false }
        )
    }

    state.pendingError?.let { error ->
        ConfirmationDialog(
            title = "Error",
            message = error,
            confirmLabel = "OK",
            onConfirm = { state.dismissError() },
            onDismiss = { state.dismissError() }
        )
    }
}
