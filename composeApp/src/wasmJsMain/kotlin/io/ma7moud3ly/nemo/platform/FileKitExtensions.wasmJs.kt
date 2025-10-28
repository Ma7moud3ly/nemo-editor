package io.ma7moud3ly.nemo.platform

import io.ma7moud3ly.nemo.managers.FilesManager.Companion.extensions
import io.ma7moud3ly.nemo.model.NemoFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.browser.document
import org.w3c.dom.HTMLAnchorElement
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

actual suspend fun FileKit.getPlatformDirectoryPicker(
    title: String?,
    directory: PlatformFile?,
    dialogSettings: FileKitDialogSettings,
): PlatformFile? {
    return null
}

actual suspend fun FileKit.platformSaveFileAs(file: NemoFile, content: String): NemoFile? {
    return null
}

actual suspend fun FileKit.platformSaveFile(file: NemoFile, content: String) {}

actual suspend fun FileKit.platformPickFile(): PlatformFile? {
    val platformFile = FileKit.openFilePicker(
        type = FileKitType.File(extensions),
        title = "Open File",
        dialogSettings = FileKitDialogSettings.createDefault(),
    )
    return platformFile
}

actual suspend fun FileKit.platformListFiles(path: String): List<NemoFile> {
    return emptyList()
}

actual suspend fun FileKit.platformDelete(file: NemoFile) {}
actual suspend fun FileKit.platformRename(file: NemoFile, newName: String): NemoFile? {
    return null
}

actual suspend fun FileKit.platformCreateFolder(path: String) {}
actual suspend fun FileKit.platformCreateFile(path: String) {}

@OptIn(ExperimentalWasmJsInterop::class)
actual suspend fun FileKit.platformExportFile(file: NemoFile, content: String) {
    val blobParts = JsArray<JsAny?>()
    blobParts[0] = content.toJsString()

    val blob = Blob(
        blobParts,
        BlobPropertyBag(type = "text/plain;charset=utf-8")
    )
    val url = URL.createObjectURL(blob)

    // Create a temporary anchor element
    val anchor = document.createElement("a") as HTMLAnchorElement
    anchor.href = url
    anchor.download = file.name

    // Trigger the download
    document.body?.appendChild(anchor)
    anchor.click()

    // Clean up
    document.body?.removeChild(anchor)
    URL.revokeObjectURL(url)
}

actual fun String.asPlatformFile(): PlatformFile? = null

actual fun NemoFile.exists(): Boolean {
    return this.platformFile != null
}