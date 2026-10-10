package io.ma7moud3ly.nemo.tabs

import io.ma7moud3ly.nemo.model.CodeState

/**
 * One open document in the editor.
 *
 * @param id unique id of the tab
 * @param tabFile what the tab edits
 * @param codeState the editor state holding the tab's text
 */
data class NemoTab(
    val id: String,
    val tabFile: TabFile,
    val codeState: CodeState
) {
    /** Text shown on the tab. */
    val title: String get() = tabFile.name

    /** Where the tab's document lives. */
    val path: String get() = tabFile.path

    /** The text currently in the tab. */
    val content: String get() = codeState.code

    /** True when the tab has changes that are not saved yet. */
    val isDirty: Boolean get() = codeState.contentChanged

    /**
     * True when the tab has text but its document has no path yet, so it needs
     * a name and a place before it can be saved.
     */
    val isNew: Boolean get() = path.isEmpty() && content.isNotEmpty()
}
