package io.ma7moud3ly.nemo.syntax.indent

/**
 * A single vertical indentation guide: the line drawn at [column] spanning the
 * rows [startLine]..[endLine] (both zero-based and inclusive).
 *
 * Guides are merged into the longest possible vertical run, so one block body
 * produces one guide rather than one per row.
 *
 * [anchorLine] and [charOffset] locate the guide horizontally: the guide is
 * drawn where character [charOffset] of [anchorLine] begins. Asking the text
 * layout for that position keeps the guide aligned with the glyphs on every
 * platform, whatever font is actually resolved and whether the file is indented
 * with spaces or tabs.
 */
data class IndentGuide(
    val column: Int,
    val startLine: Int,
    val endLine: Int,
    val anchorLine: Int,
    val charOffset: Int
)

/**
 * The indentation guides of a document, plus enough context to resolve which
 * guide the caret currently sits inside.
 *
 * Built by [IndentGuideCalculator.compute]. It is independent of the caret, so
 * it only needs recomputing when the text or the tab size changes;
 * [activeGuide] can then be queried on every caret move.
 */
class IndentGuides internal constructor(
    val guides: List<IndentGuide>,
    private val columnsPerLine: List<IntArray>,
    private val indents: IntArray
) {
    val isEmpty: Boolean get() = guides.isEmpty()

    /**
     * The innermost guide containing [cursorLine], or `null` when the caret is
     * at the top level. When the caret sits on a line that opens a block
     * (`def f():`), the guide of the block it opens is returned.
     */
    fun activeGuide(cursorLine: Int): IndentGuide? {
        if (guides.isEmpty() || cursorLine !in indents.indices) return null

        // A block opener is not itself indented, so anchor on its first body row.
        val below = indents.getOrNull(cursorLine + 1) ?: indents[cursorLine]
        val anchor = if (below > indents[cursorLine]) cursorLine + 1 else cursorLine

        val column = columnsPerLine.getOrNull(anchor)?.lastOrNull() ?: return null
        return guides.firstOrNull { it.column == column && anchor in it.startLine..it.endLine }
    }
}

/**
 * Computes the indentation guides of a document.
 *
 * Guides are what make indentation-scoped languages (Python, MicroPython,
 * YAML) readable: with no braces to close a block, the vertical line is the
 * only visual cue for how deep a statement sits.
 *
 * Levels come from the indentation the file actually uses, not from multiples
 * of the tab size. That is what makes a misaligned line stand out:
 *
 * ```
 * def foo():
 *    print(1)     <- establishes the body at column 3
 *     print(2)    <- deeper, so it gets its own guide at column 3
 * ```
 *
 * `print(2)` is one space past its sibling, so a guide is drawn immediately
 * before it. With fixed tab-size levels both rows would look identical.
 */
object IndentGuideCalculator {

    private val EMPTY = IndentGuides(emptyList(), emptyList(), IntArray(0))

    /**
     * @param tabSize width a tab character expands to, used only to turn the
     *   leading whitespace of a line into a column count.
     */
    fun compute(code: String, tabSize: Int): IndentGuides {
        if (code.isEmpty() || tabSize <= 0) return EMPTY

        val lines = code.split('\n')
        val raw = IntArray(lines.size) { indentWidth(lines[it], tabSize) }
        val indents = effectiveIndents(raw)
        val columnsPerLine = enclosingColumns(indents)

        val columns = mutableSetOf<Int>()
        columnsPerLine.forEach { line -> line.forEach { columns.add(it) } }
        if (columns.isEmpty()) return IndentGuides(emptyList(), columnsPerLine, indents)

        val guides = mutableListOf<IndentGuide>()

        fun addGuide(column: Int, start: Int, end: Int) {
            // Anchor on a row that really has this whitespace: a blank row
            // inherits its indent and has no characters to measure against.
            val anchor = (start..end).firstOrNull { raw[it] > column } ?: start
            guides += IndentGuide(
                column = column,
                startLine = start,
                endLine = end,
                anchorLine = anchor,
                charOffset = charOffsetOfColumn(lines[anchor], column, tabSize)
            )
        }

        for (column in columns.sorted()) {
            var runStart = -1
            for (line in indents.indices) {
                val covered = columnsPerLine[line].any { it == column }
                if (covered && runStart == -1) {
                    runStart = line
                } else if (!covered && runStart != -1) {
                    addGuide(column, runStart, line - 1)
                    runStart = -1
                }
            }
            if (runStart != -1) addGuide(column, runStart, indents.lastIndex)
        }

        return IndentGuides(guides, columnsPerLine, indents)
    }

    /**
     * How many characters into [line] the visual [column] falls, expanding tabs
     * on the way. Used to turn a column into an offset the text layout can
     * resolve to an exact x coordinate.
     */
    private fun charOffsetOfColumn(line: String, column: Int, tabSize: Int): Int {
        if (column <= 0) return 0
        var width = 0
        var index = 0
        while (index < line.length && width < column) {
            when (line[index]) {
                ' ' -> width++
                '\t' -> width += tabSize - (width % tabSize)
                else -> return index
            }
            index++
        }
        return index
    }

    /**
     * For every line, the indent columns of the blocks enclosing it.
     *
     * Walks the document with an indent stack, the same way Python's own
     * tokenizer tracks block depth: a line closes every level at or past its
     * own indent, is guided by whatever remains open, and then opens a level of
     * its own. A line deeper than its sibling therefore inherits that sibling's
     * column as an extra guide.
     */
    private fun enclosingColumns(indents: IntArray): List<IntArray> {
        val result = ArrayList<IntArray>(indents.size)
        val open = ArrayList<Int>()
        for (indent in indents) {
            while (open.isNotEmpty() && open.last() >= indent) open.removeAt(open.lastIndex)
            result += open.toIntArray()
            open += indent
        }
        return result
    }

    /**
     * Indent width of every line, with blank lines resolved so a guide only
     * continues through them when the block does.
     *
     * A blank line takes the *smaller* of the indent above and below it: two
     * statements inside the same block keep the guide running through the gap,
     * while a gap between two top-level definitions correctly breaks it.
     */
    private fun effectiveIndents(raw: IntArray): IntArray {
        val size = raw.size
        val result = IntArray(size)

        var above = 0
        val nearestAbove = IntArray(size)
        for (i in 0 until size) {
            if (raw[i] >= 0) above = raw[i]
            nearestAbove[i] = above
        }

        var below = 0
        for (i in size - 1 downTo 0) {
            if (raw[i] >= 0) {
                below = raw[i]
                result[i] = raw[i]
            } else {
                result[i] = minOf(nearestAbove[i], below)
            }
        }
        return result
    }

    /**
     * Visual width of the leading whitespace of [line], expanding tabs to the
     * next tab stop. Returns `-1` for a blank or whitespace-only line.
     */
    private fun indentWidth(line: String, tabSize: Int): Int {
        var width = 0
        for (char in line) {
            when (char) {
                ' ' -> width++
                '\t' -> width += tabSize - (width % tabSize)
                '\r' -> Unit
                else -> return width
            }
        }
        return -1
    }
}
