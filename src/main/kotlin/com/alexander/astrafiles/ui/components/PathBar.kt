package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexander.astrafiles.ui.theme.FinderPalette
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PathBar(currentDirectory: Path, onNavigate: (Path) -> Unit) {
    var editing by remember { mutableStateOf(false) }
    var editedText by remember(currentDirectory) { mutableStateOf(currentDirectory.toString()) }
    val focusRequester = remember { FocusRequester() }

    fun commitEdit() {
        val candidate = runCatching { Paths.get(editedText) }.getOrNull()
        if (candidate != null && Files.isDirectory(candidate)) {
            onNavigate(candidate)
        }
        editing = false
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(FinderPalette.toolbarBackground)
            .border(width = 1.dp, color = FinderPalette.borderSubtle)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = !editing
            ) {
                editedText = currentDirectory.toString()
                editing = true
            }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        if (editing) {
            BasicTextField(
                value = editedText,
                onValueChange = { editedText = it },
                singleLine = true,
                textStyle = TextStyle(fontSize = 12.sp, color = FinderPalette.textPrimary),
                cursorBrush = SolidColor(FinderPalette.accent),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .onKeyEvent { event ->
                        if (event.type != KeyEventType.KeyDown) return@onKeyEvent false
                        when (event.key) {
                            Key.Enter -> {
                                commitEdit()
                                true
                            }
                            Key.Escape -> {
                                editing = false
                                true
                            }
                            else -> false
                        }
                    }
            )
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        } else {
            val segments = buildSegments(currentDirectory)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                for ((index, segment) in segments.withIndex()) {
                    if (index > 0) {
                        Text("/", fontSize = 12.sp, color = FinderPalette.textTertiary)
                    }
                    PathSegment(
                        label = segment.first,
                        onClick = { onNavigate(segment.second) }
                    )
                }
            }
        }
    }
}

private fun buildSegments(path: Path): List<Pair<String, Path>> {
    val absolute = path.toAbsolutePath().normalize()
    val segments = mutableListOf<Pair<String, Path>>()
    var current: Path? = absolute
    val chain = mutableListOf<Path>()
    while (current != null) {
        chain.add(current)
        current = current.parent
    }
    for (element in chain.asReversed()) {
        val label = element.fileName?.toString() ?: element.toString()
        segments.add(label to element)
    }
    return segments
}

@Composable
private fun PathSegment(label: String, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    Box(
        modifier = Modifier
            .background(
                if (hovered) FinderPalette.hoverOverlay else Color.Transparent,
                RoundedCornerShape(4.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(text = label, fontSize = 12.sp, color = FinderPalette.textSecondary)
    }
}
