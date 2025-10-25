package com.ma7moud3ly.nemo.managers

import androidx.compose.runtime.*
import com.ma7moud3ly.nemo.model.CodeState
import com.ma7moud3ly.nemo.model.FindReplaceAction

/**
 * Manages find and replace operations with state management
 *
 * @param state The code editor state
 */
class FindAndReplaceManager(
    private val state: CodeState
) {
    // Search parameters
    var findText by mutableStateOf("")
    var replaceText by mutableStateOf("")
    var caseSensitive by mutableStateOf(false)
    var wholeWord by mutableStateOf(false)
    var useRegex by mutableStateOf(false)

    // Results
    var matches by mutableStateOf<List<IntRange>>(emptyList())
        private set
    var currentMatchIndex by mutableIntStateOf(-1)
        private set

    /**
     * Update search and refresh matches
     */
    fun updateSearch() {
        if (findText.isEmpty()) {
            matches = emptyList()
            currentMatchIndex = -1
            return
        }

        matches = findMatches(state.code, findText, caseSensitive, wholeWord, useRegex)

        if (matches.isNotEmpty()) {
            currentMatchIndex = matches.indexOfFirst { it.first >= state.cursorPosition }
            if (currentMatchIndex == -1) currentMatchIndex = 0
            navigateToCurrentMatch()
        } else {
            currentMatchIndex = -1
        }
    }

    /**
     * Navigate to previous match
     */
    fun goToPrevious() {
        if (matches.isEmpty()) return

        currentMatchIndex = if (currentMatchIndex > 0) {
            currentMatchIndex - 1
        } else {
            matches.size - 1
        }
        navigateToCurrentMatch()
    }

    /**
     * Navigate to next match
     */
    fun goToNext() {
        if (matches.isEmpty()) return

        currentMatchIndex = if (currentMatchIndex < matches.size - 1) {
            currentMatchIndex + 1
        } else {
            0
        }
        navigateToCurrentMatch()
    }

    /**
     * Replace current match
     */
    fun replaceCurrent() {
        if (matches.isEmpty() || currentMatchIndex < 0) return

        val match = matches[currentMatchIndex]
        val oldText = state.code
        val newText = oldText.replaceRange(match, replaceText)

        // Record for undo/redo
        state.recordAction(
            FindReplaceAction.Replace(
                start = match.first,
                end = match.last + 1,
                oldText = oldText.substring(match),
                newText = replaceText
            )
        )

        state.updateText(newText, match.first + replaceText.length)

        // Refresh matches
        matches = findMatches(newText, findText, caseSensitive, wholeWord, useRegex)
        if (matches.isNotEmpty()) {
            currentMatchIndex = currentMatchIndex.coerceAtMost(matches.size - 1)
        }
    }

    /**
     * Replace all matches
     */
    fun replaceAll() {
        if (matches.isEmpty()) return

        val oldText = state.code
        val newText = replaceAllMatches(
            oldText,
            findText,
            replaceText,
            caseSensitive,
            wholeWord,
            useRegex
        )

        if (oldText != newText) {
            // Record for undo/redo
            state.recordAction(
                FindReplaceAction.Replace(
                    start = 0,
                    end = oldText.length,
                    oldText = oldText,
                    newText = newText
                )
            )

            state.updateText(newText, 0)
            matches = emptyList()
            currentMatchIndex = -1
        }
    }

    /**
     * Clear search
     */
    fun clear() {
        findText = ""
        replaceText = ""
        matches = emptyList()
        currentMatchIndex = -1
    }

    /**
     * Check if there are matches
     */
    fun hasMatches(): Boolean = matches.isNotEmpty()

    // Private helper methods

    private fun navigateToCurrentMatch() {
        if (matches.isEmpty() || currentMatchIndex < 0) return

        val match = matches[currentMatchIndex]
        state.setSelection(match.first, match.last + 1)
    }

    private fun findMatches(
        text: String,
        searchText: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        useRegex: Boolean
    ): List<IntRange> {
        if (searchText.isEmpty()) return emptyList()

        val matchList = mutableListOf<IntRange>()

        try {
            if (useRegex) {
                val regex = if (caseSensitive) {
                    Regex(searchText)
                } else {
                    Regex(searchText, RegexOption.IGNORE_CASE)
                }

                regex.findAll(text).forEach { match ->
                    matchList.add(match.range)
                }
            } else {
                val searchPattern = if (caseSensitive) searchText else searchText.lowercase()
                val textToSearch = if (caseSensitive) text else text.lowercase()

                var index = 0
                while (index < textToSearch.length) {
                    index = textToSearch.indexOf(searchPattern, index)
                    if (index == -1) break

                    if (wholeWord) {
                        val isWordStart = index == 0 || !textToSearch[index - 1].isLetterOrDigit()
                        val isWordEnd = index + searchPattern.length >= textToSearch.length ||
                                !textToSearch[index + searchPattern.length].isLetterOrDigit()

                        if (isWordStart && isWordEnd) {
                            matchList.add(index until index + searchText.length)
                        }
                    } else {
                        matchList.add(index until index + searchText.length)
                    }

                    index += searchPattern.length
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return matchList
    }

    private fun replaceAllMatches(
        text: String,
        searchText: String,
        replaceText: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        useRegex: Boolean
    ): String {
        if (searchText.isEmpty()) return text

        return try {
            if (useRegex) {
                val regex = if (caseSensitive) {
                    Regex(searchText)
                } else {
                    Regex(searchText, RegexOption.IGNORE_CASE)
                }
                regex.replace(text, replaceText)
            } else {
                val matchList = findMatches(text, searchText, caseSensitive, wholeWord, useRegex)
                var result = text
                var offset = 0

                matchList.forEach { match ->
                    val adjustedStart = match.first + offset
                    val adjustedEnd = match.last + 1 + offset
                    result = result.replaceRange(adjustedStart, adjustedEnd, replaceText)
                    offset += replaceText.length - searchText.length
                }

                result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            text
        }
    }
}

/**
 * Remember a FindAndReplaceManager instance
 */
@Composable
fun rememberFindAndReplaceManager(state: CodeState): FindAndReplaceManager {
    return remember(state) {
        FindAndReplaceManager(state)
    }
}