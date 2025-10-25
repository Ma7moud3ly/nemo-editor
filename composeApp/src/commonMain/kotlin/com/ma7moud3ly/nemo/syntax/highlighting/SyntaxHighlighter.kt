package com.ma7moud3ly.nemo.syntax.highlighting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import com.ma7moud3ly.nemo.model.EditorTheme
import com.ma7moud3ly.nemo.model.TokenType
import com.ma7moud3ly.nemo.syntax.analysis.ErrorDetector
import com.ma7moud3ly.nemo.syntax.tokenizer.LanguageTokenizer

class SyntaxHighlighter(
    private val tokenizer: LanguageTokenizer,
    private val errorDetector: ErrorDetector?,
    private val theme: EditorTheme
) {
    fun highlight(text: String): AnnotatedString {
        val tokens = tokenizer.tokenize(text)
        val errors = errorDetector?.detectErrors(tokens)

        return buildAnnotatedString {
            append(text)
            // Apply syntax highlighting
            tokens.forEach { token ->
                val color = getColorForToken(token.type)
                val fontWeight = if (token.type == TokenType.KEYWORD) FontWeight.Bold else null
                val fontStyle = if (token.type == TokenType.COMMENT) FontStyle.Italic else null
                addStyle(
                    style = SpanStyle(
                        color = color,
                        fontWeight = fontWeight,
                        fontStyle = fontStyle
                    ),
                    start = token.start,
                    end = token.end
                )
            }

            // Apply error highlighting (red wavy underline)
            errors?.forEach { error ->
                addStyle(
                    style = SpanStyle(
                        color = Color(0xFFFF5555), // Bright red for errors
                        textDecoration = TextDecoration.Companion.Underline,
                        background = Color(0xFFFF5555).copy(alpha = 0.1f) // Light red background
                    ),
                    start = error.start,
                    end = error.end
                )
            }
        }
    }

    private fun getColorForToken(type: TokenType): Color {
        return when (type) {
            TokenType.KEYWORD -> Color(theme.syntax.keyword)
            TokenType.STRING -> Color(theme.syntax.string)
            TokenType.COMMENT -> Color(theme.syntax.comment)
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