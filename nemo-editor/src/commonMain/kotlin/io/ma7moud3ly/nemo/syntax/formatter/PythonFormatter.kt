package io.ma7moud3ly.nemo.syntax.formatter

/**
 * Formats Python and MicroPython code in the PEP 8 style.
 *
 * Block indentation follows the indentation the code already has, so its
 * structure never changes, and is rewritten with [format]'s indent unit.
 * Lines inside brackets are continuation lines: after a bracket that ends its
 * line they get a hanging indent, otherwise they line up after the bracket.
 * Spacing is set token by token: one space around binary operators and after
 * commas, none around the `=` of keyword arguments, and two before an inline
 * comment. Strings are kept as written, and so is the text of multi-line
 * strings.
 */
class PythonFormatter : CodeFormatter() {

    override fun format(code: String, tabSize: Int, useTabs: Boolean): String {
        var result = code

        // Step 1: Sort imports
        result = formatImports(result)

        // Step 2: Format code structure with proper indentation
        result = formatCode(result, tabSize, useTabs)

        // Step 3: Remove trailing whitespace
        result = removeTrailingWhitespace(result)

        // Step 4: Ensure newline at end
        result = ensureNewlineAtEnd(result)

        return result
    }

    /** An unclosed bracket, and how the lines inside it are indented. */
    private class OpenBracket(val char: Char, val contentIndent: String, val closingIndent: String)

    override fun formatCode(code: String, tabSize: Int, useTabs: Boolean): String {
        val unit = if (useTabs) "\t" else " ".repeat(tabSize)
        val output = mutableListOf<String>()
        val brackets = ArrayDeque<OpenBracket>()
        // The width of each block indentation level, as written
        val widths = ArrayDeque<Int>().apply { add(0) }
        // The delimiter of the multi-line string the line starts in, if any
        var stringDelimiter: String? = null
        // The last logical line opened a block, as in `if x:`
        var opensBlock = false
        // The last line ended with `\`, so the next one continues it
        var backslash = false
        var logicalIndent = ""
        var blankLines = 0

        for (raw in code.lines()) {
            // The text of a multi-line string is kept exactly as written
            if (stringDelimiter != null) {
                output.add(raw)
                val scan = scanLine(raw, stringDelimiter)
                stringDelimiter = scan.openString
                applyBrackets(raw, scan, brackets, unit, useTabs)
                if (stringDelimiter == null && brackets.isEmpty()) {
                    val codeText = raw.take(scan.codeEnd).trimEnd()
                    backslash = codeText.endsWith("\\")
                    if (!backslash) opensBlock = codeText.endsWith(":")
                }
                continue
            }

            val line = raw.trim()
            if (line.isEmpty()) {
                // No blank lines at the start, and at most two in a row
                if (output.isNotEmpty() && (brackets.isNotEmpty() || ++blankLines <= 2)) {
                    output.add("")
                }
                continue
            }
            blankLines = 0

            val isComment = line.startsWith("#")
            val indent = when {
                brackets.isNotEmpty() ->
                    if (line.first() in CLOSING_BRACKETS) brackets.last().closingIndent
                    else brackets.last().contentIndent

                backslash -> logicalIndent + unit
                else -> {
                    val width = widthOf(raw, tabSize)
                    val level = if (isComment) {
                        commentLevel(widths, width, opensBlock)
                    } else {
                        blockLevel(widths, width, opensBlock)
                    }
                    unit.repeat(level).also { if (!isComment) logicalIndent = it }
                }
            }

            val text = when {
                isComment -> normalizeComment(line)
                // A line that opens a multi-line string is left as written
                scanLine(line, null).openString != null -> line
                else -> formatLine(line, brackets.map { it.char })
            }
            val formatted = indent + text
            output.add(formatted)

            val scan = scanLine(formatted, null)
            stringDelimiter = scan.openString
            applyBrackets(formatted, scan, brackets, unit, useTabs)

            // At the end of a logical line, note whether it opens a block
            if (!isComment && stringDelimiter == null && brackets.isEmpty()) {
                val codeText = formatted.take(scan.codeEnd).trimEnd()
                backslash = codeText.endsWith("\\")
                if (!backslash) opensBlock = codeText.endsWith(":")
            }
        }

        return output.joinToString("\n")
    }

    /** The width of the indentation of [line], with a tab as [tabSize] spaces. */
    private fun widthOf(line: String, tabSize: Int): Int =
        line.takeWhile { it == ' ' || it == '\t' }.sumOf { if (it == '\t') tabSize else 1 }

