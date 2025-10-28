package io.ma7moud3ly.nemo.lsp.completion

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import io.ma7moud3ly.nemo.model.CompletionItem
import kotlin.time.TimeSource

class AutocompleteState {
    var items by mutableStateOf<List<CompletionItem>>(emptyList())
    var selectedIndex by mutableIntStateOf(0)
    var isVisible by mutableStateOf(false)

    // Track last completion time to prevent immediate re-trigger (KMP compatible)
    private val timeSource = TimeSource.Monotonic
    private var lastCompletionMark: TimeSource.Monotonic.ValueTimeMark? = null
    private val completionCooldownMs = 300L // milliseconds

    fun show(completions: List<CompletionItem>) {
        // Don't show if we just completed something recently
        lastCompletionMark?.let { mark ->
            val elapsed = mark.elapsedNow().inWholeMilliseconds
            if (elapsed < completionCooldownMs) {
                return
            }
        }

        items = completions
        selectedIndex = 0
        isVisible = completions.isNotEmpty()
    }

    fun hide() {
        isVisible = false
        items = emptyList()
        selectedIndex = 0
    }

    fun selectNext() {
        if (items.isNotEmpty()) {
            selectedIndex = (selectedIndex + 1) % items.size
        }
    }

    fun selectPrevious() {
        if (items.isNotEmpty()) {
            selectedIndex = if (selectedIndex > 0) selectedIndex - 1 else items.size - 1
        }
    }

    fun getSelectedItem(): CompletionItem? {
        return items.getOrNull(selectedIndex)
    }

    /**
     * Mark that a completion was just inserted
     * This prevents immediate re-triggering
     */
    fun markCompletionInserted() {
        lastCompletionMark = timeSource.markNow()
        hide()
    }

    /**
     * Calculate line numbers width based on total lines and visibility
     */
    fun calculateLineNumbersWidth(
        showLineNumbers: Boolean,
        totalLines: Int
    ): Int {
        return if (showLineNumbers) {
            val digits = totalLines.toString().length
            (digits * 10 + 16) // Approximate width: 10px per digit + padding
        } else {
            0
        }
    }
}