package com.ma7moud3ly.nemo.lsp.completion

import com.ma7moud3ly.nemo.model.CompletionItem

/**
 * Defines the contract for providing code completion suggestions.
 * Implementations of this class are specific to a programming language
 * and are responsible for generating relevant completion items based on
 * the current code context and cursor position.
 */
abstract class CompletionProvider {

    /**
     * A list of strings representing the keywords of the language.
     * Example: "if", "for", "class", "fun", etc.
     */
    protected abstract val keywords: List<String>

    /**
     * A list of strings representing the built-in or common data types of the language.
     * Example: "String", "Int", "List", etc.
     */
    protected abstract val types: List<String>

    /**
     * A list of strings representing the built-in or common functions of the language.
     * Example: "println", "main", "range", etc.
     */
    protected abstract val functions: List<String>

    /**
     * A map of trigger words to their corresponding code snippets.
     * Snippets allow for more complex template-based completions.
     * The key is the trigger (e.g., "fun") and the value is the snippet text.
     */
    protected abstract val snippets: Map<String, String>

    /**
     * The primary method to generate a list of completion suggestions.
     *
     * @param text The entire source code text in the editor.
     * @param position The current zero-based index of the cursor in the text.
     * @return A list of [CompletionItem] objects to be displayed to the user.
     */
    abstract suspend fun provideCompletions(text: String, position: Int): List<CompletionItem>

    /**
     * Provides context-aware completion suggestions based on the code structure
     * surrounding the cursor. This can include suggesting local variables,
     * methods after a dot, or specific patterns.
     *
     * @param text The entire source code string.
     * @param position The current cursor position.
     * @param prefix The word or part of a word immediately before the cursor, used for filtering.
     * @return A list of context-specific [CompletionItem]s.
     */
    protected abstract fun getContextAwareCompletions(
        text: String,
        position: Int,
        prefix: String
    ): List<CompletionItem>
}
