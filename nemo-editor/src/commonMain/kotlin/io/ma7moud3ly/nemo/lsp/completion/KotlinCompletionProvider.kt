package io.ma7moud3ly.nemo.lsp.completion

import io.ma7moud3ly.nemo.model.CompletionItem

/**
 * Completion provider for Kotlin Multiplatform, including Jetpack Compose and
 * Compose Multiplatform.
 *
 * Besides Kotlin keywords, types and the names declared in the file, it
 * suggests Compose annotations after `@`, composables, state and effect
 * functions, members of lambda scopes such as `items` and `drawCircle`, and
 * members after a known receiver: `Modifier.` and a modifier chain, `Color.`,
 * `Alignment.`, `MaterialTheme.colorScheme.`, `Icons.Default.`, `16.` and others.
 */
class KotlinCompletionProvider : CompletionProvider() {
    override val keywords = listOf(
        "package", "import", "class", "interface", "fun", "val", "var",
        "if", "else", "when", "for", "while", "do", "return", "break",
        "continue", "object", "companion", "data", "sealed", "enum",
        "abstract", "open", "override", "final", "private", "protected",
        "public", "internal", "suspend", "inline", "operator", "infix",
        "try", "catch", "finally", "throw", "is", "as", "in", "by",
        "expect", "actual"
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

    private val annotations = listOf(
        "@Composable", "@Preview", "@PreviewParameter", "@Stable", "@Immutable",
        "@ReadOnlyComposable", "@NonRestartableComposable", "@OptIn", "@Suppress",
        "@Deprecated", "@JvmStatic", "@JvmOverloads", "@Throws"
    )

    /** Members suggested after a receiver that is not a known Compose one. */
    private val commonMembers = listOf(
        "toString", "hashCode", "equals", "let", "apply", "also", "run", "takeIf"
    )

    /** Members whose value has a receiver of its own, mapped to that receiver. */
    private val memberTypes: Map<String, String> by lazy {
        val members = ComposeBindings.members
        buildMap {
            members["Color"]?.keys?.forEach { put(it, "Color") }
            members["ColorScheme"]?.keys?.forEach { put(it, "Color") }
            members["Typography"]?.keys?.forEach { put(it, "TextStyle") }
            put("colorScheme", "ColorScheme")
            put("typography", "Typography")
            put("shapes", "Shapes")
            put("modifier", "Modifier")
        }
    }

    override suspend fun provideCompletions(text: String, position: Int): List<CompletionItem> {
        val cursor = position.coerceIn(0, text.length)
        val wordStart = text.lastIndexOfAny(
            SEPARATORS.toCharArray(),
            (cursor - 1).coerceAtLeast(0)
        ) + 1
        val rawPrefix = text.substring(wordStart.coerceAtMost(cursor), cursor)

        if (rawPrefix.startsWith('@')) {
            return annotations
                .filter { it.startsWith(rawPrefix, ignoreCase = true) }
                .map {
                    CompletionItem(
                        label = it,
                        kind = CompletionKind.CLASS,
                        detail = "annotation",
                        insertText = it
                    )
                }
        }

        if (rawPrefix.contains('.')) {
            val dot = wordStart + rawPrefix.lastIndexOf('.')
            return getMemberCompletions(text, dot, rawPrefix.substringAfterLast('.'))
        }

        if (rawPrefix.isEmpty()) return emptyList()
        val prefix = rawPrefix

        // Parameters of the call around the cursor come first
        val completions = getParameterCompletions(text, wordStart, prefix).toMutableList()

        // Keywords - just insert the keyword, not the snippet
        keywords.filter { it.startsWith(prefix, ignoreCase = true) }.forEach { keyword ->
            completions.add(
                CompletionItem(
                    label = keyword,
                    kind = CompletionKind.KEYWORD,
                    detail = "keyword",
                    insertText = keyword
                )
            )
        }

        // Composables
        ComposeBindings.composables.matching(prefix).forEach { (name, params) ->
            completions.add(
                CompletionItem(
                    label = name,
                    kind = CompletionKind.FUNCTION,
                    detail = "@Composable ${params.preview()}",
                    insertText = name
                )
            )
        }

        // Compose functions
        ComposeBindings.functions.matching(prefix).forEach { (name, params) ->
            completions.add(
                CompletionItem(
                    label = name,
                    kind = CompletionKind.FUNCTION,
                    detail = params.preview(),
                    insertText = name
                )
            )
        }

        // Types
        (types + ComposeBindings.types)
            .filter { it.startsWith(prefix, ignoreCase = true) }
            .forEach { type ->
                completions.add(
                    CompletionItem(
                        label = type,
                        kind = CompletionKind.CLASS,
                        detail = "type",
                        insertText = type
                    )
                )
            }

        // Values
        ComposeBindings.values.filter { it.startsWith(prefix, ignoreCase = true) }.forEach {
            completions.add(
                CompletionItem(
                    label = it,
                    kind = CompletionKind.VALUE,
                    detail = "value",
                    insertText = it
                )
            )
        }

        // Functions
        functions.filter { it.startsWith(prefix, ignoreCase = true) }.forEach { func ->
            completions.add(
                CompletionItem(
                    label = func,
                    kind = CompletionKind.FUNCTION,
                    detail = "function",
                    insertText = func
                )
            )
        }

        // Members of lambda scopes, such as `items` in `LazyColumn`
        ComposeBindings.scopeMembers.forEach { (scope, members) ->
            members.matching(prefix).forEach { (name, params) ->
                completions.add(
                    CompletionItem(
                        label = name,
                        kind = if (params.isEmpty()) CompletionKind.PROPERTY
                        else CompletionKind.METHOD,
                        detail = "in $scope",
                        insertText = name
                    )
                )
            }
        }

        // Context-aware completions
        completions.addAll(getContextAwareCompletions(text, cursor, prefix))

        return completions.distinctBy { it.label }.take(MAX_ITEMS)
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

        // Extract variables from code
        val variablePattern = Regex("(val|var)\\s+(\\w+)")
        variablePattern.findAll(text).forEach { match ->
            val varName = match.groupValues[2]
            if (varName.startsWith(prefix, ignoreCase = true) &&
                varName !in completions.map { it.label }
            ) {
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
            if (funcName.startsWith(prefix, ignoreCase = true) &&
                funcName !in completions.map { it.label }
            ) {
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

    /**
     * Parameters of the known call whose argument starts with the word at
     * [wordStart], such as `modifier` in `Column(mo`. Parameters already named
     * in the call are left out. Picking one inserts `name = `.
     */
    private fun getParameterCompletions(
        text: String,
        wordStart: Int,
        prefix: String
    ): List<CompletionItem> {
        val open = argumentListStart(text, wordStart) ?: return emptyList()

        val nameEnd = skipWhitespaceBack(text, open - 1)
        val name = identifierEndingAt(text, nameEnd)
        if (name.isEmpty()) return emptyList()

        // A member call such as `Modifier.padding(` takes its receiver's parameters
        val nameStart = nameEnd - name.length + 1
        val isMemberCall = nameStart > 0 && text[nameStart - 1] == '.'
        val params = if (isMemberCall) {
            receiverOf(text, nameStart - 1)?.let { ComposeBindings.members[it]?.get(name) }
        } else {
            ComposeBindings.composables[name] ?: ComposeBindings.functions[name]
        } ?: return emptyList()

        val named = namedArguments(text, open, wordStart)

        return params.split(", ")
            .filter { it.isNotEmpty() && it !in named && it.startsWith(prefix, ignoreCase = true) }
            .map {
                CompletionItem(
                    label = it,
                    kind = CompletionKind.PROPERTY,
                    detail = "parameter of $name",
                    insertText = "$it = "
                )
            }
    }

    /**
     * Members to suggest after the dot at [dot]: those of a known Compose
     * receiver, or else common Kotlin members.
     */
    private fun getMemberCompletions(text: String, dot: Int, prefix: String): List<CompletionItem> {
        val receiver = receiverOf(text, dot)
        val members = receiver?.let { ComposeBindings.members[it] }
            ?: return commonMembers
                .filter { it.startsWith(prefix, ignoreCase = true) }
                .map {
                    CompletionItem(
                        label = it,
                        kind = CompletionKind.METHOD,
                        detail = "method",
                        insertText = it
                    )
                }

        return members
            .filter { (name, _) -> name.startsWith(prefix, ignoreCase = true) }
            .map { (name, params) ->
                val isCall = params.isNotEmpty() || receiver == "Modifier"
                CompletionItem(
                    label = name,
                    kind = if (isCall) CompletionKind.METHOD else CompletionKind.PROPERTY,
                    detail = if (isCall) params.preview() else receiver,
                    insertText = name
                )
            }
            .take(MAX_ITEMS)
    }

    /**
     * Name of the receiver of the member being typed after the dot at [dot], or
     * null when there is none. A call such as `Modifier.padding(8.dp).`
     * resolves to the receiver its result has, when that is a known one.
     */
    private fun receiverOf(text: String, dot: Int): String? {
        val end = skipWhitespaceBack(text, dot - 1)
        if (end < 0) return null

        if (text[end] == ')') {
            val open = openingParenOf(text, end) ?: return null
            val name = identifierEndingAt(text, open - 1)
            return when {
                ComposeBindings.members["Modifier"]?.containsKey(name) == true -> "Modifier"
                ComposeBindings.members.containsKey(name) -> name
                else -> null
            }
        }

        val name = identifierEndingAt(text, end)
        return when {
            name.isEmpty() -> null
            name.all { it.isDigit() } -> "Number"
            else -> memberTypes[name] ?: name
        }
    }

    private fun Map<String, String>.matching(prefix: String) =
        filterKeys { it.startsWith(prefix, ignoreCase = true) }

    /** The first parameters, such as `(modifier, content)`. */
    private fun String.preview(): String {
        if (isEmpty()) return "()"
        val params = split(", ")
        return if (params.size <= 3) "($this)"
        else "(${params.take(3).joinToString(", ")}, …)"
    }

    private companion object {
        /** Ends a word. `.` and `@` are kept, so members and annotations are seen. */
        const val SEPARATORS = " \n\t(){}[],;:\"'<>="
        const val MAX_ITEMS = 30
    }
}
