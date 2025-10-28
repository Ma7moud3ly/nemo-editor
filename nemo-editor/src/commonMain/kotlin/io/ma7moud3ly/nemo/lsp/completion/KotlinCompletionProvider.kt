package io.ma7moud3ly.nemo.lsp.completion

import io.ma7moud3ly.nemo.model.CompletionItem

class KotlinCompletionProvider : CompletionProvider() {
    override val keywords = listOf(
        "package", "import", "class", "interface", "fun", "val", "var",
        "if", "else", "when", "for", "while", "do", "return", "break",
        "continue", "object", "companion", "data", "sealed", "enum",
        "abstract", "open", "override", "final", "private", "protected",
        "public", "internal", "suspend", "inline", "operator", "infix",
        "try", "catch", "finally", "throw", "is", "as", "in", "by"
    )

    override val types = listOf(
        "Int", "Long", "Float", "Double", "Boolean", "Char", "String",
        "Unit", "Nothing", "Any", "List", "MutableList", "Set", "MutableSet",
        "Map", "MutableMap", "Array", "IntArray", "Pair", "Triple"
    )

    override val functions = listOf(
        "println", "print", "readLine", "require", "check", "error",
        "TODO", "run", "let", "apply", "also", "with", "lazy", "listOf",
        "mutableListOf", "setOf", "mutableSetOf", "mapOf", "mutableMapOf",
        "arrayOf", "emptyList", "emptySet", "emptyMap"
    )

    override val snippets = mapOf(
        "fun" to "fun \${1:name}(\${2:params}): \${3:ReturnType} {\n    \${0}\n}",
        "class" to "class \${1:Name}(\${2:params}) {\n    \${0}\n}",
        "if" to "if (\${1:condition}) {\n    \${0}\n}",
        "for" to "for (\${1:item} in \${2:items}) {\n    \${0}\n}",
        "when" to "when (\${1:value}) {\n    \${2:case} -> \${0}\n}",
        "try" to "try {\n    \${1}\n} catch (e: Exception) {\n    \${0}\n}"
    )

    override suspend fun provideCompletions(text: String, position: Int): List<CompletionItem> {
        val separators = " \n\t(){}[].,;:\"'<>="
        val wordStart =
            text.lastIndexOfAny(separators.toCharArray(), (position - 1).coerceAtLeast(0)) + 1
        val prefix = text.substring(wordStart, position).lowercase()

        if (prefix.isEmpty()) return emptyList()

        val completions = mutableListOf<CompletionItem>()

        // Keywords - just insert the keyword, not the snippet
        keywords.filter { it.startsWith(prefix) }.forEach { keyword ->
            completions.add(
                CompletionItem(
                    label = keyword,
                    kind = CompletionKind.KEYWORD,
                    detail = "keyword",
                    insertText = keyword // Just the keyword
                )
            )
        }

        // Types
        types.filter { it.lowercase().startsWith(prefix) }.forEach { type ->
            completions.add(
                CompletionItem(
                    label = type,
                    kind = CompletionKind.CLASS,
                    detail = "type",
                    insertText = type
                )
            )
        }

        // Functions
        functions.filter { it.lowercase().startsWith(prefix) }.forEach { func ->
            completions.add(
                CompletionItem(
                    label = func,
                    kind = CompletionKind.FUNCTION,
                    detail = "function",
                    insertText = func
                )
            )
        }

        // Context-aware completions
        completions.addAll(getContextAwareCompletions(text, position, prefix))

        return completions.take(20) // Limit results
    }

    override fun getContextAwareCompletions(
        text: String,
        position: Int,
        prefix: String
    ): List<CompletionItem> {
        val completions = mutableListOf<CompletionItem>()
        val beforeCursor = text.take(position)

        // After "fun " suggest function name pattern
        if (beforeCursor.endsWith("fun ")) {
            completions.add(
                CompletionItem(
                    label = "main",
                    kind = CompletionKind.FUNCTION,
                    detail = "main function",
                    insertText = "main(args: Array<String>) {\n    \n}"
                )
            )
        }

        // After dot, suggest common methods
        if (beforeCursor.endsWith(".")) {
            listOf(
                "toString",
                "hashCode",
                "equals",
                "let",
                "apply",
                "also",
                "run",
                "with"
            ).forEach {
                completions.add(
                    CompletionItem(
                        label = it,
                        kind = CompletionKind.METHOD,
                        detail = "method",
                        insertText = it
                    )
                )
            }
        }

        // Extract variables from code
        val variablePattern = Regex("(val|var)\\s+(\\w+)")
        variablePattern.findAll(text).forEach { match ->
            val varName = match.groupValues[2]
            if (varName.startsWith(prefix) && varName !in completions.map { it.label }) {
                completions.add(
                    CompletionItem(
                        label = varName,
                        kind = CompletionKind.VARIABLE,
                        detail = "variable",
                        insertText = varName
                    )
                )
            }
        }

        // Extract function names
        val functionPattern = Regex("fun\\s+(\\w+)")
        functionPattern.findAll(text).forEach { match ->
            val funcName = match.groupValues[1]
            if (funcName.startsWith(prefix) && funcName !in completions.map { it.label }) {
                completions.add(
                    CompletionItem(
                        label = funcName,
                        kind = CompletionKind.FUNCTION,
                        detail = "function",
                        insertText = funcName
                    )
                )
            }
        }

        return completions
    }
}
