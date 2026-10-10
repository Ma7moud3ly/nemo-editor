package io.ma7moud3ly.nemo.ui.examples

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.ma7moud3ly.nemo.NemoCodeEditor
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.rememberCodeState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun PythonEditorPreview() {
    PythonEditor()
}

@Composable
fun PythonEditor() {
    val codeState = rememberCodeState(
        code = """
                def fibonacci(n):
                    if n <= 1:
                        return n
                    return fibonacci(n-1) + fibonacci(n-2)
                
                for i in range(10):
                    print(fibonacci(i))
            """.trimIndent(),
        language = Language.PYTHON
    )


    val editorSettings = remember {
        EditorSettings(
            theme = EditorThemes.NEMO_DARK,
            readOnly = true,
            tabSize = 4,
            contentPadding = PaddingValues(4.dp)
        )
    }

    NemoCodeEditor(
        state = codeState,
        settings = editorSettings,
        modifier = Modifier.fillMaxSize()
    )
}

