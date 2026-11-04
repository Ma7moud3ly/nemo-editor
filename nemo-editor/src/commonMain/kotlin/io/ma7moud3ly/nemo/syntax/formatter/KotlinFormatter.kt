package io.ma7moud3ly.nemo.syntax.formatter

class KotlinFormatter : CodeFormatter() {

    override fun format(code: String, tabSize: Int, useTabs: Boolean): String {
        var result = code

        // Step 1: Format imports
        result = formatImports(result)

        // Step 2: Format code structure
        result = formatCode(result, tabSize, useTabs)

        // Step 3: Remove trailing whitespace
        result = removeTrailingWhitespace(result)

        // Step 4: Ensure newline at end
        result = ensureNewlineAtEnd(result)

        return result
    }

    /**
     * Indents each line by the brackets left open before it. A bracket opened
     * anywhere on a line, not only at its end, indents the lines after it by
     * one level relative to that line, so `Column(modifier = m,` indents the
     * next argument and the `) {` that closes it. Brackets in strings and
     * comments are ignored, a line starting with `.`, `?.`, `?:`, `&&` or `||`
     * continues the previous one, and text inside a multi-line string is kept
     * as written.
     */
    override fun formatCode(code: String, tabSize: Int, useTabs: Boolean): String {
        val formatted = mutableListOf<String>()
        val indent = if (useTabs) "\t" else " ".repeat(tabSize)
        // The indent level of the line each unclosed bracket was opened on
        val openers = ArrayDeque<Int>()
        var state = ScanState.CODE

        for (rawLine in code.lines()) {
            val startState = state
            val (brackets, endState) = scanBrackets(rawLine, startState)
            state = endState
            val base = openers.lastOrNull()?.plus(1) ?: 0

            // The text of a multi-line string is kept exactly as written
            if (startState == ScanState.RAW_STRING) {
                formatted.add(rawLine)
                applyBrackets(brackets, openers, base)
                continue
            }

            val line = rawLine.trim()
            if (line.isEmpty()) {
                formatted.add("")
                continue
            }

            val level = when {
                startState == ScanState.BLOCK_COMMENT -> base
                line.first() in CLOSING_BRACKETS -> openers.lastOrNull() ?: 0
                CONTINUATIONS.any { line.startsWith(it) } -> base + 1
                else -> base
            }

            val text = when {
                // Inside a comment, align the leading `*` of KDoc lines
                startState == ScanState.BLOCK_COMMENT -> if (line.startsWith("*")) " $line" else line
                // A comment line, or a line that opens a comment or a multi-line string
                line.startsWith("//") || endState != ScanState.CODE -> line
                else -> formatLine(line)
            }
            formatted.add(indent.repeat(level) + text)

            applyBrackets(brackets, openers, level)
        }

        return formatted.joinToString("\n")
    }

    /**
     * Opens and closes [brackets] in order. Brackets opened on a line take the
     * level of that line, or the lower level of a bracket the line closed first,
     * so the lambda in `foo(a,\n    b) {` indents relative to `foo`.
     */
    private fun applyBrackets(brackets: List<Char>, openers: ArrayDeque<Int>, level: Int) {
        var anchor = level
        for (bracket in brackets) {
            if (bracket in CLOSING_BRACKETS) {
                openers.removeLastOrNull()?.let { anchor = minOf(anchor, it) }
            } else {
                openers.addLast(anchor)
            }
        }
    }

    /** Where a line starts or ends, as far as strings and comments go. */
    private enum class ScanState { CODE, BLOCK_COMMENT, RAW_STRING }

    /**
     * The brackets of [line] that are outside strings, characters and comments,
     * in order, and the state the line ends in, given that it starts in [start].
     */
    private fun scanBrackets(line: String, start: ScanState): Pair<List<Char>, ScanState> {
        val brackets = mutableListOf<Char>()
        var state = start
        var i = 0
        while (i < line.length) {
            when (state) {
                ScanState.BLOCK_COMMENT -> {
                    val end = line.indexOf("*/", i)
                    if (end == -1) return brackets to state
                    i = end + 2
                    state = ScanState.CODE
                }

                ScanState.RAW_STRING -> {
                    val end = line.indexOf("\"\"\"", i)
                    if (end == -1) return brackets to state
                    i = end + 3
                    // A raw string may end with extra quotes, as in """a""""
                    while (i < line.length && line[i] == '"') i++
                    state = ScanState.CODE
                }

                ScanState.CODE -> when {
                    line.startsWith("//", i) -> return brackets to state
                    line.startsWith("/*", i) -> {
                        state = ScanState.BLOCK_COMMENT
                        i += 2
                    }

                    line.startsWith("\"\"\"", i) -> {
                        state = ScanState.RAW_STRING
                        i += 3
                    }

                    line[i] == '"' || line[i] == '\'' -> i = literalEnd(line, i)
                    else -> {
                        if (line[i] in OPENING_BRACKETS || line[i] in CLOSING_BRACKETS) {
                            brackets.add(line[i])
                        }
                        i++
                    }
                }
            }
        }
        return brackets to state
    }

