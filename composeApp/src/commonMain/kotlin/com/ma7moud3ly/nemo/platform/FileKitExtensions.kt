package com.ma7moud3ly.nemo.platform

import com.ma7moud3ly.nemo.model.NemoFile
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.PlatformFile
import io.github.vinceglb.filekit.dialogs.FileKitDialogSettings

expect suspend fun FileKit.getPlatformDirectoryPicker(
    title: String?, directory: PlatformFile?, dialogSettings: FileKitDialogSettings,
): PlatformFile?

expect suspend fun FileKit.platformSaveFileAs(file: NemoFile, content: String): NemoFile?
expect suspend fun FileKit.platformSaveFile(file: NemoFile, content: String)
expect suspend fun FileKit.platformPickFile(): PlatformFile?
expect suspend fun FileKit.platformListFiles(path: String): List<NemoFile>
expect suspend fun FileKit.platformDelete(file: NemoFile)
expect suspend fun FileKit.platformRename(file: NemoFile, newName: String): NemoFile?
expect suspend fun FileKit.platformCreateFolder(path: String)
expect suspend fun FileKit.platformCreateFile(path: String)
expect suspend fun FileKit.platformExportFile(file: NemoFile, content: String)
expect fun String.asPlatformFile(): PlatformFile?
expect fun NemoFile.exists(): Boolean