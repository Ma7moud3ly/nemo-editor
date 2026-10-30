package io.ma7moud3ly.nemo.syntax.formatter

abstract class CodeFormatter {
    abstract fun format(code: String, tabSize: Int = 4, useTabs: Boolean = false): String
    protected abstract fun formatCode(code: String, tabSize: Int, useTabs: Boolean): String
    protected abstract fun formatImports(code: String): String
    protected abstract fun removeTrailingWhitespace(code: String): String
    protected abstract fun ensureNewlineAtEnd(code: String): String
    protected abstract fun formatLine(line: String): String

    // Spacing steps a formatLine may use. Each returns the line unchanged by default.
    protected open fun addSpaceAfterKeywords(line: String): String = line
    protected open fun addSpacesAroundOperators(line: String): String = line
    protected open fun addSpaceAfterCommas(line: String): String = line
    protected open fun removeExtraSpaces(line: String): String = line
    protected open fun fixFunctionCalls(line: String): String = line
}
