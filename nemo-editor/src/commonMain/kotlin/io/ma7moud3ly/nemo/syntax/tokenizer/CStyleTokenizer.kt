package io.ma7moud3ly.nemo.syntax.tokenizer

import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.model.Token
import io.ma7moud3ly.nemo.model.TokenType

/**
 * A light tokenizer for languages with C-style syntax, such as Java,
 * JavaScript, C and Go. Only the keywords and built-in types differ between
 * them.
 *
 * It reads `//` and `/* */` comments, strings in double quotes, single quotes
 * and backticks, numbers, annotations such as `@Override`, preprocessor
 * directives such as `#include`, and names that may contain `$`.
 */
class CStyleTokenizer(
    override val keywords: Set<String>,
    override val builtInTypes: Set<String>
) : LanguageTokenizer() {

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

                // Strings, characters and template strings
                text[i] == '"' || text[i] == '\'' || text[i] == '`' -> {
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

                // Annotations and decorators
                text[i] == '@' && i + 1 < text.length && text[i + 1].isLetter() -> {
                    val end = findIdentifierEnd(text, i + 1)
                    tokens.add(Token(text.substring(i, end), TokenType.TYPE, i, end))
                    i = end
                }

                // Preprocessor directives
                text[i] == '#' && i + 1 < text.length && text[i + 1].isLetter() -> {
                    val end = findIdentifierEnd(text, i + 1)
                    tokens.add(Token(text.substring(i, end), TokenType.KEYWORD, i, end))
                    i = end
                }

                // Identifiers, keywords, types, functions
                text[i].isLetter() || text[i] == '_' || text[i] == '$' -> {
                    val end = findIdentifierEnd(text, i)
                    val word = text.substring(i, end)

                    val type = when {
                        word in keywords -> TokenType.KEYWORD
                        word in builtInTypes -> TokenType.TYPE
                        word.first().isUpperCase() -> TokenType.TYPE
                        isFunctionCall(text, end) -> TokenType.FUNCTION
                        else -> TokenType.VARIABLE
                    }

                    tokens.add(Token(word, type, i, end))
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

    /**
     * A string in double or single quotes ends at its closing quote or at the
     * end of the line. A template string in backticks may span lines.
     */
    override fun findStringEnd(text: String, start: Int, quote: Char): Pair<Int, Boolean> {
        var i = start + 1
        var escaped = false

        while (i < text.length) {
            when {
                escaped -> escaped = false
                text[i] == '\\' -> escaped = true
                text[i] == quote -> return Pair(i + 1, false)
                text[i] == '\n' && quote != '`' -> return Pair(i, true)
            }
            i++
        }

        return Pair(i, true)
    }

    override fun findNumberEnd(text: String, start: Int): Int {
        var i = start

        // Hex (0x) and binary (0b)
        if (i + 1 < text.length && text[i] == '0' && text[i + 1] in "xXbB") {
            i += 2
            while (i < text.length && (text[i].isLetterOrDigit() || text[i] == '_')) i++
            return i
        }

        var hasDecimalPoint = false
        var hasExponent = false
        while (i < text.length) {
            when {
                text[i].isDigit() || text[i] == '_' -> i++
                text[i] == '.' && !hasDecimalPoint && i + 1 < text.length && text[i + 1].isDigit() -> {
                    hasDecimalPoint = true
                    i++
                }

                text[i] in "eE" && !hasExponent -> {
                    hasExponent = true
                    i++
                    if (i < text.length && text[i] in "+-") i++
                }

                // Suffixes such as 10L, 1.5f, 10u and 10n
                text[i] in "fFdDlLuUn" -> i++
                else -> break
            }
        }

        return i
    }

    override fun findOperatorEnd(text: String, start: Int): Int {
        // Try the longest operator first
        for (length in 3 downTo 2) {
            if (start + length <= text.length && text.substring(start, start + length) in OPERATORS) {
                return start + length
            }
        }
        return start + 1
    }

    override fun findIdentifierEnd(text: String, start: Int): Int {
        var i = start
        while (i < text.length && (text[i].isLetterOrDigit() || text[i] == '_' || text[i] == '$')) i++
        return i
    }

    override fun isFunctionCall(text: String, position: Int): Boolean {
        var i = position
        while (i < text.length && text[i].isWhitespace()) i++
        return i < text.length && text[i] == '('
    }

    override fun isFunctionDef(text: String, position: Int): Boolean = false

    override fun isClassDef(text: String, position: Int): Boolean = false

    companion object {
        private val OPERATORS = setOf(
            "===", "!==", "<<=", ">>=", "...", "**=", "??=", "<=>",
            "++", "--", "&&", "||", "==", "!=", "<=", ">=", "+=", "-=", "*=",
            "/=", "%=", "&=", "|=", "^=", "->", "=>", "::", "<<", ">>", "??",
            "?.", ":="
        )

        /** The tokenizer for [language], or null when it is not a C-style language. */
        fun forLanguage(language: Language): CStyleTokenizer? = when (language) {
            Language.JAVA -> java()
            Language.JAVASCRIPT, Language.REACT_JSX -> javaScript()
            Language.TYPESCRIPT, Language.REACT_TSX -> typeScript()
            Language.C, Language.CPP, Language.C_HEADER -> cFamily()
            Language.GO -> go()
            Language.RUST -> rust()
            Language.SWIFT -> swift()
            Language.DART -> dart()
            Language.PHP -> php()
            else -> null
        }

        fun java() = CStyleTokenizer(
            keywords = setOf(
                "abstract", "assert", "boolean", "break", "byte", "case", "catch",
                "char", "class", "const", "continue", "default", "do", "double",
                "else", "enum", "extends", "final", "finally", "float", "for",
                "goto", "if", "implements", "import", "instanceof", "int",
                "interface", "long", "native", "new", "package", "private",
                "protected", "public", "return", "short", "static", "strictfp",
                "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while", "var", "record",
                "sealed", "permits", "yield", "true", "false", "null"
            ),
            builtInTypes = setOf(
                "String", "Object", "Integer", "Long", "Double", "Float", "Boolean",
                "Character", "Byte", "Short", "Math", "System", "List", "ArrayList",
                "Map", "HashMap", "Set", "HashSet", "Optional", "Exception",
                "RuntimeException", "Thread", "StringBuilder", "Iterable",
                "Collection", "Arrays", "Collections"
            )
        )

        private val JS_KEYWORDS = setOf(
            "break", "case", "catch", "class", "const", "continue", "debugger",
            "default", "delete", "do", "else", "export", "extends", "finally",
            "for", "from", "function", "if", "import", "in", "instanceof", "let",
            "new", "of", "return", "static", "super", "switch", "this", "throw",
            "try", "typeof", "var", "void", "while", "with", "yield", "async",
            "await", "get", "set", "true", "false", "null", "undefined"
        )

        private val JS_TYPES = setOf(
            "Array", "Object", "String", "Number", "Boolean", "Promise", "Map",
            "Set", "Date", "Math", "JSON", "RegExp", "Error", "Symbol", "BigInt",
            "NaN", "Infinity", "console", "window", "document"
        )

        fun javaScript() = CStyleTokenizer(keywords = JS_KEYWORDS, builtInTypes = JS_TYPES)

        fun typeScript() = CStyleTokenizer(
            keywords = JS_KEYWORDS + setOf(
                "interface", "type", "enum", "implements", "namespace", "declare",
                "abstract", "readonly", "private", "protected", "public", "keyof",
                "as", "is", "satisfies"
            ),
            builtInTypes = JS_TYPES + setOf(
                "any", "unknown", "never", "number", "string", "boolean", "symbol",
                "bigint", "object", "Record", "Partial", "Readonly", "Pick", "Omit"
            )
        )

        fun cFamily() = CStyleTokenizer(
            keywords = setOf(
                "auto", "break", "case", "char", "const", "continue", "default", "do",
                "double", "else", "enum", "extern", "float", "for", "goto", "if",
                "inline", "int", "long", "register", "restrict", "return", "short",
                "signed", "sizeof", "static", "struct", "switch", "typedef", "union",
                "unsigned", "void", "volatile", "while", "bool", "true", "false",
                "NULL", "nullptr", "class", "namespace", "template", "typename",
                "public", "private", "protected", "virtual", "override", "final",
                "new", "delete", "this", "throw", "try", "catch", "using", "operator",
                "friend", "explicit", "constexpr", "noexcept", "static_cast",
                "dynamic_cast", "reinterpret_cast", "const_cast", "mutable", "decltype"
            ),
            builtInTypes = setOf(
                "size_t", "int8_t", "int16_t", "int32_t", "int64_t", "uint8_t",
                "uint16_t", "uint32_t", "uint64_t", "std", "string", "vector",
                "map", "FILE"
            )
        )

        fun go() = CStyleTokenizer(
            keywords = setOf(
                "break", "case", "chan", "const", "continue", "default", "defer",
                "else", "fallthrough", "for", "func", "go", "goto", "if", "import",
                "interface", "map", "package", "range", "return", "select",
                "struct", "switch", "type", "var", "true", "false", "nil", "iota"
            ),
            builtInTypes = setOf(
                "int", "int8", "int16", "int32", "int64", "uint", "uint8", "uint16",
                "uint32", "uint64", "uintptr", "float32", "float64", "complex64",
                "complex128", "string", "bool", "byte", "rune", "error", "any"
            )
        )

        fun rust() = CStyleTokenizer(
            keywords = setOf(
                "as", "async", "await", "break", "const", "continue", "crate", "dyn",
                "else", "enum", "extern", "false", "fn", "for", "if", "impl", "in",
                "let", "loop", "match", "mod", "move", "mut", "pub", "ref", "return",
                "self", "Self", "static", "struct", "super", "trait", "true", "type",
                "unsafe", "use", "where", "while"
            ),
            builtInTypes = setOf(
                "i8", "i16", "i32", "i64", "i128", "isize", "u8", "u16", "u32", "u64",
                "u128", "usize", "f32", "f64", "bool", "char", "str", "String", "Vec",
                "Option", "Some", "None", "Result", "Ok", "Err", "Box"
            )
        )

        fun swift() = CStyleTokenizer(
            keywords = setOf(
                "associatedtype", "class", "deinit", "enum", "extension",
                "fileprivate", "func", "import", "init", "inout", "internal", "let",
                "open", "operator", "private", "protocol", "public", "rethrows",
                "static", "struct", "subscript", "typealias", "var", "break", "case",
                "continue", "default", "defer", "do", "else", "fallthrough", "for",
                "guard", "if", "in", "repeat", "return", "switch", "where", "while",
                "as", "catch", "false", "is", "nil", "self", "Self", "super", "throw",
                "throws", "true", "try", "async", "await", "some", "any"
            ),
            builtInTypes = setOf(
                "Int", "Double", "Float", "String", "Bool", "Character", "Array",
                "Dictionary", "Set", "Optional", "Void"
            )
        )

        fun dart() = CStyleTokenizer(
            keywords = setOf(
                "abstract", "as", "assert", "async", "await", "break", "case",
                "catch", "class", "const", "continue", "covariant", "default",
                "deferred", "do", "dynamic", "else", "enum", "export", "extends",
                "extension", "external", "factory", "false", "final", "finally",
                "for", "get", "if", "implements", "import", "in", "interface", "is",
                "late", "library", "mixin", "new", "null", "on", "operator", "part",
                "required", "rethrow", "return", "sealed", "set", "show", "static",
                "super", "switch", "sync", "this", "throw", "true", "try", "typedef",
                "var", "void", "while", "with", "yield"
            ),
            builtInTypes = setOf(
                "int", "double", "num", "String", "bool", "List", "Map", "Set",
                "Future", "Stream", "Object", "Iterable", "Widget"
            )
        )

        fun php() = CStyleTokenizer(
            keywords = setOf(
                "abstract", "and", "as", "break", "callable", "case", "catch",
                "class", "clone", "const", "continue", "declare", "default", "do",
                "echo", "else", "elseif", "empty", "extends", "final", "finally",
                "fn", "for", "foreach", "function", "global", "goto", "if",
                "implements", "include", "include_once", "instanceof", "insteadof",
                "interface", "isset", "list", "match", "namespace", "new", "or",
                "print", "private", "protected", "public", "readonly", "require",
                "require_once", "return", "static", "switch", "throw", "trait",
                "try", "unset", "use", "var", "while", "xor", "yield", "true",
                "false", "null"
            ),
            builtInTypes = setOf(
                "int", "float", "string", "bool", "array", "object", "mixed", "void",
                "self", "parent"
            )
        )
    }
}
