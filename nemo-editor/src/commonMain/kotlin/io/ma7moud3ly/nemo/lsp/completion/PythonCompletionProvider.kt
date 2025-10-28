package io.ma7moud3ly.nemo.lsp.completion

import io.ma7moud3ly.nemo.model.CompletionItem

class PythonCompletionProvider : CompletionProvider() {
    override val keywords = listOf(
        "if", "elif", "else", "for", "while", "def", "class",
        "return", "try", "except", "finally", "raise", "with",
        "import", "from", "as", "in", "not", "and", "or", "is",
        "break", "continue", "pass", "yield", "lambda", "assert",
        "global", "nonlocal", "async", "await", "True", "False", "None"
    )

    override val functions = listOf(
        "print", "input", "len", "range", "type", "int", "float", "str",
        "bool", "list", "dict", "set", "tuple", "abs", "all", "any",
        "enumerate", "filter", "map", "max", "min", "sum", "sorted",
        "reversed", "zip", "open", "help", "dir", "isinstance", "hasattr"
    )

    override val types = listOf(
        "int", "float", "str", "bool", "list", "dict", "set", "tuple",
        "object", "Exception", "ValueError", "TypeError", "KeyError"
    )

    override val snippets = mapOf(
        "def" to "def \${1:name}(\${2:params}):\n    \${0}",
        "class" to "class \${1:Name}:\n    def __init__(self\${2:, params}):\n        \${0}",
        "if" to "if \${1:condition}:\n    \${0}",
        "for" to "for \${1:item} in \${2:items}:\n    \${0}",
        "while" to "while \${1:condition}:\n    \${0}",
        "try" to "try:\n    \${1}\nexcept Exception as e:\n    \${0}"
    )

    override suspend fun provideCompletions(text: String, position: Int): List<CompletionItem> {
        val separators = " \n\t(){}[].,;:\"'="
        val wordStart =
            text.lastIndexOfAny(separators.toCharArray(), (position - 1).coerceAtLeast(0)) + 1
        val prefix = text.substring(wordStart, position).lowercase()

        if (prefix.isEmpty()) return emptyList()

        val completions = mutableListOf<CompletionItem>()

        // Keywords - just insert the keyword, not the snippet
        keywords.filter { it.lowercase().startsWith(prefix) }.forEach { keyword ->
            completions.add(
                CompletionItem(
                    label = keyword,
                    kind = CompletionKind.KEYWORD,
                    detail = "keyword",
                    insertText = keyword // Just the keyword
                )
            )
        }

        // Built-in functions
        functions.filter { it.startsWith(prefix) }.forEach { func ->
            completions.add(
                CompletionItem(
                    label = func,
                    kind = CompletionKind.FUNCTION,
                    detail = "built-in",
                    insertText = func
                )
            )
        }

        // Types
        types.filter { it.startsWith(prefix) }.forEach { type ->
            completions.add(
                CompletionItem(
                    label = type,
                    kind = CompletionKind.CLASS,
                    detail = "type",
                    insertText = type
                )
            )
        }

        // Context-aware completions
        completions.addAll(getContextAwareCompletions(text, position, prefix))

        return completions.take(20)
    }

    override fun getContextAwareCompletions(
        text: String,
        position: Int,
        prefix: String
    ): List<CompletionItem> {
        val completions = mutableListOf<CompletionItem>()
        val beforeCursor = text.take(position)

        // After "def " suggest common function patterns
        if (beforeCursor.endsWith("def ")) {
            completions.add(
                CompletionItem(
                    label = "__init__",
                    kind = CompletionKind.FUNCTION,
                    detail = "constructor",
                    insertText = "__init__(self):\n    "
                )
            )
        }

        // After dot, suggest common methods
        if (beforeCursor.endsWith(".")) {
            listOf(
                "append",
                "extend",
                "insert",
                "remove",
                "pop",
                "clear",
                "copy",
                "count",
                "index"
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

        // Extract variables
        val variablePattern = Regex("(\\w+)\\s*=")
        variablePattern.findAll(text).forEach { match ->
            val varName = match.groupValues[1]
            if (varName.startsWith(prefix) && varName !in keywords && varName !in completions.map { it.label }) {
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
        val functionPattern = Regex("def\\s+(\\w+)")
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
