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
            result = result.replace(Regex("\\b$keyword([a-zA-Z0-9_(])")) { match ->
                "$keyword ${match.groupValues[1]}"
            }
        }
        return result
    }

    override fun addSpacesAroundOperators(line: String): String {
        var result = line

        // Assignment operators
        val assignmentOps = listOf("=", "+=", "-=", "*=", "/=", "%=")
        for (op in assignmentOps) {
            result = result.replace(Regex("\\s*${Regex.escape(op)}\\s*")) { " $op " }
        }

        // Comparison operators (but not generics)
        // Handle == and != first
        result = result.replace(Regex("\\s*==\\s*"), " == ")
        result = result.replace(Regex("\\s*!=\\s*"), " != ")

        // Handle <= and >= (not generics)
        result = result.replace(Regex("([^<>])\\s*<=\\s*"), "$1 <= ")
        result = result.replace(Regex("([^<>])\\s*>=\\s*"), "$1 >= ")

        // Handle < and > only in comparison context (not generics)
        // Only add spaces if NOT part of generic type syntax
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*<\\s*([a-zA-Z0-9_])")) { match ->
            val before = match.groupValues[1]
            val after = match.groupValues[2]
            // Check if this looks like a generic (uppercase letter before <)
            if (before.last().isUpperCase() || after.first().isUpperCase()) {
                "$before<$after" // No spaces for generics
            } else {
                "$before < $after" // Spaces for comparison
            }
        }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*>\\s*([a-zA-Z0-9_])")) { match ->
            val before = match.groupValues[1]
            val after = match.groupValues[2]
            // Check if this looks like end of generic
            if (before.last().isUpperCase() || after.first().isUpperCase()) {
                "$before>$after" // No spaces for generics
            } else {
                "$before > $after" // Spaces for comparison
            }
        }

        // Remove spaces inside generics completely
        result = result.replace(Regex("<\\s+"), "<")
        result = result.replace(Regex("\\s+>"), ">")

        // Arithmetic operators (but not in negative numbers or function parameters)
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*\\+\\s*")) { "${it.groupValues[1]} + " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*-(?!>)\\s*")) { "${it.groupValues[1]} - " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*\\*\\s*")) { "${it.groupValues[1]} * " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*/\\s*")) { "${it.groupValues[1]} / " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*%\\s*")) { "${it.groupValues[1]} % " }

        // Logical operators
        result = result.replace(Regex("\\s*&&\\s*"), " && ")
        result = result.replace(Regex("\\s*\\|\\|\\s*"), " || ")

        // Range operator - no spaces
        result = result.replace(Regex("\\s*\\.\\.\\s*"), "..")

        // Elvis operator
        result = result.replace(Regex("\\s*\\?:\\s*"), " ?: ")

        // Safe call - no spaces
        result = result.replace(Regex("\\s*\\?\\.\\s*"), "?.")
        result = result.replace(Regex("\\s*!!\\s*"), "!!")

        return result
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

    override fun fixFunctionCalls(line: String): String {
        var result = line
        result = result.replace(Regex("([a-zA-Z0-9_])\\s+\\("), "$1(")
        result = result.replace(Regex("\\)(\\{)"), ") $1")
        return result
    }

    private companion object {
        const val OPENING_BRACKETS = "{(["
        const val CLOSING_BRACKETS = "})]"

        /** Starts of a line that continues the expression on the line before. */
        val CONTINUATIONS = listOf(".", "?.", "?:", "&&", "||")

        /** Matches a placeholder left by [maskLiterals], such as `"0"`. */
        val PLACEHOLDER_REGEX = Regex("""(["'])(\d+)\1""")
    }
}