    /**
     * The block level of a statement indented by [width], updating [widths].
     * A statement right after a line that opens a block is in that block even
     * when it is not indented deeper.
     */
    private fun blockLevel(widths: ArrayDeque<Int>, width: Int, opensBlock: Boolean): Int {
        val deepest = widths.last()
        val effective = if (opensBlock && width <= deepest) deepest + 1 else width
        if (effective > widths.last()) {
            widths.addLast(effective)
        } else {
            while (widths.size > 1 && widths.last() > effective) widths.removeLast()
            if (widths.last() < effective) widths.addLast(effective)
        }
        return widths.size - 1
    }

    /** The level of a comment line indented by [width]. Comments don't open or close blocks. */
    private fun commentLevel(widths: ArrayDeque<Int>, width: Int, opensBlock: Boolean): Int {
        if (opensBlock && width > widths.last()) return widths.size
        return (widths.count { it <= width } - 1).coerceAtLeast(0)
    }

    /**
     * Opens and closes the brackets that [scan] found on [line]. After a
     * bracket that ends the line, the lines inside it get one more indent unit
     * than [line]; otherwise they line up after the bracket. Their closing
     * bracket goes back to the indentation of [line].
     */
    private fun applyBrackets(
        line: String,
        scan: Scan,
        brackets: ArrayDeque<OpenBracket>,
        unit: String,
        useTabs: Boolean
    ) {
        val lineIndent = line.takeWhile { it == ' ' || it == '\t' }
        for ((bracket, index) in scan.brackets) {
            if (bracket in CLOSING_BRACKETS) {
                brackets.removeLastOrNull()
                continue
            }
            val hanging = line.substring(index + 1, scan.codeEnd).isBlank()
            val contentIndent = if (hanging || useTabs) lineIndent + unit else " ".repeat(index + 1)
            brackets.addLast(OpenBracket(bracket, contentIndent, lineIndent))
        }
    }

    /**
     * What a line holds outside strings and comments: its [brackets] with their
     * index, where its code ends and its comment starts ([codeEnd]), and the
     * delimiter of the multi-line string it leaves open, if any.
     */
    private class Scan(val brackets: List<Pair<Char, Int>>, val codeEnd: Int, val openString: String?)

    /** Scans [line], which starts inside the multi-line string closed by [delimiter], if any. */
    private fun scanLine(line: String, delimiter: String?): Scan {
        val brackets = mutableListOf<Pair<Char, Int>>()
        var open = delimiter
        var i = 0
        while (i < line.length) {
            if (open != null) {
                val end = delimiterIndex(line, i, open)
                if (end == -1) return Scan(brackets, line.length, open)
                i = end + open.length
                open = null
                continue
            }
            val c = line[i]
            when {
                c == '#' -> return Scan(brackets, i, null)
                c == '"' || c == '\'' -> {
                    val triple = "$c$c$c"
                    if (line.startsWith(triple, i)) {
                        open = triple
                        i += 3
                    } else {
                        i = stringEnd(line, i)
                    }
                }

                c in OPENING_BRACKETS || c in CLOSING_BRACKETS -> {
                    brackets.add(c to i)
                    i++
                }

                else -> i++
            }
        }
        return Scan(brackets, line.length, open)
    }

    /** Index of the [delimiter] at or after [from] that is not escaped, or -1. */
    private fun delimiterIndex(line: String, from: Int, delimiter: String): Int {
        var i = from
        while (i < line.length) {
            when {
                line[i] == '\\' -> i += 2
                line.startsWith(delimiter, i) -> return i
                else -> i++
            }
        }
        return -1
    }

    /** Index just past the string that starts with the quote at [start], or the end of [line]. */
    private fun stringEnd(line: String, start: Int): Int {
        val quote = line[start]
        val triple = "$quote$quote$quote"
        if (line.startsWith(triple, start)) {
            val end = delimiterIndex(line, start + 3, triple)
            return if (end == -1) line.length else end + 3
        }
        var i = start + 1
        while (i < line.length) {
            when (line[i]) {
                '\\' -> i += 2
                quote -> return i + 1
                else -> i++
            }
        }
        return line.length
    }

    /**
     * Sorts each run of consecutive top-level imports, `from` imports first,
     * and drops duplicates. Imports stay where they are, so an import inside
     * `try:` or a function is never moved.
     */
    override fun formatImports(code: String): String {
        val result = mutableListOf<String>()
        val run = mutableListOf<String>()
        fun flushRun() {
            val fromImports = run.filter { it.startsWith("from ") }.sorted()
            val imports = run.filter { it.startsWith("import ") }.sorted()
            result.addAll((fromImports + imports).distinct())
            run.clear()
        }

        var delimiter: String? = null
        for (line in code.lines()) {
            val inString = delimiter != null
            delimiter = scanLine(line, delimiter).openString
            if (!inString && delimiter == null && isSimpleImport(line)) {
                run.add(line.trimEnd())
            } else {
                flushRun()
                result.add(line)
            }
        }
        flushRun()

        return result.joinToString("\n")
    }

