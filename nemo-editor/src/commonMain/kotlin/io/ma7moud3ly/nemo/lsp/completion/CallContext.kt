package io.ma7moud3ly.nemo.lsp.completion

/** How far back to look for the call around the cursor. */
private const val MAX_LOOK_BACK = 2000

/** Matches a named argument, such as `mode=` in `Pin(2, mode=…`. */
private val NAMED_ARGUMENT_REGEX = Regex("""[(,]\s*(\w+)\s*=(?!=)""")

/**
 * Index of the `(` of the argument list in which the word at [wordStart]
 * starts an argument, or null when the word is not at the start of one, such
 * as inside an argument's value or a lambda.
 */
internal fun argumentListStart(text: String, wordStart: Int): Int? {
    var depth = 0
    var argumentStart = -1
    val limit = (wordStart - MAX_LOOK_BACK).coerceAtLeast(0)
    for (i in wordStart - 1 downTo limit) {
        when (text[i]) {
            ')', ']', '}' -> depth++
            '[', '{' -> if (depth == 0) return null else depth--
            ',' -> if (depth == 0 && argumentStart == -1) argumentStart = i
            '(' -> {
                if (depth > 0) {
                    depth--
                    continue
                }
                if (argumentStart == -1) argumentStart = i
                return i.takeIf { text.substring(argumentStart + 1, wordStart).isBlank() }
            }
        }
    }
    return null
}

/** Names of the arguments already named between the `(` at [open] and [end]. */
internal fun namedArguments(text: String, open: Int, end: Int): Set<String> =
    NAMED_ARGUMENT_REGEX.findAll(text.substring(open, end)).map { it.groupValues[1] }.toSet()

/** Index of the `(` that the `)` at [close] closes, or null when there is none. */
internal fun openingParenOf(text: String, close: Int): Int? {
    var depth = 0
    for (i in close downTo 0) {
        when (text[i]) {
            ')' -> depth++
            '(' -> if (--depth == 0) return i
        }
    }
    return null
}

/** The name that ends at [end], or an empty string when there is none. */
internal fun identifierEndingAt(text: String, end: Int): String {
    var start = end
    while (start >= 0 && (text[start].isLetterOrDigit() || text[start] == '_')) start--
    return text.substring(start + 1, end + 1)
}

/** Index of the last non-whitespace character at or before [index], or -1. */
internal fun skipWhitespaceBack(text: String, index: Int): Int {
    var i = index
    while (i >= 0 && text[i].isWhitespace()) i--
    return i
}
