package io.ma7moud3ly.nemo.model

data class EditorTab(
    val id: String,
    val file: NemoFile,
    val codeState: CodeState = CodeState("", Language.TEXT),
    private val isDirty: Boolean = false
) {
    fun contentChanged(): Boolean = codeState.contentChanged
    val fileContent get() = codeState.code
}