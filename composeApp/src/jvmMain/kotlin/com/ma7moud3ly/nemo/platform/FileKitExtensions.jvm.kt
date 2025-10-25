package com.ma7moud3ly.nemo.platform

import com.ma7moud3ly.nemo.managers.FilesManager.Companion.asNemoFile
import com.ma7moud3ly.nemo.managers.FilesManager.Companion.extensions
import com.ma7moud3ly.nemo.model.NemoFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.atomicMove
import io.github.vinceglb.filekit.createDirectories
import io.github.vinceglb.filekit.delete
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openDirectoryPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.dialogs.openFileSaver
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.parent
import io.github.vinceglb.filekit.writeString
import java.io.File


actual suspend fun FileKit.getPlatformDirectoryPicker(
    title: String?,
    directory: PlatformFile?,
    dialogSettings: FileKitDialogSettings,
): PlatformFile? {
    return FileKit.openDirectoryPicker(
        title = title,
        directory = directory,
        dialogSettings = dialogSettings
    )
}

actual suspend fun FileKit.platformSaveFileAs(file: NemoFile, content: String): NemoFile? {
    val platformFile = FileKit.openFileSaver(
        suggestedName = file.nameWithoutExt,
        extension = file.extension,
        directory = null,
        dialogSettings = FileKitDialogSettings.createDefault()

    )
    platformFile?.writeString(content)
    return platformFile?.asNemoFile
}

actual suspend fun FileKit.platformSaveFile(file: NemoFile, content: String) {
    file.platformFile?.writeString(content)
}

actual suspend fun FileKit.platformPickFile(): PlatformFile? {
    val platformFile = FileKit.openFilePicker(
        type = FileKitType.File(extensions),
        title = "Open File",
        dialogSettings = FileKitDialogSettings.createDefault(),
    )
    return platformFile
}

actual suspend fun FileKit.platformListFiles(path: String): List<NemoFile> {
    return File(path).listFiles()
        ?.map {
            NemoFile(
                name = it.name,
                path = it.path,
                isDirectory = it.isDirectory,
                extension = it.extension,
                size = it.length(),
                lastModified = it.lastModified(),
                platformFile = PlatformFile(it.path)
            )
        } ?: emptyList()
}

actual suspend fun FileKit.platformDelete(file: NemoFile) {
    file.platformFile?.delete(mustExist = false)
}

actual suspend fun FileKit.platformRename(file: NemoFile, newName: String): NemoFile? {
    val destination = PlatformFile("${file.platformFile?.parent()}/$newName")
    file.platformFile?.atomicMove(destination)
    return if (destination.exists()) destination.asNemoFile else null
}

actual suspend fun FileKit.platformCreateFolder(path: String) {
    val platformFile = PlatformFile(path)
    platformFile.createDirectories(mustCreate = true)
}

actual suspend fun FileKit.platformCreateFile(path: String) {
    val platformFile = PlatformFile(path)
    platformFile.writeString("")
}

actual suspend fun FileKit.platformExportFile(file: NemoFile, content: String) {}

actual fun String.asPlatformFile(): PlatformFile? = PlatformFile(this)

actual fun NemoFile.exists(): Boolean {
    return this.platformFile?.exists() ?: false
}