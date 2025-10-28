package io.ma7moud3ly.nemo.syntax.analysis

import io.ma7moud3ly.nemo.model.SyntaxError
import io.ma7moud3ly.nemo.model.Token

abstract class ErrorDetector {
    abstract fun detectErrors(tokens: List<Token>): List<SyntaxError>
    protected abstract fun findDuplicateKeywords(tokens: List<Token>): List<SyntaxError>
    protected abstract fun findUnclosedStrings(tokens: List<Token>): List<SyntaxError>
    protected abstract fun findBracketErrors(tokens: List<Token>): List<SyntaxError>
    protected abstract fun getNextNonWhitespace(tokens: List<Token>, currentIndex: Int): Token?
}

