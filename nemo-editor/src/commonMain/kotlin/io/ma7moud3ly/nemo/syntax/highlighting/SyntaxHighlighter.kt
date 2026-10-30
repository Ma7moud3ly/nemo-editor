package io.ma7moud3ly.nemo.syntax.highlighting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import io.ma7moud3ly.nemo.model.EditorTheme
import io.ma7moud3ly.nemo.model.TokenType
import io.ma7moud3ly.nemo.syntax.analysis.ErrorDetector
import io.ma7moud3ly.nemo.syntax.tokenizer.LanguageTokenizer

/**
 * Colors code by its tokens, and marks the errors the [errorDetector] finds.
 *
 * The whole text gets the theme's foreground color, and only tokens drawn
 * differently get a style of their own. Neighboring tokens with the same
 * style, such as `private fun` or a string split by templates, share one
 * style, so the text holds far fewer styles to shift and lay out.
 */
class SyntaxHighlighter(
    private val tokenizer: LanguageTokenizer,
    private val errorDetector: ErrorDetector?,
    private val theme: EditorTheme
) {
    private val baseStyle = SpanStyle(color = Color(theme.foreground))

    /** The style of each token type, or no entry when it looks like plain text. */
    private val tokenStyles: Map<TokenType, SpanStyle> = TokenType.entries
        .associateWith { styleOf(it) }
        .filterValues { it != baseStyle }

    private val errorStyle = SpanStyle(
        color = Color(0xFFFF5555), // Bright red for errors
        textDecoration = TextDecoration.Underline,
        background = Color(0xFFFF5555).copy(alpha = 0.1f) // Light red background
    )

    fun highlight(text: String): AnnotatedString {
        val tokens = tokenizer.tokenize(text)
        val errors = errorDetector?.detectErrors(tokens)

        val styles = ArrayList<AnnotatedString.Range<SpanStyle>>(tokens.size / 2 + 1)
        styles.add(AnnotatedString.Range(baseStyle, 0, text.length))

        // The style being extended over neighboring tokens, and its range
        var current: SpanStyle? = null
        var start = 0
        var end = 0
        for (token in tokens) {
            if (token.type == TokenType.WHITESPACE) continue
            val style = tokenStyles[token.type]
            // Extend the current style when only whitespace separates the tokens
            if (style != null && style == current && text.isBlankBetween(end, token.start)) {
                end = token.end
                continue
            }
            if (current != null) styles.add(AnnotatedString.Range(current, start, end))
            current = style
            start = token.start
            end = token.end
        }
        if (current != null) styles.add(AnnotatedString.Range(current, start, end))

        // Mark errors (red underline) over the colors
        errors?.forEach { error ->
            styles.add(AnnotatedString.Range(errorStyle, error.start, error.end))
        }

        return AnnotatedString(text, styles)
    }

    private fun String.isBlankBetween(from: Int, to: Int): Boolean {
        for (i in from until to) if (!this[i].isWhitespace()) return false
        return true
    }

    private fun styleOf(type: TokenType): SpanStyle = SpanStyle(
        color = getColorForToken(type),
        fontWeight = if (type == TokenType.KEYWORD) FontWeight.Bold else null,
        fontStyle = when (type) {
            TokenType.COMMENT, TokenType.DOC_COMMENT, TokenType.DOC_TAG -> FontStyle.Italic
            else -> null
        }
    )

    private fun getColorForToken(type: TokenType): Color {
        return when (type) {
            TokenType.KEYWORD -> Color(theme.syntax.keyword)
            TokenType.STRING -> Color(theme.syntax.string)
            TokenType.COMMENT -> Color(theme.syntax.comment)
            TokenType.DOC_COMMENT -> Color(theme.syntax.docComment)
            TokenType.DOC_TAG -> Color(theme.syntax.docTag)
            TokenType.NUMBER -> Color(theme.syntax.number)
            TokenType.FUNCTION -> Color(theme.syntax.function)
            TokenType.TYPE -> Color(theme.syntax.type)
            TokenType.VARIABLE -> Color(theme.syntax.variable)
            TokenType.OPERATOR -> Color(theme.syntax.operator)
            TokenType.PUNCTUATION -> Color(theme.foreground)
            TokenType.WHITESPACE -> Color(theme.foreground)
        }
    }
}
