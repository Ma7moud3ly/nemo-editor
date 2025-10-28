package io.ma7moud3ly.nemo.syntax.formatter

class PythonFormatter : CodeFormatter() {

    override fun format(code: String, tabSize: Int, useTabs: Boolean): String {
        var result = code

        // Step 1: Format imports
        result = formatImports(result)

        // Step 2: Format code structure with proper indentation
        result = formatCode(result, tabSize, useTabs)

        // Step 3: Remove trailing whitespace
        result = removeTrailingWhitespace(result)

        // Step 4: Ensure newline at end
        result = ensureNewlineAtEnd(result)

        return result
    }

    override fun formatCode(code: String, tabSize: Int, useTabs: Boolean): String {
        val lines = code.lines()
        val formatted = mutableListOf<String>()
        var currentIndentLevel = 0
        val indent = if (useTabs) "\t" else " ".repeat(tabSize)
        var inMultiLineString = false
        var multiLineStringDelimiter = ""
        var previousIndentLevel = 0
        var previousNonCommentIndentLevel = 0

        for (i in lines.indices) {
            val line = lines[i]
            val trimmedLine = line.trim()

            // Empty lines
            if (trimmedLine.isEmpty()) {
                formatted.add("")
                continue
            }

            // Calculate the original indentation level first
            val originalIndentChars = line.takeWhile { it.isWhitespace() }
            val originalIndentLevel = if (useTabs) {
                originalIndentChars.count { it == '\t' }
            } else {
                originalIndentChars.length / tabSize
            }

            // Multi-line string detection
            val tripleQuoteCount = countTripleQuotes(trimmedLine)
            if (tripleQuoteCount > 0) {
                val wasInMultiLineString = inMultiLineString

                if (!inMultiLineString) {
                    if (tripleQuoteCount == 1 || (tripleQuoteCount == 2 && trimmedLine.indexOf("\"\"\"") != trimmedLine.lastIndexOf(
                            "\"\"\""
                        ))
                    ) {
                        inMultiLineString = true
                        multiLineStringDelimiter =
                            if (trimmedLine.contains("\"\"\"")) "\"\"\"" else "'''"

                        // Reset indent if multi-line string starts at column 0
                        if (originalIndentLevel == 0) {
                            currentIndentLevel = 0
                        }
                    }
                } else {
                    if (trimmedLine.contains(multiLineStringDelimiter)) {
                        inMultiLineString = false
                    }
                }

                formatted.add(indent.repeat(maxOf(0, currentIndentLevel)) + trimmedLine)

                // Update tracking only if we started a new multi-line string
                if (!wasInMultiLineString) {
                    previousNonCommentIndentLevel = originalIndentLevel
                    previousIndentLevel = originalIndentLevel
                }
                continue
            }

            // Inside multi-line strings
            if (inMultiLineString) {
                formatted.add(line)
                continue
            }

            // Comments - check if they're at top level to reset indent
            if (trimmedLine.startsWith("#")) {
                // If comment is at column 0 (top level), reset indent
                if (originalIndentLevel == 0) {
                    currentIndentLevel = 0
                }
                formatted.add(indent.repeat(maxOf(0, currentIndentLevel)) + trimmedLine)
                previousIndentLevel = originalIndentLevel
                // Don't update previousNonCommentIndentLevel for comments
                continue
            }

            // Detect dedent (going back to outer scope) - compare with last non-comment line
            if (originalIndentLevel < previousNonCommentIndentLevel) {
                val dedentLevels = previousNonCommentIndentLevel - originalIndentLevel
                currentIndentLevel = maxOf(0, currentIndentLevel - dedentLevels)
            }

            // Check if this is a dedent keyword (else, elif, except, finally)
            val isDedentKeyword = trimmedLine.startsWith("else:") ||
                    trimmedLine.startsWith("elif ") ||
                    trimmedLine.startsWith("except:") ||
                    trimmedLine.startsWith("except ") ||
                    trimmedLine.startsWith("finally:")

            // Dedent for else, elif, except, finally
            if (isDedentKeyword) {
                currentIndentLevel = maxOf(0, currentIndentLevel - 1)
            }

            // Format the line content
            val formattedLine = formatLine(trimmedLine)

            // Add proper indentation
            formatted.add(indent.repeat(currentIndentLevel) + formattedLine)

            // Update previous indent levels
            previousNonCommentIndentLevel = originalIndentLevel
            previousIndentLevel = originalIndentLevel

            // If line ends with colon, increase indent for next line
            if (formattedLine.trimEnd().endsWith(":")) {
                currentIndentLevel++
            }
        }

        return formatted.joinToString("\n")
    }

    private fun countTripleQuotes(line: String): Int {
        var count = 0
        var i = 0
        while (i < line.length - 2) {
            if ((line[i] == '"' && line[i + 1] == '"' && line[i + 2] == '"') ||
                (line[i] == '\'' && line[i + 1] == '\'' && line[i + 2] == '\'')
            ) {
                count++
                i += 3
            } else {
                i++
            }
        }
        return count
    }

