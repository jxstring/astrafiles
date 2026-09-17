package com.alexander.astrafiles.core

import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists
import kotlin.io.path.name

data class VolumeEntry(val label: String, val path: Path)

data class FavoriteLocation(val label: String, val path: Path, val icon: FavoriteIcon)

enum class FavoriteIcon {
    Home, Desktop, Documents, Downloads, Pictures, Music, Videos
}

object FileSystemRepository {

    fun homeDirectory(): Path = Paths.get(System.getProperty("user.home"))

    fun listDirectory(directory: Path, includeHidden: Boolean): List<FileEntry> {
        if (!Files.isDirectory(directory)) return emptyList()
        return Files.newDirectoryStream(directory).use { stream ->
            stream.mapNotNull(FileEntry::from)
        }.filter { includeHidden || !it.isHidden }
    }

    fun favorites(): List<FavoriteLocation> {
        val home = homeDirectory()
        val candidates = listOf(
            FavoriteLocation("Home", home, FavoriteIcon.Home),
            FavoriteLocation("Desktop", home.resolve("Desktop"), FavoriteIcon.Desktop),
            FavoriteLocation("Documents", home.resolve("Documents"), FavoriteIcon.Documents),
            FavoriteLocation("Downloads", home.resolve("Downloads"), FavoriteIcon.Downloads),
            FavoriteLocation("Pictures", home.resolve("Pictures"), FavoriteIcon.Pictures),
            FavoriteLocation("Music", home.resolve("Music"), FavoriteIcon.Music),
            FavoriteLocation("Videos", home.resolve("Videos"), FavoriteIcon.Videos)
        )
        return candidates.filter { it.path.exists() }
    }

    fun volumes(): List<VolumeEntry> {
        val roots = FileSystems.getDefault().rootDirectories.map { VolumeEntry("Filesystem", it) }
        val mediaRoots = listOf(Paths.get("/media"), Paths.get("/mnt"))
            .filter { Files.isDirectory(it) }
            .flatMap { base ->
                Files.newDirectoryStream(base).use { stream ->
                    stream.filter { Files.isDirectory(it) }.map { VolumeEntry(it.name, it) }
                }
            }
        return roots + mediaRoots
    }

    fun uniqueName(directory: Path, baseName: String, extension: String = ""): String {
        val suffix = if (extension.isEmpty()) "" else ".$extension"
        var candidate = "$baseName$suffix"
        var counter = 2
        while (directory.resolve(candidate).exists()) {
            candidate = "$baseName $counter$suffix"
            counter++
        }
        return candidate
    }

    fun createFolder(parent: Path, name: String): Result<Path> {
        return try {
            Result.success(Files.createDirectory(parent.resolve(name)))
        } catch (error: IOException) {
            Result.failure(error)
        }
    }

    fun createFile(parent: Path, name: String): Result<Path> {
        return try {
            Result.success(Files.createFile(parent.resolve(name)))
        } catch (error: IOException) {
            Result.failure(error)
        }
    }

    fun rename(path: Path, newName: String): Result<Path> {
        return try {
            val target = path.resolveSibling(newName)
            if (target.exists()) return Result.failure(IOException("\"$newName\" already exists"))
            val renamed = Files.move(path, target)
            Result.success(renamed)
        } catch (error: IOException) {
            Result.failure(error)
        }
    }

    fun loadText(path: Path): Result<String> = runCatching { Files.readString(path) }

    fun saveText(path: Path, content: String): Result<Unit> = runCatching {
        Files.writeString(path, content)
        Unit
    }

    fun copyInto(sources: List<Path>, destinationDirectory: Path): Result<Unit> {
        return try {
            for (source in sources) {
                val targetName = uniqueName(
                    destinationDirectory,
                    source.name.substringBeforeLast('.', source.name),
                    source.name.substringAfterLast('.', "")
                )
                val target = destinationDirectory.resolve(if (targetName == source.name) source.name else targetName)
                copyRecursively(source, target)
            }
            Result.success(Unit)
        } catch (error: IOException) {
            Result.failure(error)
        }
    }

    fun moveInto(sources: List<Path>, destinationDirectory: Path): Result<Unit> {
        return try {
            for (source in sources) {
                val target = destinationDirectory.resolve(source.name)
                Files.move(source, target, StandardCopyOption.REPLACE_EXISTING)
            }
            Result.success(Unit)
        } catch (error: IOException) {
            Result.failure(error)
        }
    }

    fun moveToTrash(paths: List<Path>): Result<Unit> {
        return try {
            if (isCommandAvailable("gio")) {
                for (path in paths) {
                    val process = ProcessBuilder("gio", "trash", path.toString())
                        .redirectErrorStream(true)
                        .start()
                    process.waitFor()
                }
            } else {
                for (path in paths) {
                    deleteRecursively(path)
                }
            }
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private fun isCommandAvailable(command: String): Boolean {
        return try {
            val process = ProcessBuilder("which", command).redirectErrorStream(true).start()
            process.waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }

    private fun copyRecursively(source: Path, target: Path) {
        if (Files.isDirectory(source)) {
            Files.createDirectories(target)
            Files.newDirectoryStream(source).use { stream ->
                for (child in stream) {
                    copyRecursively(child, target.resolve(child.name))
                }
            }
        } else {
            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES)
        }
    }

    private fun deleteRecursively(path: Path) {
        if (Files.isDirectory(path)) {
            Files.newDirectoryStream(path).use { stream ->
                for (child in stream) {
                    deleteRecursively(child)
                }
            }
        }
        Files.deleteIfExists(path)
    }
}
