package com.alexander.astrafiles

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.alexander.astrafiles.core.FileManagerState
import com.alexander.astrafiles.core.FileSystemRepository
import com.alexander.astrafiles.ui.FileManagerScreen
import com.alexander.astrafiles.ui.TextEditorWindow
import com.alexander.astrafiles.ui.theme.FinderPalette
import com.alexander.astrafiles.ui.theme.FinderTheme
import java.nio.file.Path

fun main() = application {
    val windowState = rememberWindowState(
        position = WindowPosition.Aligned(Alignment.Center),
        size = DpSize(1080.dp, 680.dp)
    )
    val scope = rememberCoroutineScope()
    val fileManagerState = remember { FileManagerState(FileSystemRepository.homeDirectory(), scope) }
    val openEditors = remember { mutableStateListOf<Path>() }

    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Files",
        resizable = true
    ) {
        FinderTheme {
            FileManagerScreen(
                state = fileManagerState,
                isDarkTheme = FinderPalette.isDark,
                onToggleTheme = { FinderPalette.isDark = !FinderPalette.isDark },
                onOpenTextFile = { entry ->
                    if (!openEditors.contains(entry.path)) {
                        openEditors.add(entry.path)
                    }
                }
            )
        }
    }

    for (path in openEditors) {
        key(path.toString()) {
            TextEditorWindow(path = path, onClose = { openEditors.remove(path) })
        }
    }
}
