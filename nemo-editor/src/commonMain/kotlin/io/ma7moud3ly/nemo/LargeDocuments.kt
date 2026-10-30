package io.ma7moud3ly.nemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import io.ma7moud3ly.nemo.syntax.highlighting.SyntaxHighlighter
import io.ma7moud3ly.nemo.syntax.indent.IndentGuideCalculator
import io.ma7moud3ly.nemo.syntax.indent.IndentGuides
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.milliseconds

/**
 * Above this many characters, a document is highlighted and its indent guides
 * are computed off the main thread, once typing pauses.
 */
internal const val LARGE_DOCUMENT = 10_000

/**
 * Above this many characters, a document is shown as plain text, without
 * indent guides, error marks or autocomplete.
 */
internal const val HUGE_DOCUMENT = 500_000

/** How long typing must pause before a large document is processed again. */
private val SETTLE_DELAY = 150.milliseconds

/**
 * [code] with its syntax colors.
 *
 * A small document is highlighted right away. A large one is highlighted on a
 * background thread once typing pauses; until then the last colors are moved
 * onto the edited text with [shiftStyles], so the colors always match the
 * text. A huge document is shown in [plainColor].
 */
@Composable
internal fun rememberHighlightedCode(
    code: String,
    highlighter: SyntaxHighlighter,
    plainColor: Color
): AnnotatedString {
    if (code.length > HUGE_DOCUMENT) {
        return remember(code, plainColor) { plainText(code, plainColor) }
    }

    // The last full highlight, shown while a large document is highlighted again
    var latest by remember(highlighter) { mutableStateOf<AnnotatedString?>(null) }

    if (code.length <= LARGE_DOCUMENT) {
        val highlighted = remember(code, highlighter) { highlighter.highlight(code) }
        SideEffect { latest = highlighted }
        return highlighted
    }

    LaunchedEffect(code, highlighter) {
        // A newly opened document is highlighted right away
        if (latest != null) delay(SETTLE_DELAY)
        latest = withContext(Dispatchers.Default) { highlighter.highlight(code) }
    }

    val last = latest
    return remember(code, last, plainColor) {
        when {
            last == null -> plainText(code, plainColor)
            last.text == code -> last
            else -> shiftStyles(last, code)
        }
    }
}

/**
 * The indent guides of [code], or null when they are off.
 *
 * Like [rememberHighlightedCode], they are computed right away for a small
 * document and on a background thread once typing pauses for a large one,
 * which keeps its last guides meanwhile. A huge document has none.
 */
@Composable
internal fun rememberIndentGuides(code: String, tabSize: Int, enabled: Boolean): IndentGuides? {
    if (!enabled || code.length > HUGE_DOCUMENT) return null

    var latest by remember(tabSize) { mutableStateOf<IndentGuides?>(null) }

    if (code.length <= LARGE_DOCUMENT) {
        val guides = remember(code, tabSize) { IndentGuideCalculator.compute(code, tabSize) }
        SideEffect { latest = guides }
        return guides
    }

    LaunchedEffect(code, tabSize) {
        if (latest != null) delay(SETTLE_DELAY)
        latest = withContext(Dispatchers.Default) { IndentGuideCalculator.compute(code, tabSize) }
    }
    return latest
}

/**
 * The styles of [highlighted] moved onto [text], an edited version of its
 * text. Styles before the edit stay, styles after it move with the text, a
 * style that holds the whole edit grows or shrinks with it, and a style that
 * the edit cuts through is dropped until the next highlight.
 */
internal fun shiftStyles(highlighted: AnnotatedString, text: String): AnnotatedString {
    val old = highlighted.text
    val limit = minOf(old.length, text.length)

    var prefix = 0
    while (prefix < limit && old[prefix] == text[prefix]) prefix++
    var suffix = 0
    while (suffix < limit - prefix &&
        old[old.length - 1 - suffix] == text[text.length - 1 - suffix]
    ) suffix++

    val oldEnd = old.length - suffix
    val delta = text.length - old.length
    val styles = highlighted.spanStyles.mapNotNull { range ->
        when {
            range.end <= prefix -> range
            range.start >= oldEnd ->
                AnnotatedString.Range(range.item, range.start + delta, range.end + delta)

            range.start <= prefix && range.end >= oldEnd ->
                AnnotatedString.Range(range.item, range.start, range.end + delta)

            else -> null
        }
    }
    return AnnotatedString(text, styles)
}

private fun plainText(code: String, color: Color): AnnotatedString =
    AnnotatedString(code, listOf(AnnotatedString.Range(SpanStyle(color = color), 0, code.length)))
