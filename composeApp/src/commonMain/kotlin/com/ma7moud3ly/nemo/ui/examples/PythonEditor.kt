package com.ma7moud3ly.nemo.ui.examples

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.ma7moud3ly.nemo.model.EditorSettings
import com.ma7moud3ly.nemo.model.Language
import com.ma7moud3ly.nemo.model.rememberCodeState
import com.ma7moud3ly.nemo.themes.EditorThemes
import com.ma7moud3ly.nemo.ui.editor.NemoCodeEditor
import org.jetbrains.compose.ui.tooling.preview.Preview

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


    val settings = remember {
        EditorSettings(
            theme = EditorThemes.NEMO_DARK,
            tabSize = 4,
        )
    }

    NemoCodeEditor(
        codeState = codeState,
        editorSettings = settings,
        modifier = Modifier.fillMaxSize()
    )
}

