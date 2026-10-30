package io.ma7moud3ly.nemo

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import io.ma7moud3ly.nemo.lsp.completion.AutocompleteState
import io.ma7moud3ly.nemo.managers.AutoIndentHandlerFactory
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.syntax.analysis.ErrorDetectorFactory
import io.ma7moud3ly.nemo.syntax.highlighting.SyntaxHighlighter
import io.ma7moud3ly.nemo.syntax.indent.IndentGuides
import io.ma7moud3ly.nemo.syntax.indent.indentGuides
import io.ma7moud3ly.nemo.syntax.tokenizer.TokenizerFactory
import kotlinx.coroutines.launch

/**
 * The code editor.
 *
 * Clicking a line number toggles a highlight on that line, kept in
 * [CodeState.highlights].
 *
 * @param state the code, the caret, the undo history and the highlighted lines
 * @param modifier applied to the editor
 * @param settings theme, font, indentation and the other options
 */
@Composable
fun NemoCodeEditor(
    state: CodeState,
    modifier: Modifier = Modifier,
    settings: EditorSettings = EditorSettings()
) {
    val language = state.language
    val theme by remember { settings.themeState }

    val tokenizer = remember(language) { TokenizerFactory.getTokenizer(language) }
    val errorDetector = remember(language) { ErrorDetectorFactory.getErrorDetector(language) }
    val autoIndentHandler = remember(language, settings) {
        AutoIndentHandlerFactory.create(language, settings)
    }
    val highlighter = remember(theme, tokenizer, errorDetector) {
        SyntaxHighlighter(tokenizer, errorDetector, theme)
    }

    // Highlighted right away for a small document, off the main thread for a large one
    val highlightedCode = rememberHighlightedCode(
        code = state.code,
        highlighter = highlighter,
        plainColor = Color(theme.foreground)
    )

    // Indentation guides - recomputed only when the text or tab size changes,
    // the active guide is resolved later during the draw phase.
    val indentGuides = rememberIndentGuides(
        code = state.code,
        tabSize = settings.tabSizeState.value,
        enabled = settings.showIndentGuidesState.value
    )

    // Track scroll state for autocomplete positioning
    val scrollState = rememberScrollState()

    // Autocomplete state
    val autocompleteState = remember { AutocompleteState() }

    Box(
        modifier = modifier
            .pointerInput(Unit) {
                detectTransformGestures { _, _, zoom, _ ->
                    settings.gesturesZoom(zoom)
                }
            }
            .onPreviewKeyEvent { keyEvent ->
                if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

                // Keyboard navigation for autocomplete
                if (autocompleteState.isVisible) {
                    when (keyEvent.key) {
                        Key.DirectionDown -> {
                            autocompleteState.selectNext()
                            return@onPreviewKeyEvent true
                        }

                        Key.DirectionUp -> {
                            autocompleteState.selectPrevious()
                            return@onPreviewKeyEvent true
                        }

                        Key.Tab, Key.Enter -> {
                            val item = autocompleteState.getSelectedItem()
                            if (item != null) {
                                state.insertCompletion(item.insertText)
                                autocompleteState.markCompletionInserted()
                                return@onPreviewKeyEvent true
                            }
                            // Nothing to accept - close the popup and let the
                            // key do its ordinary job instead of swallowing it.
                            autocompleteState.hide()
                        }

                        Key.Escape -> {
                            autocompleteState.hide()
                            return@onPreviewKeyEvent true
                        }

                        else -> Unit
                    }
                }

                // Break the line here rather than letting the platform insert
                // the newline and then correcting the text underneath it.
                if (keyEvent.key == Key.Enter && !settings.readOnlyState.value) {
                    state.insertLineBreak(
                        if (settings.enableAutoIndentState.value) autoIndentHandler else null
                    )
                    return@onPreviewKeyEvent true
                }

                false
            }
    ) {
        EditorContent(
            code = state.value,
            highlightedCode = highlightedCode,
            onValueChange = { newValue ->
                if (settings.readOnlyState.value.not()) {
                    state.handleTextChange(
                        newValue = newValue,
                        autoIndentHandler = if (settings.enableAutoIndentState.value) autoIndentHandler
                        else null
                    )
                }
            },
            theme = theme,
            totalLines = state.totalLines,
            currentLineIndex = state.currentLine - 1,
            fontSize = settings.fontSizeState.value,
            fontFamily = settings.fontFamilyState.value,
            showLineNumbers = settings.showLineNumbersState.value,
            readOnly = settings.readOnlyState.value,
            contentPadding = settings.contentPaddingState.value,
            scrollState = scrollState,
            guides = { indentGuides },
            activeLine = { state.currentLine - 1 },
            state = state,
            settings = settings,
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
    fontFamily: FontFamily,
    showLineNumbers: Boolean,
    readOnly: Boolean,
    contentPadding: PaddingValues,
    scrollState: ScrollState,
    guides: () -> IndentGuides?,
    activeLine: () -> Int,
    state: CodeState,
    settings: EditorSettings,
    autocompleteState: AutocompleteState
) {
    val horizontalScrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    val textSize = fontSize.sp
    val lineHeight = (fontSize * 1.5f).sp

    val codeTextStyle = remember(textSize, lineHeight, fontFamily) {
        TextStyle(
            fontFamily = fontFamily,
            fontSize = textSize,
            lineHeight = lineHeight
        )
    }

    // Layout of the highlighted text. Indent guides read exact row bounds from
    // it, and the autocomplete popup reads the caret rectangle.
    var codeTextLayout by remember { mutableStateOf<TextLayoutResult?>(null) }

    // Color of a highlighted line that has no color of its own.
    val highlightColor = Color(theme.syntax.keyword).copy(alpha = 0.2f)

    // Bounds of the visible code area, the viewport the popup is placed within.
    var codeAreaSize by remember { mutableStateOf(IntSize.Zero) }

    // Where the text starts inside the scrolling content, in pixels.
    val layoutDirection = LocalLayoutDirection.current
    val textLeft = with(LocalDensity.current) {
        contentPadding.calculateLeftPadding(layoutDirection).toPx()
    }
    val textTop = with(LocalDensity.current) { contentPadding.calculateTopPadding().toPx() }

    // The lines on screen, read in the draw phase so scrolling only repaints
    val visibleLines: () -> IntRange? = {
        codeTextLayout?.let { layout ->
            val top = scrollState.value - textTop
            layout.getLineForVerticalPosition(top)..layout.getLineForVerticalPosition(top + codeAreaSize.height)
        }
    }

    // Caret bounds in code-area coordinates: read off the real text layout, then
    // shifted by the scroll offsets and the padding the text sits behind. Taking
    // it from the layout keeps it right at any density and font size, which an
    // estimate from line height cannot be.
    val caretRect: () -> Rect? = {
        val layout = codeTextLayout
        if (layout == null) {
            null
        } else {
            val offset = code.selection.start.coerceIn(0, layout.layoutInput.text.length)
            layout.getCursorRect(offset).translate(
                translateX = textLeft - horizontalScrollState.value,
                translateY = textTop - scrollState.value
            )
        }
    }

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
        // Line numbers. Only the visible ones are drawn, at the lines of the code
        // layout, and a click on one toggles that line's highlight.
        if (showLineNumbers) {
            LineNumberGutter(
                totalLines = totalLines,
                textLayout = { codeTextLayout },
                scrollState = scrollState,
                textTop = textTop,
                currentLine = { state.currentLine },
                highlights = { state.highlights },
                style = codeTextStyle,
                color = Color(theme.lineNumber),
                activeColor = Color(theme.lineNumberActive),
                highlightColor = highlightColor,
                background = Color(theme.gutter),
                onLineClick = { state.toggleHighlight(it) }
            )
        }

        // Editor area. Its own bounds are the viewport the autocomplete popup
        // is placed within, so the popup is a child of this box rather than of
        // the scrolling content.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color(theme.background))
                .onSizeChanged { codeAreaSize = it }
        ) {
            val customTextSelectionColors = TextSelectionColors(
                handleColor = Color(theme.syntax.keyword),
                backgroundColor = Color(theme.selection).copy(alpha = 0.6f)
            )

            CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {
                // The field draws the colored code itself, so the document is laid
                // out once, and the caret and selection always match the glyphs.
                val highlighting = remember(highlightedCode) {
                    HighlightTransformation(highlightedCode)
                }
                BasicTextField(
                    value = code,
                    onValueChange = onValueChange,
                    readOnly = readOnly,
                    textStyle = codeTextStyle.copy(color = Color(theme.foreground)),
                    cursorBrush = SolidColor(Color(theme.syntax.keyword)),
                    visualTransformation = highlighting,
                    onTextLayout = { codeTextLayout = it },
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .horizontalScroll(horizontalScrollState)
                        .padding(contentPadding)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .lineHighlights(
                                    highlights = { state.highlights },
                                    textLayout = { codeTextLayout },
                                    defaultColor = highlightColor
                                )
                                .indentGuides(
                                    guides = guides,
                                    activeLine = activeLine,
                                    textLayout = { codeTextLayout },
                                    color = Color(theme.lineNumber).copy(alpha = 0.30f),
                                    activeColor = Color(theme.lineNumberActive).copy(alpha = 0.75f),
                                    visibleLines = visibleLines
                                )
                        ) {
                            if (code.text.isEmpty() && !readOnly) {
                                Text(
                                    "Start typing...",
                                    style = codeTextStyle.copy(
                                        color = Color(theme.lineNumber)
                                    )
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }

            AutocompletePopup(
                state = state,
                settings = settings,
                autocompleteState = autocompleteState,
                caretRect = caretRect,
                viewportSize = { codeAreaSize }
            )

            if (settings.showScrollbarsState.value) {
                val scrollbarColor = Color(theme.lineNumber).copy(alpha = 0.5f)
                EditorScrollbar(
                    scrollState = scrollState,
                    orientation = Orientation.Vertical,
                    color = scrollbarColor,
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
                EditorScrollbar(
                    scrollState = horizontalScrollState,
                    orientation = Orientation.Horizontal,
                    color = scrollbarColor,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }
        }
    }
}
