package com.alexander.astrafiles.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.rememberWindowState
import com.alexander.astrafiles.core.FileSystemRepository
import com.alexander.astrafiles.ui.theme.FinderPalette
import com.alexander.astrafiles.ui.theme.FinderTheme
import java.nio.file.Path
import kotlin.io.path.name

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun TextEditorWindow(path: Path, onClose: () -> Unit) {
    val windowState = rememberWindowState(size = DpSize(900.dp, 640.dp))
    var content by remember { mutableStateOf(FileSystemRepository.loadText(path).getOrDefault("")) }
    var savedContent by remember { mutableStateOf(content) }
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val isDirty = content != savedContent

    fun save() {
        FileSystemRepository.saveText(path, content).fold(
            onSuccess = { savedContent = content },
            onFailure = { errorMessage = it.message ?: "Failed to save file" }
        )
    }

    Window(
        onCloseRequest = {
            if (isDirty) showUnsavedDialog = true else onClose()
        },
        state = windowState,
        title = path.name
    ) {
        FinderTheme {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(FinderPalette.contentBackground)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown && event.isCtrlPressed && event.key == Key.S) {
                            save()
                            true
                        } else {
                            false
                        }
                    }
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(FinderPalette.toolbarBackground)
                        .border(width = 1.dp, color = FinderPalette.borderSubtle)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = path.name,
                        color = FinderPalette.textPrimary,
                        fontSize = 13.sp
                    )
                    if (isDirty) {
                        Icon(
                            imageVector = Icons.Filled.Circle,
                            contentDescription = "Unsaved changes",
                            tint = FinderPalette.accent,
                            modifier = Modifier.size(8.dp)
                        )
                    }
                    Box(modifier = Modifier.weight(1f))
                    IconButton(onClick = { save() }, enabled = isDirty) {
                        Icon(
                            imageVector = Icons.Filled.Save,
                            contentDescription = "Save",
                            tint = if (isDirty) FinderPalette.accent else FinderPalette.textTertiary
                        )
                    }
                }

                BasicTextField(
                    value = content,
                    onValueChange = { content = it },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = FinderPalette.textPrimary
                    ),
                    cursorBrush = SolidColor(FinderPalette.accent),
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                )
            }

            if (showUnsavedDialog) {
                AlertDialog(
                    onDismissRequest = { showUnsavedDialog = false },
                    containerColor = FinderPalette.contentBackground,
                    shape = RoundedCornerShape(10.dp),
                    title = { Text("Unsaved Changes", color = FinderPalette.textPrimary) },
                    text = {
                        Text(
                            "Do you want to save the changes you made to \"${path.name}\"?",
                            color = FinderPalette.textSecondary
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            save()
                            showUnsavedDialog = false
                            onClose()
                        }) {
                            Text("Save", color = FinderPalette.accent)
                        }
                    },
                    dismissButton = {
                        Row {
                            TextButton(onClick = { showUnsavedDialog = false }) {
                                Text("Cancel", color = FinderPalette.textSecondary)
                            }
                            TextButton(onClick = onClose) {
                                Text("Discard", color = FinderPalette.textSecondary)
                            }
                        }
                    }
                )
            }

            errorMessage?.let { message ->
                AlertDialog(
                    onDismissRequest = { errorMessage = null },
                    containerColor = FinderPalette.contentBackground,
                    shape = RoundedCornerShape(10.dp),
                    title = { Text("Error", color = FinderPalette.textPrimary) },
                    text = { Text(message, color = FinderPalette.textSecondary) },
                    confirmButton = {
                        TextButton(onClick = { errorMessage = null }) {
                            Text("OK", color = FinderPalette.accent)
                        }
                    }
                )
            }
        }
    }
}