    /** True for a top-level import that fits on its one line. */
    private fun isSimpleImport(line: String): Boolean =
        (line.startsWith("import ") || line.startsWith("from ")) &&
                '(' !in line && ';' !in line && !line.trimEnd().endsWith("\\")

    override fun removeTrailingWhitespace(code: String): String {
        return code.lines().joinToString("\n") { it.trimEnd() }
    }

    override fun ensureNewlineAtEnd(code: String): String {
        return code.trimEnd('\n') + "\n"
    }

    override fun formatLine(line: String): String = formatLine(line, emptyList())

    /** A bracket open in the line being formatted. */
    private class Frame(val char: Char, var annotated: Boolean = false)

    /** What a token does in its line, which decides the spaces around it. */
    private enum class Role {
        ATOM, OPEN, CLOSE, COMMA, DOT, COLON, SLICE, LAMBDA_COLON,
        KEYWORD_ARGUMENT, BINARY, UNARY, DECORATOR, BACKSLASH
    }

    private enum class Kind { NAME, NUMBER, STRING, OP }

    private class Token(val text: String, val kind: Kind) {
        val isKeyword: Boolean get() = kind == Kind.NAME && text in KEYWORDS
    }

    /**
     * Sets the spaces between the tokens of [line], which starts inside the
     * [enclosing] brackets left open by the lines before it.
     */
    private fun formatLine(line: String, enclosing: List<Char>): String {
        val (tokens, comment) = tokenize(line)
        val code = StringBuilder()
        val frames = ArrayDeque<Frame>()
        enclosing.forEach { frames.addLast(Frame(it)) }
        // The bracket depth of each lambda whose parameters are being read
        val lambdas = ArrayDeque<Int>()
        var previous: Token? = null
        var previousRole = Role.ATOM

        for (token in tokens) {
            val role = roleOf(token, previous, previousRole, frames, lambdas)
            if (previous != null && needsSpace(previous, previousRole, token, role)) code.append(' ')
            code.append(token.text)

            when {
                token.isKeyword && token.text == "lambda" -> lambdas.addLast(frames.size)
                role == Role.OPEN -> frames.addLast(Frame(token.text[0]))
                role == Role.CLOSE -> {
                    frames.removeLastOrNull()
                    while (lambdas.isNotEmpty() && lambdas.last() > frames.size) lambdas.removeLast()
                }

                role == Role.COMMA -> frames.lastOrNull()?.annotated = false
                role == Role.LAMBDA_COLON -> lambdas.removeLastOrNull()
                role == Role.COLON -> frames.lastOrNull()?.let { if (it.char == '(') it.annotated = true }
            }
            previous = token
            previousRole = role
        }

        val text = code.toString()
        return when {
            comment.isEmpty() -> text
            text.isEmpty() -> normalizeComment(comment)
            else -> "$text  ${normalizeComment(comment)}"
        }
    }

    private fun roleOf(
        token: Token,
        previous: Token?,
        previousRole: Role,
        frames: ArrayDeque<Frame>,
        lambdas: ArrayDeque<Int>
    ): Role {
        if (token.kind != Kind.OP) return Role.ATOM
        val inLambdaParameters = lambdas.lastOrNull() == frames.size
        val top = frames.lastOrNull()
        return when (token.text) {
            "(", "[", "{" -> Role.OPEN
            ")", "]", "}" -> Role.CLOSE
            ",", ";" -> Role.COMMA
            "." -> Role.DOT
            "\\" -> Role.BACKSLASH
            ":" -> when {
                inLambdaParameters -> Role.LAMBDA_COLON
                top?.char == '[' -> Role.SLICE
                else -> Role.COLON
            }

            // `f(end="")` and `lambda x=1: x`, but `def f(x: int = 1)`
            "=" -> if (inLambdaParameters || (top != null && top.char == '(' && !top.annotated)) {
                Role.KEYWORD_ARGUMENT
            } else {
                Role.BINARY
            }

            "~" -> Role.UNARY
            // `-x`, `*args`, `**kwargs`
            "-", "+", "*", "**" ->
                if (startsOperand(previous, previousRole)) Role.UNARY else Role.BINARY

            "@" -> if (previous == null && frames.isEmpty()) Role.DECORATOR else Role.BINARY
            else -> Role.BINARY
        }
    }

    /** True when the token after [previous] starts an operand, so `-` there is a sign. */
    private fun startsOperand(previous: Token?, previousRole: Role): Boolean = when {
        previous == null -> true
        previous.isKeyword -> previous.text !in VALUE_KEYWORDS
        else -> previousRole != Role.ATOM && previousRole != Role.CLOSE
    }

