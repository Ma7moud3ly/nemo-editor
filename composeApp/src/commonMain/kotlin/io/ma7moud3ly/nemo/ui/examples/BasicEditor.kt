package io.ma7moud3ly.nemo.ui.examples

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.rememberCodeState
import io.ma7moud3ly.nemo.NemoCodeEditor


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
    NemoCodeEditor(state = codeState)
}