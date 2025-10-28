package io.ma7moud3ly.nemo.syntax.formatter

class KotlinFormatter : CodeFormatter() {

    override fun format(code: String, tabSize: Int, useTabs: Boolean): String {
        var result = code

        // Step 1: Format imports
        result = formatImports(result)

        // Step 2: Format code structure
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
        var indentLevel = 0
        val indent = if (useTabs) "\t" else " ".repeat(tabSize)
        var inMultiLineComment = false

        for (i in lines.indices) {
            var line = lines[i].trim()

            if (line.isEmpty()) {
                formatted.add("")
                continue
            }

            // Check for multi-line comment start/end
            if (line.contains("/*")) {
                inMultiLineComment = true
            }

            // Don't format lines inside multi-line comments
            if (inMultiLineComment) {
                formatted.add(indent.repeat(indentLevel) + line)
                if (line.contains("*/")) {
                    inMultiLineComment = false
                }
                continue
            }

            // Don't format single-line comments - keep them as is with proper indent
            if (line.startsWith("//")) {
                formatted.add(indent.repeat(indentLevel) + line)
                continue
            }

            // Decrease indent for closing braces
            if (line.startsWith("}") || line.startsWith(")") || line.startsWith("]")) {
                indentLevel = maxOf(0, indentLevel - 1)
            }

            // Format the line (but not comments)
            line = formatLine(line)

            // Add indentation
            val indentedLine = indent.repeat(indentLevel) + line
            formatted.add(indentedLine)

            // Increase indent after opening braces
            if (line.endsWith("{") || line.endsWith("(") || line.endsWith("[")) {
                indentLevel++
            }

            // Special case: if line has both { and }, don't change indent
            val openCount = line.count { it == '{' }
            val closeCount = line.count { it == '}' }
            if (openCount > 0 && closeCount > 0 && openCount == closeCount) {
                indentLevel -= closeCount
            }
        }

        return formatted.joinToString("\n")
    }

