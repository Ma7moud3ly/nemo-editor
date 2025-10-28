package io.ma7moud3ly.nemo.syntax.tokenizer

import io.ma7moud3ly.nemo.model.Token
import io.ma7moud3ly.nemo.model.TokenType

class PythonTokenizer : LanguageTokenizer() {
    override val keywords = setOf(
        // Control flow
        "if", "elif", "else", "for", "while", "break", "continue", "return",
        "pass", "yield", "raise", "try", "except", "finally", "with", "assert",
        // Declarations
        "def", "class", "lambda", "global", "nonlocal", "import", "from", "as",
        // Operators
        "and", "or", "not", "in", "is", "del",
        // Other
        "True", "False", "None", "async", "await"
    )

    private val builtInFunctions = setOf(
        "print", "input", "len", "range", "type", "int", "float", "str", "bool",
        "list", "dict", "set", "tuple", "abs", "all", "any", "bin", "chr", "dir",
        "enumerate", "filter", "format", "hex", "id", "isinstance", "issubclass",
        "iter", "map", "max", "min", "next", "oct", "open", "ord", "pow", "repr",
        "reversed", "round", "sorted", "sum", "super", "zip", "help", "hasattr",
        "getattr", "setattr", "delattr", "vars", "callable", "compile", "eval",
        "exec", "staticmethod", "classmethod", "property"
    )

    override val builtInTypes = setOf(
        "object", "type", "Exception", "BaseException", "ValueError", "TypeError",
        "KeyError", "IndexError", "AttributeError", "NameError", "IOError",
        "RuntimeError", "StopIteration", "GeneratorExit", "KeyboardInterrupt"
    )

    override fun tokenize(text: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0

        while (i < text.length) {
            when {
                // Comments
                text[i] == '#' -> {
                    val end = text.indexOf('\n', i).takeIf { it != -1 } ?: text.length
                    tokens.add(Token(text.substring(i, end), TokenType.COMMENT, i, end))
                    i = end
                }

                // Triple-quoted strings
                text.startsWith("\"\"\"", i) || text.startsWith("'''", i) -> {
                    val delimiter = text.substring(i, i + 3)
                    val end =
                        text.indexOf(delimiter, i + 3).takeIf { it != -1 }?.plus(3) ?: text.length
                    tokens.add(Token(text.substring(i, end), TokenType.STRING, i, end))
                    i = end
                }

                // Raw strings
                text[i] in "rRbBfFuU" && i + 1 < text.length && text[i + 1] in "\"'" -> {
                    val start = i
                    i++ // Skip prefix
                    val quote = text[i]
                    val (end, _) = findStringEnd(text, i, quote)
                    tokens.add(Token(text.substring(start, end), TokenType.STRING, start, end))
                    i = end
                }

                // String literals
                text[i] == '"' || text[i] == '\'' -> {
                    val (end, _) = findStringEnd(text, i, text[i])
                    tokens.add(Token(text.substring(i, end), TokenType.STRING, i, end))
                    i = end
                }

                // Numbers
                text[i].isDigit() || (text[i] == '.' && i + 1 < text.length && text[i + 1].isDigit()) -> {
                    val end = findNumberEnd(text, i)
                    tokens.add(Token(text.substring(i, end), TokenType.NUMBER, i, end))
                    i = end
                }

                // Decorators
                text[i] == '@' && i + 1 < text.length && text[i + 1].isLetter() -> {
                    var end = i + 1
                    while (end < text.length && (text[end].isLetterOrDigit() || text[end] == '_')) end++
                    tokens.add(Token(text.substring(i, end), TokenType.TYPE, i, end))
                    i = end
                }

                // Identifiers, keywords, functions
                text[i].isLetter() || text[i] == '_' -> {
                    var end = i
                    while (end < text.length && (text[end].isLetterOrDigit() || text[end] == '_')) end++
                    val word = text.substring(i, end)

                    val type = when {
                        word in keywords -> TokenType.KEYWORD
                        word in builtInTypes -> TokenType.TYPE
                        word in builtInFunctions -> TokenType.FUNCTION
                        word[0].isUpperCase() -> TokenType.TYPE
                        isFunctionCall(text, end) -> TokenType.FUNCTION
                        isFunctionDef(text, i) -> TokenType.FUNCTION
                        isClassDef(text, i) -> TokenType.TYPE
                        else -> TokenType.VARIABLE
                    }

                    tokens.add(Token(word, type, i, end))
                    i = end
                }

                // Operators and punctuation
                text[i] in "+-*/%=<>!&|^~(){}[].,;:@" -> {
                    val end = findOperatorEnd(text, i)
                    tokens.add(Token(text.substring(i, end), TokenType.OPERATOR, i, end))
                    i = end
                }

                // Whitespace
                text[i].isWhitespace() -> {
                    var end = i
                    while (end < text.length && text[end].isWhitespace()) end++
                    tokens.add(Token(text.substring(i, end), TokenType.WHITESPACE, i, end))
                    i = end
                }

                else -> i++
            }
        }

        return tokens
    }