    /**
     * Index just past the string or character literal that starts at [start],
     * or the end of [line] when it is not closed. Templates such as
     * `"${map["key"]}"` are skipped as a whole.
     */
    private fun literalEnd(line: String, start: Int): Int {
        val quote = line[start]
        var i = start + 1
        while (i < line.length) {
            when {
                line[i] == '\\' -> i += 2
                line[i] == quote -> return i + 1
                quote == '"' && line.startsWith("\${", i) -> i = templateEnd(line, i + 2)
                else -> i++
            }
        }
        return line.length
    }

    /** Index just past the `}` that closes the template opened before [start]. */
    private fun templateEnd(line: String, start: Int): Int {
        var depth = 1
        var i = start
        while (i < line.length && depth > 0) {
            when (line[i]) {
                '{' -> depth++
                '}' -> depth--
                '"', '\'' -> {
                    i = literalEnd(line, i)
                    continue
                }
            }
            i++
        }
        return i
    }

    /**
     * [line] with its string and character literals and inline comments
     * replaced by numbered placeholders such as `"0"`, the trailing `//`
     * comment, and the replaced text, which [restoreLiterals] puts back.
     */
    private fun maskLiterals(line: String): Triple<String, String, List<String>> {
        val code = StringBuilder()
        val literals = mutableListOf<String>()
        var i = 0
        while (i < line.length) {
            val end = when {
                line.startsWith("//", i) -> return Triple(code.toString(), line.substring(i), literals)
                line.startsWith("/*", i) ->
                    line.indexOf("*/", i + 2).let { if (it == -1) line.length else it + 2 }

                line.startsWith("\"\"\"", i) ->
                    line.indexOf("\"\"\"", i + 3).let { if (it == -1) line.length else it + 3 }

                line[i] == '"' || line[i] == '\'' -> literalEnd(line, i)
                else -> {
                    code.append(line[i])
                    i++
                    continue
                }
            }
            val quote = if (line[i] == '\'') '\'' else '"'
            code.append(quote).append(literals.size).append(quote)
            literals.add(line.substring(i, end))
            i = end
        }
        return Triple(code.toString(), "", literals)
    }

    /** Puts back the text that [maskLiterals] replaced with placeholders. */
    private fun restoreLiterals(code: String, literals: List<String>): String =
        PLACEHOLDER_REGEX.replace(code) { match ->
            literals.getOrNull(match.groupValues[2].toInt()) ?: match.value
        }

    override fun formatImports(code: String): String {
        val lines = code.lines().toMutableList()
        val imports = mutableListOf<String>()
        val nonImports = mutableListOf<String>()
        var hasPackage = false
        var packageLine = ""

        for (line in lines) {
            when {
                line.trim().startsWith("package ") -> {
                    hasPackage = true
                    packageLine = line.trim()
                }

                line.trim().startsWith("import ") -> {
                    imports.add(line.trim())
                }

                else -> {
                    nonImports.add(line)
                }
            }
        }

        imports.sort()
        val uniqueImports = imports.distinct()

        val result = mutableListOf<String>()

        // Add package line
        if (hasPackage) {
            result.add(packageLine)
            result.add("")
        }

        // Add imports
        if (uniqueImports.isNotEmpty()) {
            result.addAll(uniqueImports)
            result.add("")
        }

        // Add rest of code, but skip empty lines at the start
        var startAdding = false
        for (line in nonImports) {
            if (!startAdding && line.trim().isEmpty()) {
                continue
            }
            startAdding = true
            result.add(line)
        }

        return result.joinToString("\n")
    }

    override fun removeTrailingWhitespace(code: String): String {
        return code.lines().joinToString("\n") { it.trimEnd() }
    }

    override fun ensureNewlineAtEnd(code: String): String {
        return if (code.endsWith("\n")) code else "$code\n"
    }

    /**
     * Fixes the spacing of [line]. Strings, characters and comments are kept
     * as written, so `"a=b"` stays as it is.
     */
    override fun formatLine(line: String): String {
        val (code, comment, literals) = maskLiterals(line)

        var result = code
        result = addSpaceAfterKeywords(result)
        result = addSpacesAroundOperators(result)
        result = addSpaceAfterCommas(result)
        result = addSpaceBeforeOpeningBrace(result)
        result = removeExtraSpaces(result)
        result = fixFunctionCalls(result)
        result = restoreLiterals(result, literals).trimEnd()

        return when {
            comment.isEmpty() -> result
            result.isEmpty() -> comment
            else -> "$result $comment"
        }
    }

