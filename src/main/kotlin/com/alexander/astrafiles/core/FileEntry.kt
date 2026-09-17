package com.alexander.astrafiles.core

import java.nio.file.Files
import java.nio.file.Path
import java.time.Instant
import kotlin.io.path.isDirectory
import kotlin.io.path.isHidden
import kotlin.io.path.name

enum class FileKind {
    Folder,
    Image,
    Document,
    Spreadsheet,
    Archive,
    Audio,
    Video,
    Code,
    Executable,
    PlainText,
    Unknown
}

data class FileEntry(
    val path: Path,
    val name: String,
    val isDirectory: Boolean,
    val isHidden: Boolean,
    val sizeBytes: Long,
    val modifiedAt: Instant,
    val kind: FileKind
) {
    val extension: String
        get() = name.substringAfterLast('.', "").lowercase()

    val isTextEditable: Boolean
        get() = kind == FileKind.PlainText || kind == FileKind.Code

    companion object {
        fun from(path: Path): FileEntry? {
            return try {
                val attributes = Files.readAttributes(path, java.nio.file.attribute.BasicFileAttributes::class.java)
                val directory = path.isDirectory()
                val name = path.name
                FileEntry(
                    path = path,
                    name = name,
                    isDirectory = directory,
                    isHidden = path.isHidden() || name.startsWith("."),
                    sizeBytes = if (directory) 0L else attributes.size(),
                    modifiedAt = attributes.lastModifiedTime().toInstant(),
                    kind = classify(path, name, directory)
                )
            } catch (_: Exception) {
                null
            }
        }

        private fun classify(path: Path, name: String, isDirectory: Boolean): FileKind {
            if (isDirectory) return FileKind.Folder
            val extension = name.substringAfterLast('.', "").lowercase()
            val byExtension = when (extension) {
                "png", "jpg", "jpeg", "gif", "bmp", "webp", "svg", "heic" -> FileKind.Image
                "mp4", "mkv", "avi", "mov", "webm", "flv" -> FileKind.Video
                "mp3", "wav", "flac", "ogg", "m4a", "aac" -> FileKind.Audio
                "zip", "tar", "gz", "7z", "rar", "xz", "bz2" -> FileKind.Archive
                "pdf", "doc", "docx", "odt", "rtf" -> FileKind.Document
                "xls", "xlsx", "ods", "csv" -> FileKind.Spreadsheet
                "kt", "kts", "java", "py", "js", "ts", "c", "cpp", "h", "rs", "go", "sh", "html", "css", "json", "xml", "yaml", "yml" -> FileKind.Code
                "txt", "md", "log" -> FileKind.PlainText
                "run", "appimage", "bin" -> FileKind.Executable
                else -> null
            }
            if (byExtension != null) return byExtension
            return if (extension.isEmpty() && Files.isExecutable(path)) FileKind.Executable else FileKind.Unknown
        }
    }
}
