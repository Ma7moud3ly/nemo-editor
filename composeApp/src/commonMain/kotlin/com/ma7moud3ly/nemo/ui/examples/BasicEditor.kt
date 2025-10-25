package com.ma7moud3ly.nemo.ui.examples

import androidx.compose.runtime.Composable
import com.ma7moud3ly.nemo.model.Language
import com.ma7moud3ly.nemo.model.rememberCodeState
import com.ma7moud3ly.nemo.ui.editor.NemoCodeEditor
import org.jetbrains.compose.ui.tooling.preview.Preview

@Preview
@Composable
private fun MyEditorPreview() {
    MyEditor()
}

@Composable
fun MyEditor() {
    val codeState = rememberCodeState(
        code = "fun main() {\n    println(\"Hello\")\n}",
        language = Language.KOTLIN
    )
    NemoCodeEditor(codeState = codeState)
}