    override fun findStringEnd(text: String, start: Int, quote: Char): Pair<Int, Boolean> {
        var i = start + 1
        var escaped = false

        while (i < text.length) {
            when {
                escaped -> escaped = false
                text[i] == '\\' -> escaped = true
                text[i] == quote -> return Pair(i + 1, false)
            }
            i++
        }

        return Pair(i, false)
    }

    override fun findNumberEnd(text: String, start: Int): Int {
        var i = start
        var hasDecimalPoint = false
        var hasExponent = false

        // Check for hex (0x), binary (0b), octal (0o)
        if (i < text.length - 1 && text[i] == '0') {
            when (text[i + 1]) {
                'x', 'X' -> {
                    i += 2
                    while (i < text.length && (text[i].isDigit() || text[i] in "abcdefABCDEF" || text[i] == '_')) i++
                    return i
                }

                'b', 'B' -> {
                    i += 2
                    while (i < text.length && (text[i] in "01" || text[i] == '_')) i++
                    return i
                }

                'o', 'O' -> {
                    i += 2
                    while (i < text.length && (text[i] in "01234567" || text[i] == '_')) i++
                    return i
                }
            }
        }

        // Regular number
        while (i < text.length) {
            when {
                text[i].isDigit() -> i++
                text[i] == '.' && !hasDecimalPoint && i + 1 < text.length && text[i + 1].isDigit() -> {
                    hasDecimalPoint = true
                    i++
                }

                text[i] in "eE" && !hasExponent -> {
                    hasExponent = true
                    i++
                    if (i < text.length && text[i] in "+-") i++
                }

                text[i] == 'j' || text[i] == 'J' -> return i + 1 // Complex number
                text[i] == '_' -> i++ // Numeric separator
                else -> break
            }
        }

        return i
    }

    override fun findOperatorEnd(text: String, start: Int): Int {
        val multiCharOps = setOf(
            "//", "**", "==", "!=", "<=", ">=", "+=", "-=", "*=", "/=",
            "//=", "**=", "%=", "&=", "|=", "^=", ">>=", "<<=", "->", ":="
        )

        // Try 3-character operators
        if (start + 3 <= text.length) {
            val threeChar = text.substring(start, start + 3)
            if (threeChar in multiCharOps) return start + 3
        }

        // Try 2-character operators
        if (start + 2 <= text.length) {
            val twoChar = text.substring(start, start + 2)
            if (twoChar in multiCharOps) return start + 2
        }

        return start + 1
    }

    override fun findIdentifierEnd(text: String, start: Int): Int {
        TODO("Not yet implemented")
    }

    override fun isFunctionCall(text: String, position: Int): Boolean {
        var i = position
        while (i < text.length && text[i].isWhitespace()) i++
        return i < text.length && text[i] == '('
    }

    override fun isFunctionDef(text: String, position: Int): Boolean {
        var i = position - 1
        while (i >= 0 && text[i].isWhitespace()) i--

        if (i >= 2 && text.substring(i - 2, i + 1) == "def") {
            return i - 3 < 0 || text[i - 3].isWhitespace()
        }
        return false
    }

    override fun isClassDef(text: String, position: Int): Boolean {
        var i = position - 1
        while (i >= 0 && text[i].isWhitespace()) i--

        if (i >= 4 && text.substring(i - 4, i + 1) == "class") {
            return i - 5 < 0 || text[i - 5].isWhitespace()
        }
        return false
    }
}