    override fun addSpaceAfterKeywords(line: String): String {
        val keywords = listOf(
            "if", "else", "when", "for", "while", "do", "return",
            "fun", "val", "var", "class", "interface", "object",
            "try", "catch", "finally", "throw", "import", "package"
        )

        var result = line
        for (keyword in keywords) {
            result = result.replace(Regex("\\b$keyword(\\()")) { match ->
                "$keyword ${match.groupValues[1]}"
            }
        }
        return result
    }

    /**
     * Spaces the operators of [line] in one pass, reading each operator whole,
     * so `==` is never split into `= =`.
     *
     * Assignments, comparisons, `&&`, `||`, `->` and `?:` get one space on each
     * side. `+ - * / %` are spaced only between two operands, so signs (`-x`,
     * `return -1`), spread (`*args`), `import a.*` and exponents (`1e-5`) stay
     * as they are. `..`, `..<` and `?.` lose their spaces. Angle brackets of a
     * generic, such as `List<Int>`, are left as written.
     */
    override fun addSpacesAroundOperators(line: String): String {
        val generic = genericAngleBrackets(line)
        val result = StringBuilder()
        var i = 0
        while (i < line.length) {
            val op = if (i in generic) null else OPERATORS.firstOrNull { line.startsWith(it, i) }
            if (op == null) {
                result.append(line[i])
                i++
                continue
            }

            val spaced = when (op) {
                in SPACED_OPERATORS -> true
                "+", "-", "*", "/", "%" -> isBinary(line, i, op)
                else -> false
            }
            when {
                spaced -> {
                    trimEndSpaces(result)
                    if (result.isNotEmpty()) result.append(' ')
                    result.append(op)
                    i = skipSpaces(line, i + op.length)
                    if (i < line.length) result.append(' ')
                }

                op in TIGHT_OPERATORS -> {
                    trimEndSpaces(result)
                    result.append(op)
                    i = skipSpaces(line, i + op.length)
                }

                op == "!!" -> {
                    trimEndSpaces(result)
                    result.append(op)
                    i += op.length
                }

                else -> {
                    result.append(op)
                    i += op.length
                }
            }
        }
        return result.toString()
    }

    /**
     * True when the [op] at [index] of [line] sits between two operands, as in
     * `a - b`, rather than starting one, as in `-b`, `*args` or `1e-5`.
     */
    private fun isBinary(line: String, index: Int, op: String): Boolean {
        var end = index - 1
        while (end >= 0 && line[end] == ' ') end--
        if (end < 0) return false
        val before = line[end]

        if (before == ')') {
            // `if (x) -1`: the parenthesis closes a condition, not an operand
            val open = openingParenthesis(line, end)
            return open < 0 || wordBefore(line, open) !in CONDITION_KEYWORDS
        }
        if (before == ']' || before == '}' || before == '"' || before == '\'') return true
        if (!before.isLetterOrDigit() && before != '_') return false

        var start = end
        while (start > 0 && (line[start - 1].isLetterOrDigit() || line[start - 1] == '_' || line[start - 1] == '.')) {
            start--
        }
        val word = line.substring(start, end + 1)

        // The sign of an exponent, as in 1e-5 or 2.5E+3
        val isExponent = (op == "+" || op == "-") && end == index - 1 &&
                (before == 'e' || before == 'E') && word.first().isDigit() &&
                !word.startsWith("0x", ignoreCase = true) && !word.startsWith("0b", ignoreCase = true)
        if (isExponent) return false

        // `return -1`, `in -5..5`, `else -1`
        return word.substringAfterLast('.') !in WORDS_BEFORE_OPERAND
    }

    /** Index of the `(` that the `)` at [close] closes, or -1. */
    private fun openingParenthesis(line: String, close: Int): Int {
        var depth = 0
        for (i in close downTo 0) {
            when (line[i]) {
                ')' -> depth++
                '(' -> if (--depth == 0) return i
            }
        }
        return -1
    }

    /** The word just before [index], skipping spaces, or an empty string. */
    private fun wordBefore(line: String, index: Int): String {
        var end = index - 1
        while (end >= 0 && line[end] == ' ') end--
        var start = end
        while (start >= 0 && (line[start].isLetterOrDigit() || line[start] == '_')) start--
        return line.substring(start + 1, end + 1)
    }

