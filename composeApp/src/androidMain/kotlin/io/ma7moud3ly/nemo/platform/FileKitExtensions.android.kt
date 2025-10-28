package io.ma7moud3ly.nemo.platform

import android.content.Intent
import android.net.Uri
import io.ma7moud3ly.nemo.AndroidApplication
import io.ma7moud3ly.nemo.managers.FilesManager.Companion.asNemoFile
import io.ma7moud3ly.nemo.managers.FilesManager.Companion.extensions
import io.ma7moud3ly.nemo.model.NemoFile
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
import androidx.core.net.toUri

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
    val context = AndroidApplication.getAppContext()
    val uri = platformFile.toString().toUri()
    context?.contentResolver?.takePersistableUriPermission(
        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    )
    return platformFile?.asNemoFile
}

actual suspend fun FileKit.platformPickFile(): PlatformFile? {
    val platformFile = FileKit.openFilePicker(
        type = FileKitType.File(extensions),
        title = "Open File",
        dialogSettings = FileKitDialogSettings.createDefault(),
    )
    val uri = Uri.parse(platformFile.toString())
    val context = AndroidApplication.getAppContext()
    context?.contentResolver?.takePersistableUriPermission(
        uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
    )
    /*for (perm in context!!.contentResolver.persistedUriPermissions) {
        println("PersistedURI - URI=${perm.uri}, read=${perm.isReadPermission}, write=${perm.isWritePermission}")
    }*/
    println("--------------> $platformFile")
    return platformFile
}


actual suspend fun FileKit.platformSaveFile(file: NemoFile, content: String) {
    file.platformFile?.writeString(content)
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