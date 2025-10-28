package io.ma7moud3ly.nemo.syntax.analysis


import io.ma7moud3ly.nemo.model.SyntaxError
import io.ma7moud3ly.nemo.model.Token
import io.ma7moud3ly.nemo.model.TokenType


class KotlinErrorDetector : ErrorDetector() {

    override fun detectErrors(tokens: List<Token>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()

        // Only check for obvious errors
        errors.addAll(findBracketErrors(tokens))
        errors.addAll(findUnclosedStrings(tokens))
        errors.addAll(findDuplicateKeywords(tokens))

        return errors
    }

    override fun findDuplicateKeywords(tokens: List<Token>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        var i = 0

        while (i < tokens.size) {
            val token = tokens[i]

            if (token.type == TokenType.WHITESPACE || token.type == TokenType.COMMENT) {
                i++
                continue
            }

            if (token.type == TokenType.KEYWORD) {
                val nextNonWhitespace = getNextNonWhitespace(tokens, i)

                // Only flag same keyword twice in a row (fun fun, class class)
                if (nextNonWhitespace != null &&
                    nextNonWhitespace.type == TokenType.KEYWORD &&
                    nextNonWhitespace.text == token.text) {

                    errors.add(
                        SyntaxError(
                            start = nextNonWhitespace.start,
                            end = nextNonWhitespace.end,
                            message = "Duplicate keyword '${nextNonWhitespace.text}'"
                        )
                    )
                }
            }

            i++
        }

        return errors
    }

    override fun findUnclosedStrings(tokens: List<Token>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()

        tokens.forEach { token ->
            if (token.type == TokenType.STRING) {
                val text = token.text
                // Only flag clearly unclosed strings
                if (text.length == 1 ||
                    (!text.startsWith("\"\"\"") && text.length >= 2 && text.first() != text.last())) {
                    errors.add(
                        SyntaxError(
                            start = token.start,
                            end = token.end,
                            message = "Unclosed string literal"
                        )
                    )
                }
            }
        }

        return errors
    }

    override fun findBracketErrors(tokens: List<Token>): List<SyntaxError> {
        val errors = mutableListOf<SyntaxError>()
        val stack = mutableListOf<Token>()

        tokens.forEach { token ->
            if (token.type == TokenType.OPERATOR) {
                when (token.text) {
                    "(", "{", "[" -> stack.add(token)
                    ")", "}", "]" -> {
                        val expected = when (token.text) {
                            ")" -> "("
                            "}" -> "{"
                            "]" -> "["
                            else -> null
                        }

                        if (stack.isEmpty()) {
                            errors.add(
                                SyntaxError(
                                    start = token.start,
                                    end = token.end,
                                    message = "Unmatched '${token.text}'"
                                )
                            )
                        } else if (stack.last().text != expected) {
                            errors.add(
                                SyntaxError(
                                    start = token.start,
                                    end = token.end,
                                    message = "Expected '${getClosingBracket(stack.last().text)}' but found '${token.text}'"
                                )
                            )
                            stack.removeLastOrNull()
                        } else {
                            stack.removeLastOrNull()
                        }
                    }
                }
            }
        }

        // Mark unclosed opening brackets
        stack.forEach { unclosedToken ->
            errors.add(
                SyntaxError(
                    start = unclosedToken.start,
                    end = unclosedToken.end,
                    message = "Unclosed '${unclosedToken.text}'"
                )
            )
        }

        return errors
    }

    override fun getNextNonWhitespace(tokens: List<Token>, currentIndex: Int): Token? {
        var i = currentIndex + 1
        while (i < tokens.size) {
            if (tokens[i].type != TokenType.WHITESPACE && tokens[i].type != TokenType.COMMENT) {
                return tokens[i]
            }
            i++
        }
        return null
    }

    private fun getClosingBracket(open: String): String {
        return when (open) {
            "(" -> ")"
            "{" -> "}"
            "[" -> "]"
            else -> ""
        }
    }
}
