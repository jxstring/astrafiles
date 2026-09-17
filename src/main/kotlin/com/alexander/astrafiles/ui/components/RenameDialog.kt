package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import com.alexander.astrafiles.ui.theme.FinderPalette

@Composable
fun RenameDialog(initialName: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    var name by remember(initialName) { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FinderPalette.contentBackground,
        shape = RoundedCornerShape(10.dp),
        title = { Text("Rename", color = FinderPalette.textPrimary) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = FinderPalette.textPrimary,
                    unfocusedTextColor = FinderPalette.textPrimary,
                    focusedBorderColor = FinderPalette.accent,
                    unfocusedBorderColor = FinderPalette.borderSubtle,
                    cursorColor = FinderPalette.accent
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(name) }) {
                Text("Rename", color = FinderPalette.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FinderPalette.textSecondary)
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FinderPalette.contentBackground,
        shape = RoundedCornerShape(10.dp),
        title = { Text(title, color = FinderPalette.textPrimary) },
        text = { Text(message, color = FinderPalette.textSecondary) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmLabel, color = FinderPalette.accent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = FinderPalette.textSecondary)
            }
        }
    )
}