    override fun formatImports(code: String): String {
        val lines = code.lines().toMutableList()
        val imports = mutableListOf<String>()
        val nonImports = mutableListOf<String>()
        var hasPackage = false
        var packageLine = ""

        for (line in lines) {
            when {
                line.trim().startsWith("package ") -> {
                    hasPackage = true
                    packageLine = line.trim()
                }

                line.trim().startsWith("import ") -> {
                    imports.add(line.trim())
                }

                else -> {
                    nonImports.add(line)
                }
            }
        }

        imports.sort()
        val uniqueImports = imports.distinct()

        val result = mutableListOf<String>()

        // Add package line
        if (hasPackage) {
            result.add(packageLine)
            result.add("")
        }

        // Add imports
        if (uniqueImports.isNotEmpty()) {
            result.addAll(uniqueImports)
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
        if (line.contains("//")) {
            // Format code before comment, keep comment as is
            val commentIndex = line.indexOf("//")
            val codePart = line.substring(0, commentIndex)
            val commentPart = line.substring(commentIndex)

            var formattedCode = codePart
            formattedCode = addSpaceAfterKeywords(formattedCode)
            formattedCode = addSpacesAroundOperators(formattedCode)
            formattedCode = addSpaceAfterCommas(formattedCode)
            formattedCode = addSpaceBeforeOpeningBrace(formattedCode)
            formattedCode = removeExtraSpaces(formattedCode)
            formattedCode = fixFunctionCalls(formattedCode)

            return formattedCode.trimEnd() + " " + commentPart
        }

        var result = line

        result = addSpaceAfterKeywords(result)
        result = addSpacesAroundOperators(result)
        result = addSpaceAfterCommas(result)
        result = addSpaceBeforeOpeningBrace(result)
        result = removeExtraSpaces(result)
        result = fixFunctionCalls(result)

        return result
    }

    override fun addSpaceAfterKeywords(line: String): String {
        val keywords = listOf(
            "if", "else", "when", "for", "while", "do", "return",
            "fun", "val", "var", "class", "interface", "object",
            "try", "catch", "finally", "throw", "import", "package"
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
        val assignmentOps = listOf("=", "+=", "-=", "*=", "/=", "%=")
        for (op in assignmentOps) {
            result = result.replace(Regex("\\s*${Regex.escape(op)}\\s*")) { " $op " }
        }

        // Comparison operators (but not generics)
        // Handle == and != first
        result = result.replace(Regex("\\s*==\\s*"), " == ")
        result = result.replace(Regex("\\s*!=\\s*"), " != ")

        // Handle <= and >= (not generics)
        result = result.replace(Regex("([^<>])\\s*<=\\s*"), "$1 <= ")
        result = result.replace(Regex("([^<>])\\s*>=\\s*"), "$1 >= ")

        // Handle < and > only in comparison context (not generics)
        // Only add spaces if NOT part of generic type syntax
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*<\\s*([a-zA-Z0-9_])")) { match ->
            val before = match.groupValues[1]
            val after = match.groupValues[2]
            // Check if this looks like a generic (uppercase letter before <)
            if (before.last().isUpperCase() || after.first().isUpperCase()) {
                "$before<$after" // No spaces for generics
            } else {
                "$before < $after" // Spaces for comparison
            }
        }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*>\\s*([a-zA-Z0-9_])")) { match ->
            val before = match.groupValues[1]
            val after = match.groupValues[2]
            // Check if this looks like end of generic
            if (before.last().isUpperCase() || after.first().isUpperCase()) {
                "$before>$after" // No spaces for generics
            } else {
                "$before > $after" // Spaces for comparison
            }
        }

        // Remove spaces inside generics completely
        result = result.replace(Regex("<\\s+"), "<")
        result = result.replace(Regex("\\s+>"), ">")

        // Arithmetic operators (but not in negative numbers or function parameters)
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*\\+\\s*")) { "${it.groupValues[1]} + " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*-\\s*")) { "${it.groupValues[1]} - " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*\\*\\s*")) { "${it.groupValues[1]} * " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*/\\s*")) { "${it.groupValues[1]} / " }
        result = result.replace(Regex("([a-zA-Z0-9_)])\\s*%\\s*")) { "${it.groupValues[1]} % " }

        // Logical operators
        result = result.replace(Regex("\\s*&&\\s*"), " && ")
        result = result.replace(Regex("\\s*\\|\\|\\s*"), " || ")

        // Range operator - no spaces
        result = result.replace(Regex("\\s*\\.\\.\\s*"), "..")

        // Elvis operator
        result = result.replace(Regex("\\s*\\?:\\s*"), " ?: ")

        // Safe call - no spaces
        result = result.replace(Regex("\\s*\\?\\.\\s*"), "?.")
        result = result.replace(Regex("\\s*!!\\s*"), "!!")

        return result
    }

    override fun addSpaceAfterCommas(line: String): String {
        return line.replace(Regex(",(?!\\s)"), ", ")
    }

    // Only for Kotlin: add space before brace
    fun addSpaceBeforeOpeningBrace(line: String): String {
        return line.replace(Regex("(\\S)\\{"), "$1 {")
    }

    override fun removeExtraSpaces(line: String): String {
        var result = line
        // Remove spaces before semicolons, commas, and closing brackets
        result = result.replace(Regex("\\s+([;,)])"), "$1")
        // Remove spaces after opening brackets
        result = result.replace(Regex("([\\[\\(])\\s+"), "$1")
        // Replace multiple spaces with single space (but not at start of line)
        result = result.replace(Regex("([^ ]) {2,}"), "$1 ")
        return result
    }

    override fun fixFunctionCalls(line: String): String {
        var result = line
        result = result.replace(Regex("([a-zA-Z0-9_])\\s+\\("), "$1(")
        result = result.replace(Regex("\\)(\\{)"), ") $1")
        return result
    }
}