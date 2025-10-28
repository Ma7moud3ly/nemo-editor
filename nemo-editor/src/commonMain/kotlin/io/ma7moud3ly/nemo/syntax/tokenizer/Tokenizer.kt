package io.ma7moud3ly.nemo.syntax.tokenizer

import io.ma7moud3ly.nemo.model.Token

/**
 * Defines the contract for a language tokenizer.
 * A tokenizer is responsible for breaking a string of source code
 * into a sequence of meaningful tokens.
 */
abstract class LanguageTokenizer {

    /**
     * A set of strings representing the keywords of the language.
     * Example: "if", "for", "class", "fun", etc.
     */
    protected abstract val keywords: Set<String>

    /**
     * A set of strings representing the built-in data types or common library types.
     * Example: "String", "Int", "ValueError", etc.
     */
    protected abstract val builtInTypes: Set<String>

    /**
     * The primary method to tokenize a given text.
     *
     * @param text The source code string to be tokenized.
     * @return A list of [Token] objects representing the code.
     */
    abstract fun tokenize(text: String): List<Token>

    /**
     * Finds the end of a string literal, handling escaped characters.
     *
     * @param text The source code string.
     * @param start The starting index of the string literal (at the opening quote).
     * @param quote The character used for the string quote (' or ").
     * @return A Pair containing the end index of the string and a boolean indicating if it's unterminated.
     */
    protected abstract fun findStringEnd(text: String, start: Int, quote: Char): Pair<Int, Boolean>

    /**
     * Finds the end of a number literal, handling integers, floats, hex, etc.
     *
     * @param text The source code string.
     * @param start The starting index of the number.
     * @return The index immediately after the last character of the number.
     */
    protected abstract fun findNumberEnd(text: String, start: Int): Int

    /**
     * Finds the end of a multi-character operator.
     *
     * @param text The source code string.
     * @param start The starting index of the potential operator.
     * @return The index immediately after the last character of the operator.
     */
    protected abstract fun findOperatorEnd(text: String, start: Int): Int

    /**
     * Finds the end of an identifier.
     *
     * @param text The source code string.
     * @param start The starting index of the potential identifier.
     * @return The index immediately after the last character of the identifier.
     */
    protected abstract fun findIdentifierEnd(text: String, start: Int): Int

    /**
     * Checks if an identifier at a given position is a function call.
     * Typically, this involves checking for a subsequent '('.
     *
     * @param text The source code string.
     * @param position The position in the text immediately after the identifier.
     * @return True if the identifier is followed by an opening parenthesis, indicating a function call.
     */
    protected abstract fun isFunctionCall(text: String, position: Int): Boolean

    /**
     * Checks if an identifier is part of a function definition.
     *
     * @param text The source code string.
     * @param position The starting position of the identifier.
     * @return True if the identifier is being defined as a function.
     */
    protected abstract fun isFunctionDef(text: String, position: Int): Boolean

    /**
     * Checks if an identifier is part of a class definition.
     *
     * @param text The source code string.
     * @param position The starting position of the identifier.
     * @return True if the identifier is being defined as a class.
     */
    protected abstract fun isClassDef(text: String, position: Int): Boolean
}