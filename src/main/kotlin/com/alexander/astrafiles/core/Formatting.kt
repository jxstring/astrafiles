package com.alexander.astrafiles.core

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val modifiedDateFormatter = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm").withZone(ZoneId.systemDefault())

fun formatFileSize(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = listOf("KB", "MB", "GB", "TB")
    var value = bytes.toDouble()
    var unitIndex = -1
    while (value >= 1024 && unitIndex < units.lastIndex) {
        value /= 1024
        unitIndex++
    }
    return if (value >= 100) {
        "%.0f %s".format(value, units[unitIndex])
    } else {
        "%.1f %s".format(value, units[unitIndex])
    }
}

fun formatModifiedDate(instant: Instant): String = modifiedDateFormatter.format(instant)
