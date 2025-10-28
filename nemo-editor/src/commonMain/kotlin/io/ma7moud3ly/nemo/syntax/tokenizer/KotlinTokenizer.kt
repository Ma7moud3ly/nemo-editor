package io.ma7moud3ly.nemo.syntax.tokenizer

import io.ma7moud3ly.nemo.model.Token
import io.ma7moud3ly.nemo.model.TokenType

class KotlinTokenizer : LanguageTokenizer() {
    override val keywords = setOf(
        // Control flow
        "if", "else", "when", "for", "while", "do", "return", "break", "continue",
        // Declarations
        "package", "import", "class", "interface", "fun", "val", "var", "object",
        "companion", "data", "sealed", "enum", "annotation", "typealias",
        // Modifiers
        "abstract", "open", "override", "final", "private", "protected",
        "public", "internal", "lateinit", "const", "inline", "noinline",
        "crossinline", "reified", "suspend", "tailrec", "operator", "infix",
        "external", "expect", "actual",
        // Other
        "this", "super", "null", "true", "false", "is", "in", "as", "try",
        "catch", "finally", "throw", "constructor", "init", "by", "where",
        "out", "get", "set", "field", "it"
    )

    override val builtInTypes = setOf(
        "Unit", "Nothing", "Any", "Boolean", "Byte", "Short", "Int", "Long",
        "Float", "Double", "Char", "String", "Array", "List", "Set", "Map",
        "Pair", "Triple", "Sequence", "IntArray", "LongArray", "FloatArray",
        "DoubleArray", "BooleanArray", "CharArray", "ByteArray", "ShortArray",
        "Collection", "Iterable", "Iterator", "MutableList", "MutableSet",
        "MutableMap", "Number", "Comparable", "Throwable", "Exception", "Error"
    )

    override fun tokenize(text: String): List<Token> {
        val tokens = mutableListOf<Token>()
        var i = 0

        while (i < text.length) {
            when {
                // Single-line comments
                text.startsWith("//", i) -> {
                    val end = text.indexOf('\n', i).takeIf { it != -1 } ?: text.length
                    tokens.add(Token(text.substring(i, end), TokenType.COMMENT, i, end))
                    i = end
                }

                // Multi-line comments
                text.startsWith("/*", i) -> {
                    val end = text.indexOf("*/", i + 2).takeIf { it != -1 }?.plus(2) ?: text.length
                    tokens.add(Token(text.substring(i, end), TokenType.COMMENT, i, end))
                    i = end
                }

                // Raw strings (triple quotes)
                text.startsWith("\"\"\"", i) -> {
                    val end =
                        text.indexOf("\"\"\"", i + 3).takeIf { it != -1 }?.plus(3) ?: text.length
                    tokens.add(Token(text.substring(i, end), TokenType.STRING, i, end))
                    i = end
                }

                // String literals (double quotes)
                text[i] == '"' -> {
                    val (end, _) = findStringEnd(text, i, '"')
                    tokens.add(Token(text.substring(i, end), TokenType.STRING, i, end))
                    i = end
                }

                // Character literals (single quotes)
                text[i] == '\'' -> {
                    val (end, _) = findStringEnd(text, i, '\'')
                    tokens.add(Token(text.substring(i, end), TokenType.STRING, i, end))
                    i = end
                }

                // Numbers
                text[i].isDigit() || (text[i] == '.' && i + 1 < text.length && text[i + 1].isDigit()) -> {
                    val end = findNumberEnd(text, i)
                    tokens.add(Token(text.substring(i, end), TokenType.NUMBER, i, end))
                    i = end
                }

                // Annotations
                text[i] == '@' && i + 1 < text.length && text[i + 1].isLetter() -> {
                    var end = i + 1
                    while (end < text.length && (text[end].isLetterOrDigit() || text[end] == '_')) end++
                    tokens.add(Token(text.substring(i, end), TokenType.TYPE, i, end))
                    i = end
                }

                // Identifiers, keywords, types, functions
                text[i].isLetter() || text[i] == '_' || text[i] == '`' -> {
                    val end = findIdentifierEnd(text, i)
                    val word = text.substring(i, end).trim('`')

                    val type = when {
                        word in keywords -> TokenType.KEYWORD
                        word in builtInTypes -> TokenType.TYPE
                        word[0].isUpperCase() -> TokenType.TYPE
                        isFunctionCall(text, end) -> TokenType.FUNCTION
                        else -> TokenType.VARIABLE
                    }

                    tokens.add(Token(text.substring(i, end), type, i, end))
                    i = end
                }

                // Operators and punctuation
                text[i] in "+-*/%=<>!&|^~(){}[].,;:?" -> {
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

        // Check for hex (0x) or binary (0b)
        if (i < text.length - 1 && text[i] == '0') {
            if (text[i + 1] == 'x' || text[i + 1] == 'X') {
                i += 2
                while (i < text.length && (text[i].isDigit() || text[i] in "abcdefABCDEF")) i++
                return i
            }
            if (text[i + 1] == 'b' || text[i + 1] == 'B') {
                i += 2
                while (i < text.length && text[i] in "01") i++
                return i
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

                text[i] in "fFdDlL" -> return i + 1
                text[i] == '_' -> i++ // Numeric separator
                else -> break
            }
        }

        return i
    }

    override fun findIdentifierEnd(text: String, start: Int): Int {
        var i = start

        // Handle backtick-quoted identifiers
        if (text[i] == '`') {
            i++
            while (i < text.length && text[i] != '`') i++
            if (i < text.length) i++
            return i
        }

        // Regular identifier
        while (i < text.length && (text[i].isLetterOrDigit() || text[i] == '_')) i++
        return i
    }

    override fun findOperatorEnd(text: String, start: Int): Int {
        val multiCharOps = setOf(
            "++", "--", "&&", "||", "==", "!=", "<=", ">=", "===", "!==",
            "+=", "-=", "*=", "/=", "%=", "->", "..", "?:", "!!", "?."
        )

        // Try to match 3-character operators first
        if (start + 3 <= text.length) {
            val threeChar = text.substring(start, start + 3)
            if (threeChar in multiCharOps) return start + 3
        }

        // Try 2-character operators
        if (start + 2 <= text.length) {
            val twoChar = text.substring(start, start + 2)
            if (twoChar in multiCharOps) return start + 2
        }

        // Single character operator
        return start + 1
    }

    override fun isFunctionCall(text: String, position: Int): Boolean {
        var i = position
        while (i < text.length && text[i].isWhitespace()) i++
        return i < text.length && text[i] == '('
    }

    override fun isFunctionDef(text: String, position: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun isClassDef(text: String, position: Int): Boolean {
        TODO("Not yet implemented")
    }
}

