package com.alexander.astrafiles.core

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Desktop
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists

class FileManagerState(startDirectory: Path, private val scope: CoroutineScope) {

    var currentDirectory by mutableStateOf(startDirectory)
        private set

    val entries: SnapshotStateList<FileEntry> = mutableStateListOf()

    var isLoading by mutableStateOf(false)
        private set

    val selection: SnapshotStateList<Path> = mutableStateListOf()

    var viewMode by mutableStateOf(ViewMode.Icons)
    var sortField by mutableStateOf(SortField.Name)
    var sortAscending by mutableStateOf(true)
    var showHidden by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var iconSize by mutableStateOf(72)

    var statusMessage by mutableStateOf<String?>(null)
        private set

    var pendingError by mutableStateOf<String?>(null)
        private set

    var justCreatedPath by mutableStateOf<Path?>(null)
        private set

    val favorites: List<FavoriteLocation> = FileSystemRepository.favorites()
    val volumes: List<VolumeEntry> = FileSystemRepository.volumes()

    private val backStack = mutableListOf<Path>()
    private val forwardStack = mutableListOf<Path>()
    private var clipboard: ClipboardContent? = null
    private var refreshGeneration = 0

    val canGoBack: Boolean get() = backStack.isNotEmpty()
    val canGoForward: Boolean get() = forwardStack.isNotEmpty()

    val visibleEntries: List<FileEntry> by derivedStateOf {
        val filtered = if (searchQuery.isBlank()) {
            entries
        } else {
            entries.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
        val comparator = comparatorFor(sortField)
        val sorted = filtered.sortedWith(comparator)
        if (sortAscending) sorted else sorted.asReversed()
    }

    init {
        refresh()
    }

    private fun comparatorFor(field: SortField): Comparator<FileEntry> {
        val base: Comparator<FileEntry> = when (field) {
            SortField.Name -> compareBy { it.name.lowercase() }
            SortField.DateModified -> compareBy { it.modifiedAt }
            SortField.Size -> compareBy { it.sizeBytes }
            SortField.Kind -> compareBy { it.kind.name }
        }
        return compareByDescending<FileEntry> { it.isDirectory }.then(base)
    }

    fun refresh() {
        val generation = ++refreshGeneration
        val directory = currentDirectory
        val includeHidden = showHidden
        isLoading = true
        scope.launch {
            val loaded = withContext(Dispatchers.IO) {
                FileSystemRepository.listDirectory(directory, includeHidden)
            }
            if (generation == refreshGeneration) {
                entries.clear()
                entries.addAll(loaded)
                isLoading = false
            }
        }
    }

    fun toggleHidden() {
        showHidden = !showHidden
        refresh()
    }

    fun navigateTo(path: Path) {
        if (!Files.isDirectory(path)) return
        backStack.add(currentDirectory)
        forwardStack.clear()
        currentDirectory = path
        selection.clear()
        refresh()
    }

    fun goBack() {
        val previous = backStack.removeLastOrNull() ?: return
        forwardStack.add(currentDirectory)
        currentDirectory = previous
        selection.clear()
        refresh()
    }

    fun goForward() {
        val next = forwardStack.removeLastOrNull() ?: return
        backStack.add(currentDirectory)
        currentDirectory = next
        selection.clear()
        refresh()
    }

    fun goUp() {
        val parent = currentDirectory.parent ?: return
        navigateTo(parent)
    }

    fun select(entry: FileEntry, additive: Boolean) {
        if (additive) {
            if (selection.contains(entry.path)) {
                selection.remove(entry.path)
            } else {
                selection.add(entry.path)
            }
        } else {
            selection.clear()
            selection.add(entry.path)
        }
    }

    fun clearSelection() {
        selection.clear()
    }

    fun createFolder() {
        val name = FileSystemRepository.uniqueName(currentDirectory, "New Folder")
        FileSystemRepository.createFolder(currentDirectory, name).fold(
            onSuccess = { created ->
                refresh()
                justCreatedPath = created
                statusMessage = null
            },
            onFailure = { pendingError = it.message ?: "Failed to create folder" }
        )
    }

    fun createFile() {
        val name = FileSystemRepository.uniqueName(currentDirectory, "New File", "txt")
        FileSystemRepository.createFile(currentDirectory, name).fold(
            onSuccess = { created ->
                refresh()
                justCreatedPath = created
                statusMessage = null
            },
            onFailure = { pendingError = it.message ?: "Failed to create file" }
        )
    }

    fun consumeJustCreated() {
        justCreatedPath = null
    }

    fun rename(entry: FileEntry, newName: String) {
        if (newName.isBlank() || newName == entry.name) return
        FileSystemRepository.rename(entry.path, newName).fold(
            onSuccess = { refresh() },
            onFailure = { pendingError = it.message ?: "Failed to rename" }
        )
    }

    fun moveSelectionToTrash() {
        val paths = selection.toList()
        if (paths.isEmpty()) return
        FileSystemRepository.moveToTrash(paths).fold(
            onSuccess = {
                selection.clear()
                refresh()
            },
            onFailure = { pendingError = it.message ?: "Failed to move to Trash" }
        )
    }

    fun copySelection() {
        if (selection.isEmpty()) return
        clipboard = ClipboardContent(selection.toList(), cut = false)
        statusMessage = "${selection.size} item(s) copied"
    }

    fun cutSelection() {
        if (selection.isEmpty()) return
        clipboard = ClipboardContent(selection.toList(), cut = true)
        statusMessage = "${selection.size} item(s) cut"
    }

    fun pasteIntoCurrentDirectory() {
        val content = clipboard ?: return
        val existing = content.paths.filter { it.exists() }
        if (existing.isEmpty()) return
        val result = if (content.cut) {
            FileSystemRepository.moveInto(existing, currentDirectory)
        } else {
            FileSystemRepository.copyInto(existing, currentDirectory)
        }
        result.fold(
            onSuccess = {
                if (content.cut) clipboard = null
                refresh()
            },
            onFailure = { pendingError = it.message ?: "Failed to paste" }
        )
    }

    fun open(entry: FileEntry) {
        if (entry.isDirectory) {
            navigateTo(entry.path)
            return
        }
        scope.launch {
            val failed = withContext(Dispatchers.IO) {
                try {
                    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                        Desktop.getDesktop().open(entry.path.toFile())
                    } else {
                        ProcessBuilder("xdg-open", entry.path.toString()).start()
                    }
                    false
                } catch (_: Exception) {
                    try {
                        ProcessBuilder("xdg-open", entry.path.toString()).start()
                        false
                    } catch (_: Exception) {
                        true
                    }
                }
            }
            if (failed) {
                pendingError = "Unable to open \"${entry.name}\""
            }
        }
    }

    fun dismissError() {
        pendingError = null
    }

    private data class ClipboardContent(val paths: List<Path>, val cut: Boolean)
}
