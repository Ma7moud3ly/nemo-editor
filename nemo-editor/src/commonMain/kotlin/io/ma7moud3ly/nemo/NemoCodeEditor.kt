package io.ma7moud3ly.nemo

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.lsp.completion.AutocompleteState
import io.ma7moud3ly.nemo.managers.AutoIndentHandlerFactory
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.syntax.analysis.ErrorDetectorFactory
import io.ma7moud3ly.nemo.syntax.highlighting.SyntaxHighlighter
import io.ma7moud3ly.nemo.syntax.tokenizer.TokenizerFactory
import kotlinx.coroutines.launch

@Composable
fun NemoCodeEditor(
    codeState: CodeState,
    editorSettings: EditorSettings = EditorSettings(),
    modifier: Modifier = Modifier
) {
    val language = codeState.language
    val theme by remember { editorSettings.themeState }

    val tokenizer = remember(language) { TokenizerFactory.getTokenizer(language) }
    val errorDetector = remember(language) { ErrorDetectorFactory.getErrorDetector(language) }
    val autoIndentHandler = remember(language, editorSettings) {
        AutoIndentHandlerFactory.create(language, editorSettings)
    }
    val highlighter = remember(theme, tokenizer, errorDetector) {
        SyntaxHighlighter(tokenizer, errorDetector, theme)
    }

    val highlightedCode = remember(codeState.code, theme) {
        highlighter.highlight(codeState.code)
    }

    // Track scroll state for autocomplete positioning
    val scrollState = rememberScrollState()

    // Autocomplete state
    val autocompleteState = remember { AutocompleteState() }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    editorSettings.gesturesZoom(zoom)
                }
            }
            .onPreviewKeyEvent { keyEvent ->
                // Handle keyboard navigation for autocomplete
                if (autocompleteState.isVisible && keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.DirectionDown -> {
                            autocompleteState.selectNext()
                            true
                        }

                        Key.DirectionUp -> {
                            autocompleteState.selectPrevious()
                            true
                        }

                        Key.Tab, Key.Enter -> {
                            autocompleteState.getSelectedItem()?.let { item ->
                                codeState.insertCompletion(item.insertText)
                                autocompleteState.markCompletionInserted()
                            }
                            true
                        }

                        Key.Escape -> {
                            autocompleteState.hide()
                            true
                        }

                        else -> false
                    }
                } else {
                    false
                }
            }
    ) {
        EditorContent(
            code = codeState.value,
            highlightedCode = highlightedCode,
            onValueChange = { newValue ->
                if (editorSettings.readOnlyState.value.not()) {
                    codeState.handleTextChange(
                        newValue = newValue,
                        autoIndentHandler = if (editorSettings.enableAutoIndentState.value) autoIndentHandler
                        else null
                    )
                }
            },
            theme = theme,
            totalLines = codeState.totalLines,
            currentLineIndex = codeState.currentLine - 1,
            fontSize = editorSettings.fontSizeState.value,
            showLineNumbers = editorSettings.showLineNumbersState.value,
            readOnly = editorSettings.readOnlyState.value,
            scrollState = scrollState
        )

        AutocompletePopup(
            state = codeState,
            settings = editorSettings,
            scrollOffset = scrollState.value,
            autocompleteState = autocompleteState
        )
    }
}

@Composable
private fun EditorContent(
    code: TextFieldValue,
    highlightedCode: AnnotatedString,
    onValueChange: (TextFieldValue) -> Unit,
    totalLines: Int,
    currentLineIndex: Int,
    theme: EditorTheme,
    fontSize: Int,
    showLineNumbers: Boolean,
    readOnly: Boolean,
    scrollState: ScrollState
) {
    val horizontalScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val textSize = fontSize.sp
    val lineHeight = (fontSize * 1.5f).sp

    val density = LocalDensity.current
    val imeInsets = WindowInsets.ime
    val imeBottom = imeInsets.getBottom(density)
    val imeVisible = imeBottom > 0

    // Auto-scroll to cursor when keyboard appears
    LaunchedEffect(currentLineIndex, imeVisible) {
        if (imeVisible) {
            val lineHeightPx = (fontSize * 1.5f * density.density).toInt()
            val targetScrollPosition = currentLineIndex * lineHeightPx
            coroutineScope.launch {
                scrollState.animateScrollTo(targetScrollPosition)
            }
        }
    }

    // Request focus on first composition
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Row(modifier = Modifier.fillMaxSize()) {
        // Line numbers
        if (showLineNumbers) {
            Surface(
                color = Color(theme.gutter),
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .fillMaxHeight()
                    .background(Color(theme.gutter))
                    .padding(top = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .verticalScroll(scrollState, enabled = false)
                        .horizontalScroll(horizontalScrollState, enabled = false)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = (1..totalLines).joinToString("\n") { it.toString() },
                        style = TextStyle(
                            fontFamily = FontFamily.Monospace,
                            fontSize = textSize,
                            lineHeight = lineHeight,
                            color = Color(theme.lineNumber),
                            textAlign = TextAlign.End
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        softWrap = false,
                        maxLines = Int.MAX_VALUE
                    )
                }
            }
        }

        // Editor area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(theme.background))
                .padding(top = 4.dp)
        ) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = Color(theme.syntax.keyword),
                backgroundColor = Color(0xFF3D5A80).copy(alpha = 0.4f)
            )

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                BasicTextField(
                    value = code,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = textSize,
                        lineHeight = lineHeight,
                        color = Color.Transparent
                    ),
                    cursorBrush = SolidColor(Color(theme.syntax.keyword)),
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .horizontalScroll(horizontalScrollState)
                        .padding(horizontal = 4.dp)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box {
                            if (code.text.isEmpty()) {
                                Text(
                                    if (readOnly) "" else "Start typing...",
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = textSize,
                                        lineHeight = lineHeight,
                                        color = Color(theme.lineNumber)
                                    )
                                )
                            } else {
                                Text(
                                    text = highlightedCode,
                                    style = TextStyle(
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = textSize,
                                        lineHeight = lineHeight
                                    ),
                                    softWrap = false,
                                    maxLines = Int.MAX_VALUE
                                )
                            }
                            Box(modifier = Modifier.alpha(1f)) {
                                innerTextField()
                            }
                        }
                    }
                )
            }
        }
    }
}