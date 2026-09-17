package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexander.astrafiles.core.FileEntry
import com.alexander.astrafiles.core.formatFileSize
import com.alexander.astrafiles.core.formatModifiedDate
import com.alexander.astrafiles.ui.theme.FinderPalette

@Composable
fun GetInfoDialog(entry: FileEntry, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = FinderPalette.contentBackground,
        shape = RoundedCornerShape(10.dp),
        title = { Text(entry.name, color = FinderPalette.textPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                InfoRow("Kind", if (entry.isDirectory) "Folder" else entry.kind.name)
                InfoRow("Size", if (entry.isDirectory) "--" else formatFileSize(entry.sizeBytes))
                InfoRow("Location", entry.path.parent?.toString() ?: "--")
                InfoRow("Modified", formatModifiedDate(entry.modifiedAt))
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = FinderPalette.accent)
            }
        }
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, fontSize = 12.sp, color = FinderPalette.textTertiary)
        Text(text = value, fontSize = 12.sp, color = FinderPalette.textPrimary)
    }
}
