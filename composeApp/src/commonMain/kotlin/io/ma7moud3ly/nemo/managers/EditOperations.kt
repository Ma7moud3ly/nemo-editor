package io.ma7moud3ly.nemo.managers

import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.FindReplaceAction
import io.ma7moud3ly.nemo.model.Language

/**
 * Provides various edit operations for the code editor
 */
class EditOperations(
    private val state: CodeState,
    private val settings: EditorSettings
) {

    /**
     * Duplicate the current line
     */
    fun duplicateLine() {
        val code = state.code
        val cursorPos = state.cursorPosition

        // Find current line boundaries
        val lineStart = code.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)) + 1
        val lineEnd = code.indexOf('\n', cursorPos).let { if (it == -1) code.length else it }

        val currentLine = code.substring(lineStart, lineEnd)
        val newText = code.take(lineEnd) + "\n" + currentLine + code.substring(lineEnd)

        // Record for undo
        state.recordAction(
            FindReplaceAction.Insert(lineEnd, "\n$currentLine")
        )

        state.updateText(newText, lineEnd + 1 + currentLine.length)
    }

    /**
     * Delete the current line
     */
    fun deleteLine() {
        val code = state.code
        val cursorPos = state.cursorPosition

        // Find current line boundaries
        val lineStart = code.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)) + 1
        val lineEnd = code.indexOf('\n', cursorPos).let {
            if (it == -1) code.length else it + 1 // Include the newline
        }

        val deletedText = code.substring(lineStart, lineEnd)
        val newText = code.removeRange(lineStart, lineEnd)

        // Record for undo
        state.recordAction(
            FindReplaceAction.Delete(lineStart, deletedText)
        )

        state.updateText(newText, lineStart.coerceAtMost(newText.length))
    }

    /**
     * Indent the current line or selection
     */
    fun indent() {
        val code = state.code
        val selection = state.selection
        val indentString = settings.getIndentString()

        if (selection.collapsed) {
            // Single line indent
            val cursorPos = state.cursorPosition
            val lineStart = code.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)) + 1

            val newText = code.take(lineStart) + indentString + code.substring(lineStart)

            state.recordAction(
                FindReplaceAction.Insert(lineStart, indentString)
            )

            state.updateText(newText, cursorPos + indentString.length)
        } else {
            // Multi-line indent
            indentSelection(true)
        }
    }

    /**
     * Unindent the current line or selection
     */
    fun unindent() {
        val code = state.code
        val selection = state.selection
        val tabSize = settings.tabSizeState.value

        if (selection.collapsed) {
            // Single line unindent
            val cursorPos = state.cursorPosition
            val lineStart = code.lastIndexOf('\n', (cursorPos - 1).coerceAtLeast(0)) + 1

            val removed = removeIndent(code, lineStart, tabSize, settings.useTabsState.value)
            if (removed.removedLength > 0) {
                state.recordAction(
                    FindReplaceAction.Delete(lineStart, removed.removedText)
                )

                state.updateText(
                    removed.newText,
                    (cursorPos - removed.removedLength).coerceAtLeast(lineStart)
                )
            }
        } else {
            // Multi-line unindent
            indentSelection(false)
        }
    }

    /**
     * Toggle line comment for current line or selection
     */
    fun toggleComment() {
        val commentPrefix = getCommentPrefix(state.language)
        if (commentPrefix.isEmpty()) return

        val code = state.code
        val selection = state.selection

        // Get line range
        val startLine = code.take(selection.start).count { it == '\n' }
        val endLine = code.take(selection.end).count { it == '\n' }

        val lines = code.split('\n').toMutableList()

        // Check if all lines are commented
        val allCommented = (startLine..endLine).all { lineIndex ->
            if (lineIndex >= lines.size) return@all false
            lines[lineIndex].trimStart().startsWith(commentPrefix)
        }

        // Toggle comments
        val modifiedLines = lines.toMutableList()
        for (i in startLine..endLine) {
            if (i >= modifiedLines.size) break

            modifiedLines[i] = if (allCommented) {
                // Remove comment
                val trimmed = modifiedLines[i].trimStart()
                val spaces = modifiedLines[i].takeWhile { it.isWhitespace() }
                if (trimmed.startsWith(commentPrefix)) {
                    spaces + trimmed.removePrefix(commentPrefix).trimStart()
                } else {
                    modifiedLines[i]
                }
            } else {
                // Add comment
                val trimmed = modifiedLines[i].trimStart()
                val spaces = modifiedLines[i].takeWhile { it.isWhitespace() }
                "$spaces$commentPrefix $trimmed"
            }
        }

        val oldText = state.code
        val newText = modifiedLines.joinToString("\n")

        if (oldText != newText) {
            state.recordAction(
                FindReplaceAction.Replace(
                    start = 0,
                    end = oldText.length,
                    oldText = oldText,
                    newText = newText
                )
            )

            state.updateText(
                newText,
                state.cursorPosition.coerceAtMost(newText.length)
            )
        }
    }

    // Private helper methods

    private fun indentSelection(increase: Boolean) {
        val code = state.code
        val selection = state.selection
        val indentString = settings.getIndentString()
        val tabSize = settings.tabSizeState.value

        // Get line range
        val startLine = code.take(selection.start).count { it == '\n' }
        val endLine = code.take(selection.end).count { it == '\n' }

        val lines = code.split('\n').toMutableList()

        for (i in startLine..endLine) {
            if (i >= lines.size) break

            lines[i] = if (increase) {
                indentString + lines[i]
            } else {
                val removed = removeIndentFromLine(lines[i], tabSize, settings.useTabsState.value)
                removed
            }
        }

        val oldText = state.code
        val newText = lines.joinToString("\n")

        if (oldText != newText) {
            state.recordAction(
                FindReplaceAction.Replace(
                    start = 0,
                    end = oldText.length,
                    oldText = oldText,
                    newText = newText
                )
            )

            state.updateText(newText, state.cursorPosition.coerceAtMost(newText.length))
        }
    }

    private data class IndentRemoval(
        val newText: String,
        val removedText: String,
        val removedLength: Int
    )

    private fun removeIndent(
        text: String,
        lineStart: Int,
        tabSize: Int,
        useTabs: Boolean
    ): IndentRemoval {
        val lineEnd = text.indexOf('\n', lineStart).let { if (it == -1) text.length else it }
        val line = text.substring(lineStart, lineEnd)

        val (newLine, removed) = when {
            useTabs && line.startsWith('\t') -> {
                line.substring(1) to "\t"
            }

            !useTabs && line.startsWith(" ") -> {
                val spacesToRemove = minOf(tabSize, line.takeWhile { it == ' ' }.length)
                line.substring(spacesToRemove) to " ".repeat(spacesToRemove)
            }

            else -> line to ""
        }

        return if (removed.isNotEmpty()) {
            val newText = text.take(lineStart) + newLine + text.substring(lineEnd)
            IndentRemoval(newText, removed, removed.length)
        } else {
            IndentRemoval(text, "", 0)
        }
    }

    private fun removeIndentFromLine(line: String, tabSize: Int, useTabs: Boolean): String {
        return when {
            useTabs && line.startsWith('\t') -> line.substring(1)
            !useTabs && line.startsWith(" ") -> {
                val spacesToRemove = minOf(tabSize, line.takeWhile { it == ' ' }.length)
                line.substring(spacesToRemove)
            }

            else -> line
        }
    }

    private fun getCommentPrefix(language: Language): String {
        return when (language) {
            Language.KOTLIN, Language.JAVA, Language.JAVASCRIPT, Language.CPP -> "//"
            Language.PYTHON -> "#"
            Language.HTML, Language.XML -> "<!--"
            Language.CSS -> "/*"
            else -> "//"
        }
    }
}