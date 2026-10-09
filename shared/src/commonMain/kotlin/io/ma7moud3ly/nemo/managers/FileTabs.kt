package io.ma7moud3ly.nemo.managers

import io.ma7moud3ly.nemo.model.NemoFile
import io.ma7moud3ly.nemo.model.asLanguage
import io.ma7moud3ly.nemo.platform.exists
import io.ma7moud3ly.nemo.tabs.NemoTab
import io.ma7moud3ly.nemo.tabs.TabsManager

/** The file a tab edits. Every tab in the app is opened from a [NemoFile]. */
val NemoTab.file: NemoFile get() = tabFile as NemoFile

/**
 * Opens [file] in a tab, or switches to its tab when it is already open.
 * A file that is not on disk yet starts with unsaved changes.
 */
fun TabsManager.openFile(file: NemoFile, content: String) {
    openTab(
        document = file,
        content = content,
        language = file.extension.asLanguage(),
        isDirty = file.exists().not()
    )
}

/** Adds an empty tab that has no file on disk yet. */
fun TabsManager.newUntitledTab() {
    addTab(document = NemoFile(name = "untitled", path = "", extension = ""))
}
