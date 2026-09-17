package com.alexander.astrafiles.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.alexander.astrafiles.ui.theme.FinderPalette

@Composable
fun StatusBar(itemCount: Int, selectedCount: Int, statusMessage: String?) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(FinderPalette.toolbarBackground)
            .border(width = 1.dp, color = FinderPalette.borderSubtle)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        val summary = if (selectedCount > 0) {
            "$itemCount items, $selectedCount selected"
        } else {
            "$itemCount items"
        }
        Text(text = summary, fontSize = 11.sp, color = FinderPalette.textSecondary)
        statusMessage?.let {
            Text(text = it, fontSize = 11.sp, color = FinderPalette.textTertiary)
        }
    }
}
