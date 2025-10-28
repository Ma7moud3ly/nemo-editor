package io.ma7moud3ly.nemo.model

data class Token(
    val text: String,
    val type: TokenType,
    val start: Int,
    val end: Int
)

enum class TokenType {
    KEYWORD, STRING, COMMENT, NUMBER, FUNCTION, TYPE, VARIABLE, OPERATOR, PUNCTUATION, WHITESPACE
}
