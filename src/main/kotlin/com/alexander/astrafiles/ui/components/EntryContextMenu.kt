package com.alexander.astrafiles.ui.components

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.alexander.astrafiles.ui.theme.FinderPalette

data class ContextMenuAction(val label: String, val onSelect: () -> Unit)

@Composable
fun EntryContextMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    actions: List<ContextMenuAction>
) {
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        for (action in actions) {
            DropdownMenuItem(
                text = { Text(action.label, color = FinderPalette.textPrimary) },
                onClick = {
                    onDismiss()
                    action.onSelect()
                }
            )
        }
    }
}
