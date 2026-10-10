package io.ma7moud3ly.nemo

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.managers.AutoIndentHandlerFactory
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.syntax.highlighting.SyntaxHighlighter
import io.ma7moud3ly.nemo.syntax.tokenizer.TokenizerFactory

/**
 * A syntax-highlighted input field for code, such as a REPL prompt.
 *
 * It is the small sibling of [NemoCodeEditor]. It shares the same [CodeState],
 * highlighting and undo history, but it has no line numbers, indent guides or
 * autocomplete, it is as tall as its text, and it does not take focus on its
 * own. Long lines scroll sideways.
 *
 * The field draws no background, so put it on a surface that suits the
 * theme's colors. Read the text from [CodeState.code].
 *
 * @param state the code, the caret and the undo history
 * @param modifier applied to the field
 * @param settings theme, font, indentation and read-only mode
 * @param singleLine keeps the text on one line. Enter then runs the keyboard
 *   action from [keyboardActions] and inserts no line break.
 * @param keyboardOptions keyboard type and the action shown on the keyboard,
 *   such as `ImeAction.Send`
 * @param keyboardActions what runs when the keyboard action is pressed
 * @param focusRequester lets the caller move focus to the field
 */
@Composable
fun NemoCodeField(
    state: CodeState,
    modifier: Modifier = Modifier,
    settings: EditorSettings = EditorSettings(),
    singleLine: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester = remember { FocusRequester() }
) {
    val language = state.language
    val theme by remember { settings.themeState }
    val readOnly = settings.readOnlyState.value
    val fontSize = settings.fontSizeState.value
    val fontFamily = settings.fontFamilyState.value

    val tokenizer = remember(language) { TokenizerFactory.getTokenizer(language) }
    val highlighter = remember(theme, tokenizer) {
        SyntaxHighlighter(tokenizer, errorDetector = null, theme = theme)
    }
    val highlightedCode = remember(state.code, theme) {
        highlighter.highlight(state.code)
    }

    // A single line never breaks, so it has no indentation to carry over.
    val autoIndentHandler = remember(language, settings, singleLine) {
        if (singleLine) null else AutoIndentHandlerFactory.create(language, settings)
    }
    val autoIndent = settings.enableAutoIndentState.value

    val codeTextStyle = remember(fontSize, fontFamily) {
        TextStyle(
            fontFamily = fontFamily,
            fontSize = fontSize.sp,
            lineHeight = (fontSize * 1.5f).sp
        )
    }

    val selectionColors = TextSelectionColors(
        handleColor = Color(theme.syntax.keyword),
        backgroundColor = Color(theme.selection).copy(alpha = 0.4f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides selectionColors) {
        BasicTextField(
            value = state.value,
            onValueChange = { newValue ->
                if (!readOnly) {
                    state.handleTextChange(
                        newValue = newValue,
                        autoIndentHandler = if (autoIndent) autoIndentHandler else null
                    )
                }
            },
            modifier = modifier
                .focusRequester(focusRequester)
                .onPreviewKeyEvent { keyEvent ->
                    // With several lines, Enter breaks the line and keeps its
                    // indentation. With a single line it is left to the text
                    // field, which runs the keyboard action.
                    val breaksLine = !singleLine &&
                            !readOnly &&
                            keyEvent.type == KeyEventType.KeyDown &&
                            keyEvent.key == Key.Enter
                    if (breaksLine) {
                        state.insertLineBreak(if (autoIndent) autoIndentHandler else null)
                    }
                    breaksLine
                }
                // The field scrolls as a whole, so the highlighted text drawn
                // over it always stays aligned with the text being edited.
                .horizontalScroll(rememberScrollState()),
            readOnly = readOnly,
            singleLine = singleLine,
            textStyle = codeTextStyle.copy(color = Color.Transparent),
            cursorBrush = SolidColor(Color(theme.syntax.keyword)),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            decorationBox = { innerTextField ->
                Box {
                    Text(
                        text = highlightedCode,
                        style = codeTextStyle,
                        softWrap = false,
                        maxLines = if (singleLine) 1 else Int.MAX_VALUE
                    )
                    innerTextField()
                }
            }
        )
    }
}
