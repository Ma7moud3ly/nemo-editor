package io.ma7moud3ly.nemo.syntax.formatter

abstract class CodeFormatter {
    abstract fun format(code: String, tabSize: Int = 4, useTabs: Boolean = false): String
    protected abstract fun formatCode(code: String, tabSize: Int, useTabs: Boolean): String
    protected abstract fun formatImports(code: String): String
    protected abstract fun removeTrailingWhitespace(code: String): String
    protected abstract fun ensureNewlineAtEnd(code: String): String
    protected abstract fun formatLine(line: String): String
    protected abstract fun addSpaceAfterKeywords(line: String): String
    protected abstract fun addSpacesAroundOperators(line: String): String
    protected abstract fun addSpaceAfterCommas(line: String): String
    protected abstract fun removeExtraSpaces(line: String): String
    protected abstract fun fixFunctionCalls(line: String): String
}