    override fun formatImports(code: String): String {
        val lines = code.lines().toMutableList()
        val imports = mutableListOf<String>()
        val nonImports = mutableListOf<String>()

        for (line in lines) {
            if (line.trim().startsWith("import ") || line.trim().startsWith("from ")) {
                imports.add(line.trim())
            } else {
                nonImports.add(line)
            }
        }

        // Sort imports: 'from' imports first, then 'import' statements
        val fromImports = imports.filter { it.startsWith("from ") }.sorted()
        val regularImports = imports.filter { it.startsWith("import ") }.sorted()
        val sortedImports = fromImports + regularImports

        val result = mutableListOf<String>()

        if (sortedImports.isNotEmpty()) {
            result.addAll(sortedImports.distinct())
            result.add("")
        }

        // Add rest of code, but skip empty lines at the start
        var startAdding = false
        for (line in nonImports) {
            if (!startAdding && line.trim().isEmpty()) {
                continue
            }
            startAdding = true
            result.add(line)
        }

        return result.joinToString("\n")
    }

    override fun removeTrailingWhitespace(code: String): String {
        return code.lines().joinToString("\n") { it.trimEnd() }
    }

    override fun ensureNewlineAtEnd(code: String): String {
        return if (code.endsWith("\n")) code else "$code\n"
    }

    override fun formatLine(line: String): String {
        // Don't format if line contains a comment
        if (line.contains("#")) {
            val commentIndex = line.indexOf("#")
            val codePart = line.take(commentIndex)
            val commentPart = line.substring(commentIndex)

            var formattedCode = codePart
            formattedCode = addSpaceAfterKeywords(formattedCode)
            formattedCode = addSpacesAroundOperators(formattedCode)
            formattedCode = addSpaceAfterCommas(formattedCode)
            formattedCode = removeExtraSpaces(formattedCode)
            formattedCode = fixFunctionCalls(formattedCode)

            return formattedCode.trimEnd() + " " + commentPart
        }

        var result = line

        result = addSpaceAfterKeywords(result)
        result = addSpacesAroundOperators(result)
        result = addSpaceAfterCommas(result)
        result = removeExtraSpaces(result)
        result = fixFunctionCalls(result)

        return result
    }

    override fun addSpaceAfterKeywords(line: String): String {
        val keywords = listOf(
            "if", "elif", "else", "for", "while", "def", "class",
            "return", "try", "except", "finally", "raise", "with",
            "import", "from", "as", "in", "not", "and", "or"
        )

        var result = line
        for (keyword in keywords) {
            result = result.replace(Regex("\\b$keyword([a-zA-Z0-9_(])")) { match ->
                "$keyword ${match.groupValues[1]}"
            }
        }
        return result
    }

    override fun addSpacesAroundOperators(line: String): String {
        var result = line

        // Assignment operators
        val assignmentOps = listOf("=", "+=", "-=", "*=", "/=", "%=", "//=", "**=")
        for (op in assignmentOps) {
            result = result.replace(Regex("\\s*${Regex.escape(op)}\\s*")) { " $op " }
        }

        // Comparison operators
        val comparisonOps = listOf("==", "!=", "<=", ">=", "<", ">")
        for (op in comparisonOps) {
            result = result.replace(Regex("\\s*${Regex.escape(op)}\\s*")) { " $op " }
        }

        // Arithmetic operators
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*\\+\\s*")) { "${it.groupValues[1]} + " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*-\\s*")) { "${it.groupValues[1]} - " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*\\*\\s*")) { "${it.groupValues[1]} * " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*/\\s*")) { "${it.groupValues[1]} / " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*%\\s*")) { "${it.groupValues[1]} % " }

        // Logical operators (already words, just ensure spacing)
        result = result.replace(Regex("\\s+and\\s+"), " and ")
        result = result.replace(Regex("\\s+or\\s+"), " or ")
        result = result.replace(Regex("\\s+not\\s+"), " not ")

        return result
    }

    override fun addSpaceAfterCommas(line: String): String {
        return line.replace(Regex(",(?!\\s)"), ", ")
    }

    override fun removeExtraSpaces(line: String): String {
        var result = line
        // Remove spaces before semicolons, commas, and closing brackets
        result = result.replace(Regex("\\s+([;,)])"), "$1")
        // Remove spaces after opening brackets
        result = result.replace(Regex("([\\[\\(])\\s+"), "$1")
        // Replace multiple spaces with single space
        result = result.replace(Regex("([^ ]) {2,}"), "$1 ")
        return result
    }

    override fun fixFunctionCalls(line: String): String {
        var result = line
        result = result.replace(Regex("([a-zA-Z0-9_])\\s+\\("), "$1(")
        return result
    }
}