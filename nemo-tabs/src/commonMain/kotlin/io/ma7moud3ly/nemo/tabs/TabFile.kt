package io.ma7moud3ly.nemo.tabs

/**
 * Something that can be opened in a tab, such as a file or a script.
 *
 * Implement it on the app's own file class to open that class in a
 * [TabsManager].
 */
interface TabFile {
    /** Text shown on the tab. */
    val name: String

    /**
     * Where the document lives. Two documents with the same path are the same
     * one. It is empty for a document that is not saved yet.
     */
    val path: String
}