    private fun needsSpace(a: Token, aRole: Role, b: Token, bRole: Role): Boolean = when {
        bRole == Role.CLOSE || bRole == Role.COMMA -> false
        bRole == Role.COLON || bRole == Role.SLICE || bRole == Role.LAMBDA_COLON -> false
        aRole == Role.OPEN -> false
        bRole == Role.BACKSLASH -> true
        aRole == Role.COMMA || aRole == Role.COLON || aRole == Role.LAMBDA_COLON -> true
        aRole == Role.SLICE -> false
        aRole == Role.UNARY || aRole == Role.DECORATOR -> false
        aRole == Role.KEYWORD_ARGUMENT || bRole == Role.KEYWORD_ARGUMENT -> false
        // `from . import x`, `from .module import x`
        aRole == Role.DOT -> b.text == "import"
        bRole == Role.DOT -> a.text == "from" || a.text == "import"
        // No space before the bracket of a call or an index, as in `f(x)` or `a[0]`
        bRole == Role.OPEN -> a.isKeyword || (a.kind == Kind.OP && aRole != Role.CLOSE)
        else -> true
    }

    /**
     * Splits the code of [line] into tokens, and returns them with the
     * trailing comment, or an empty string when there is none.
     */
    private fun tokenize(line: String): Pair<List<Token>, String> {
        val tokens = mutableListOf<Token>()
        var i = 0
        while (i < line.length) {
            val c = line[i]
            when {
                c.isWhitespace() -> i++
                c == '#' -> return tokens to line.substring(i)
                c == '"' || c == '\'' -> {
                    val end = stringEnd(line, i)
                    tokens.add(Token(line.substring(i, end), Kind.STRING))
                    i = end
                }

                c.isLetter() || c == '_' -> {
                    var end = i
                    while (end < line.length && (line[end].isLetterOrDigit() || line[end] == '_')) end++
                    val word = line.substring(i, end)
                    // A prefixed string, such as f"{x}" or rb'\d'
                    if (end < line.length && line[end] in "\"'" && word.lowercase() in STRING_PREFIXES) {
                        val stringEnd = stringEnd(line, end)
                        tokens.add(Token(line.substring(i, stringEnd), Kind.STRING))
                        i = stringEnd
                    } else {
                        tokens.add(Token(word, Kind.NAME))
                        i = end
                    }
                }

                c.isDigit() || (c == '.' && i + 1 < line.length && line[i + 1].isDigit()) -> {
                    val end = numberEnd(line, i)
                    tokens.add(Token(line.substring(i, end), Kind.NUMBER))
                    i = end
                }

                line.startsWith("...", i) -> {
                    tokens.add(Token("...", Kind.NAME))
                    i += 3
                }

                else -> {
                    val op = OPERATORS.firstOrNull { line.startsWith(it, i) } ?: c.toString()
                    tokens.add(Token(op, Kind.OP))
                    i += op.length
                }
            }
        }
        return tokens to ""
    }

    /** Index just past the number at [start], such as `0x1F`, `1_000` or `1.5e-3j`. */
    private fun numberEnd(line: String, start: Int): Int {
        val isHex = line.startsWith("0x", start, ignoreCase = true)
        var i = start
        while (i < line.length) {
            val c = line[i]
            i += when {
                c.isLetterOrDigit() || c == '_' || c == '.' -> 1
                // The sign of an exponent, as in 1e-5
                (c == '+' || c == '-') && !isHex && line[i - 1] in "eE" -> 1
                else -> break
            }
        }
        return i
    }

    /** A comment with a space after its `#`, as in `# note`. */
    private fun normalizeComment(comment: String): String =
        if (comment.length > 1 && comment[1].isLetterOrDigit()) "# ${comment.substring(1)}" else comment

    private companion object {
        const val OPENING_BRACKETS = "([{"
        const val CLOSING_BRACKETS = ")]}"

        val KEYWORDS = setOf(
            "False", "None", "True", "and", "as", "assert", "async", "await",
            "break", "class", "continue", "def", "del", "elif", "else", "except",
            "finally", "for", "from", "global", "if", "import", "in", "is",
            "lambda", "nonlocal", "not", "or", "pass", "raise", "return", "try",
            "while", "with", "yield"
        )

        /** Keywords that are values, so an operator after them is binary, as in `True - 1`. */
        val VALUE_KEYWORDS = setOf("False", "None", "True")

        val STRING_PREFIXES = setOf("r", "u", "b", "f", "br", "rb", "fr", "rf")

        /** Operators of more than one character, longest first. */
        val OPERATORS = listOf(
            "**=", "//=", ">>=", "<<=", "->", ":=", "==", "!=", "<=", ">=", "**",
            "//", "<<", ">>", "+=", "-=", "*=", "/=", "%=", "&=", "|=", "^=", "@="
        )
    }
}