    /**
     * Indexes of the `<` and `>` of [line] that form a generic, such as in
     * `List<Int>`, `fun <T : Any>` or `Map<String, () -> Unit>`. A pair counts
     * only when it holds nothing but types, with no space just inside it, so a
     * comparison such as `a < b` is never taken for one.
     */
    private fun genericAngleBrackets(line: String): Set<Int> {
        val result = mutableSetOf<Int>()
        for (start in line.indices) {
            if (line[start] != '<' || start in result) continue
            if (line.startsWith("<=", start) || line.getOrNull(start + 1) == ' ') continue
            // `..<` is a range, not a generic
            if (start >= 2 && line.startsWith("..", start - 2)) continue

            val brackets = mutableListOf(start)
            var depth = 1
            var parentheses = 0
            var i = start + 1
            while (i < line.length) {
                val c = line[i]
                when {
                    c == '-' && line.getOrNull(i + 1) == '>' -> i++ // `->` of a function type
                    c == '<' -> {
                        depth++
                        brackets.add(i)
                    }

                    c == '>' -> {
                        if (line[i - 1] == ' ') break
                        depth--
                        brackets.add(i)
                        if (depth == 0) {
                            if (parentheses == 0) result.addAll(brackets)
                            break
                        }
                    }

                    c == '(' -> parentheses++
                    c == ')' -> if (--parentheses < 0) break
                    c.isLetterOrDigit() || c in " _.,?*:@" -> Unit
                    else -> break
                }
                i++
            }
        }
        return result
    }

    private fun trimEndSpaces(text: StringBuilder) {
        while (text.isNotEmpty() && text.last() == ' ') text.setLength(text.length - 1)
    }

    private fun skipSpaces(line: String, from: Int): Int {
        var i = from
        while (i < line.length && line[i] == ' ') i++
        return i
    }

    override fun addSpaceAfterCommas(line: String): String {
        return line.replace(Regex(",(?!\\s)"), ", ")
    }

    // Only for Kotlin: add space before brace
    fun addSpaceBeforeOpeningBrace(line: String): String {
        return line.replace(Regex("(\\S)\\{"), "$1 {")
    }

    override fun removeExtraSpaces(line: String): String {
        var result = line
        // Remove spaces before semicolons, commas, and closing brackets
        result = result.replace(Regex("\\s+([;,)])"), "$1")
        // Remove spaces after opening brackets
        result = result.replace(Regex("([\\[\\(])\\s+"), "$1")
        // Replace multiple spaces with single space (but not at start of line)
        result = result.replace(Regex("([^ ]) {2,}"), "$1 ")
        return result
    }

    /**
     * Removes the space between a function and its `(`, as in `foo (x)`, but
     * keeps it after a keyword, as in `if (x)` or `val (a, b) = pair`.
     */
    override fun fixFunctionCalls(line: String): String {
        var result = line
        result = result.replace(Regex("(\\w+)\\s+\\(")) { match ->
            val word = match.groupValues[1]
            if (word in KEYWORDS_BEFORE_PARENTHESIS) "$word (" else "$word("
        }
        result = result.replace(Regex("\\)(\\{)"), ") $1")
        return result
    }

    private companion object {
        const val OPENING_BRACKETS = "{(["
        const val CLOSING_BRACKETS = "})]"

        /** Operators of more than one character come first, so each is read whole. */
        val OPERATORS = listOf(
            "===", "!==", "..<", "?:", "?.", "!!", "::", "->", "==", "!=", "<=", ">=",
            "&&", "||", "+=", "-=", "*=", "/=", "%=", "++", "--", "..",
            "=", "+", "-", "*", "/", "%", "<", ">", "!"
        )

        /** Operators with one space on each side. */
        val SPACED_OPERATORS = setOf(
            "===", "!==", "?:", "->", "==", "!=", "<=", ">=", "&&", "||",
            "+=", "-=", "*=", "/=", "%=", "=", "<", ">"
        )

        /** Operators with no space around them. */
        val TIGHT_OPERATORS = setOf("..", "..<", "?.")

        /** Words after which `-`, `+` and `*` start an operand, as in `return -1`. */
        val WORDS_BEFORE_OPERAND = setOf(
            "return", "in", "is", "else", "throw", "to", "until", "downTo", "step",
            "and", "or", "xor"
        )

        /** Keywords whose `(…)` holds a condition, as in `if (x) -1`. */
        val CONDITION_KEYWORDS = setOf("if", "while", "when")

        /** Keywords followed by a space before `(`, as in `if (x)`. `fun(x: Int)` has none. */
        val KEYWORDS_BEFORE_PARENTHESIS = setOf(
            "if", "else", "when", "for", "while", "do", "return", "val", "var",
            "try", "catch", "finally", "throw"
        )

        /** Starts of a line that continues the expression on the line before. */
        val CONTINUATIONS = listOf(".", "?.", "?:", "&&", "||")

        /** Matches a placeholder left by [maskLiterals], such as `"0"`. */
        val PLACEHOLDER_REGEX = Regex("""(["'])(\d+)\1""")
    }
}
