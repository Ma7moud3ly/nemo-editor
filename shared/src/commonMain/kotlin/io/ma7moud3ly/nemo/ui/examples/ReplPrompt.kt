package io.ma7moud3ly.nemo.ui.examples

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.NemoCodeField
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.rememberCodeState

@Preview
@Composable
private fun ReplPromptPreview() {
    ReplPrompt()
}

/**
 * A one-line Python prompt built with [NemoCodeField].
 *
 * Enter, or the Send key of an on-screen keyboard, moves the line to the
 * history above the prompt and empties the field.
 */
@Composable
fun ReplPrompt() {
    val codeState = rememberCodeState(
        code = "print(\"Hello\")",
        language = Language.PYTHON
    )
    val settings = remember { EditorSettings(theme = EditorThemes.NEMO_DARK) }
    val theme = settings.themeState.value
    val history = remember { mutableStateListOf("x = 42") }

    // The same font, size and line height as the field, so the prompt mark
    // and the history line up with the code being typed.
    val fontSize = settings.fontSizeState.value
    val textStyle = TextStyle(
        fontFamily = settings.fontFamilyState.value,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 1.5f).sp
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            // The field draws no background of its own
            .background(Color(theme.background))
            .padding(8.dp)
    ) {
        history.forEach { line ->
            Text(
                text = ">>> $line",
                style = textStyle,
                color = Color(theme.lineNumber)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = ">>> ",
                style = textStyle,
                color = Color(theme.syntax.keyword)
            )
            NemoCodeField(
                state = codeState,
                settings = settings,
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        if (codeState.code.isNotBlank()) history.add(codeState.code)
                        codeState.updateText("")
                    }
                )
            )
        }
    }
}
