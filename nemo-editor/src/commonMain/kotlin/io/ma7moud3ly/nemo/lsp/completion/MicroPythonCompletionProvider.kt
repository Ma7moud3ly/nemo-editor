package io.ma7moud3ly.nemo.lsp.completion

import io.ma7moud3ly.nemo.model.CompletionItem

/**
 * Completion provider for MicroPython.
 *
 * Extends the plain Python vocabulary with the hardware oriented modules,
 * classes and constants that MicroPython firmwares expose (machine, network,
 * time, esp32, neopixel ...). It also suggests:
 * - members after a known module, a known class, a variable assigned from one,
 *   or a call that returns one, such as `Pin(2).` or `requests.get(url).`
 * - the parameters of a known call, such as `mode=` in `Pin(2, mo`
 * - snippets, such as `wifi` or `i2c`, that insert ready-made code
 */
class MicroPythonCompletionProvider : CompletionProvider() {

    override val keywords = listOf(
        "if", "elif", "else", "for", "while", "def", "class",
        "return", "try", "except", "finally", "raise", "with",
        "import", "from", "as", "in", "not", "and", "or", "is",
        "break", "continue", "pass", "yield", "lambda", "assert",
        "global", "nonlocal", "del", "async", "await",
        "True", "False", "None"
    )

    override val functions = listOf(
        // Python built-ins available on MicroPython
        "print", "input", "len", "range", "type", "int", "float", "str",
        "bool", "bytes", "bytearray", "memoryview", "list", "dict", "set",
        "tuple", "abs", "all", "any", "bin", "chr", "ord", "hex", "oct",
        "enumerate", "filter", "map", "max", "min", "sum", "sorted",
        "reversed", "zip", "open", "dir", "help", "id", "isinstance",
        "issubclass", "hasattr", "getattr", "setattr", "callable", "round",
        "pow", "repr", "iter", "next", "super", "globals", "locals",
        // MicroPython specific helpers, usually imported into the file scope
        "const", "sleep", "sleep_ms", "sleep_us", "ticks_ms", "ticks_us",
        "ticks_diff", "ticks_add", "unique_id", "freq", "reset", "soft_reset",
        "deepsleep", "lightsleep", "disable_irq", "enable_irq", "collect"
    )

    override val types = listOf(
        // Python types / exceptions
        "int", "float", "str", "bool", "bytes", "bytearray", "list", "dict",
        "set", "tuple", "object", "Exception", "ValueError", "TypeError",
        "KeyError", "IndexError", "AttributeError", "OSError", "MemoryError",
        "RuntimeError", "NotImplementedError", "StopIteration",
        "KeyboardInterrupt",
        // machine peripherals
        "Pin", "Signal", "ADC", "ADCBlock", "PWM", "DAC", "UART", "SPI",
        "SoftSPI", "I2C", "SoftI2C", "I2S", "RTC", "Timer", "WDT", "SDCard",
        // other commonly used classes
        "NeoPixel", "WLAN", "BLE", "OneWire", "DS18X20", "DHT11", "DHT22",
        "FrameBuffer", "StateMachine", "Partition", "NVS", "MQTTClient",
        "ESPNow", "SSD1306_I2C", "SSD1306_SPI", "Event", "Lock"
    )

    override val snippets = MicroPythonBindings.snippets

    private val modules = MicroPythonBindings.modules
    private val moduleMembers = MicroPythonBindings.moduleMembers
    private val classMembers = MicroPythonBindings.classMembers

    override suspend fun provideCompletions(text: String, position: Int): List<CompletionItem> {
        val cursor = position.coerceIn(0, text.length)
        val separators = " \n\t(){}[],;:\"'="
        val wordStart =
            text.lastIndexOfAny(separators.toCharArray(), (cursor - 1).coerceAtLeast(0)) + 1
        val rawPrefix = text.substring(wordStart.coerceAtMost(cursor), cursor)

        // Member access: complete against the receiver instead of the global scope.
        if (rawPrefix.contains('.')) {
            return getMemberCompletions(text, wordStart, rawPrefix).take(MAX_ITEMS)
        }

        if (rawPrefix.isEmpty()) return emptyList()
        val prefix = rawPrefix.lowercase()

        // On an `import x` / `from x` line only module names make sense.
        val currentLine = text.take(cursor).substringAfterLast('\n')
        if (IMPORT_LINE_REGEX.containsMatchIn(currentLine)) {
            return getContextAwareCompletions(text, cursor, rawPrefix).take(MAX_ITEMS)
        }

        // Parameters of the call around the cursor come first
        val completions = getParameterCompletions(text, wordStart, rawPrefix).toMutableList()

        // Keywords - just insert the keyword, not the snippet
        keywords.filter { it.lowercase().startsWith(prefix) }.forEach { keyword ->
            completions.add(
                CompletionItem(
                    label = keyword,
                    kind = CompletionKind.KEYWORD,
                    detail = "keyword",
                    insertText = keyword
                )
            )
        }

        // Snippets, indented to the current line
        val indent = indentAt(text, wordStart)
        snippets.filterKeys { it.startsWith(prefix) }.forEach { (trigger, body) ->
            completions.add(
                CompletionItem(
                    label = trigger,
                    kind = CompletionKind.SNIPPET,
                    detail = "snippet",
                    insertText = expandSnippet(body, indent)
                )
            )
        }

        // MicroPython modules
        modules.filter { it.startsWith(prefix) }.forEach { module ->
            completions.add(
                CompletionItem(
                    label = module,
                    kind = CompletionKind.MODULE,
                    detail = "module",
                    insertText = module
                )
            )
        }

        // Types and peripheral classes
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

        // Context-aware completions
        completions.addAll(getContextAwareCompletions(text, cursor, rawPrefix))

        // A snippet may share its label with a keyword, such as `if`
        return completions.distinctBy { it.label to it.kind }.take(MAX_ITEMS)
    }

    override fun getContextAwareCompletions(
        text: String,
        position: Int,
        prefix: String
    ): List<CompletionItem> {
        val completions = mutableListOf<CompletionItem>()
        val beforeCursor = text.take(position)
        val currentLine = beforeCursor.substringAfterLast('\n')

        // On an `import x` / `from x` line only module names make sense.
        if (IMPORT_LINE_REGEX.containsMatchIn(currentLine)) {
            modules.filter { it.startsWith(prefix) }.forEach { module ->
                completions.add(
                    CompletionItem(
                        label = module,
                        kind = CompletionKind.MODULE,
                        detail = "module",
                        insertText = module
                    )
                )
            }
            return completions
        }

        // After "def " suggest the constructor
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

        // Names pulled in by `from machine import Pin, PWM`
        FROM_IMPORT_REGEX.findAll(text).forEach { match ->
            match.groupValues[2].split(',').forEach { part ->
                val name = part.trim().substringAfterLast(" as ").trim()
                if (name.isNotEmpty() && name != "*" && name.startsWith(prefix)) {
                    completions.add(
                        CompletionItem(
                            label = name,
                            kind = CompletionKind.CLASS,
                            detail = "from ${match.groupValues[1]}",
                            insertText = name
                        )
                    )
                }
            }
        }

        // Local variables
        Regex("^\\s*(\\w+)\\s*=[^=]", RegexOption.MULTILINE).findAll(text).forEach { match ->
            val varName = match.groupValues[1]
            if (varName.startsWith(prefix) && varName !in keywords) {
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

        // Local functions
        Regex("def\\s+(\\w+)").findAll(text).forEach { match ->
            val funcName = match.groupValues[1]
            if (funcName.startsWith(prefix)) {
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

        // Local classes
        Regex("class\\s+(\\w+)").findAll(text).forEach { match ->
            val className = match.groupValues[1]
            if (className.startsWith(prefix)) {
                completions.add(
                    CompletionItem(
                        label = className,
                        kind = CompletionKind.CLASS,
                        detail = "class",
                        insertText = className
                    )
                )
            }
        }

        return completions.distinctBy { it.label }
    }

    /**
     * Completions for `receiver.member`, where the receiver is a known module,
     * a known class, a variable assigned from one, or a call that returns one.
     */
    private fun getMemberCompletions(
        text: String,
        wordStart: Int,
        rawPrefix: String
    ): List<CompletionItem> {
        val receiver = rawPrefix.substringBeforeLast('.').substringAfterLast('.')
        val memberPrefix = rawPrefix.substringAfterLast('.')
        val module = normalizeModule(receiver)

        val (members, detail) = when {
            // `Pin(2).` - the dot follows a call
            receiver.isEmpty() -> {
                val dot = wordStart + rawPrefix.lastIndexOf('.')
                val type = typeOfCallBefore(text, dot)
                type?.let { classMembers[it] }?.let { it to type }
            }

            moduleMembers.containsKey(module) -> moduleMembers.getValue(module) to "$receiver module"
            classMembers.containsKey(receiver) -> classMembers.getValue(receiver) to receiver
            else -> {
                val type = inferVariableType(text, receiver)
                type?.let { classMembers[it] }?.let { it to type }
            }
        } ?: return emptyList()

        return members
            .filter { it.startsWith(memberPrefix) }
            .map { member ->
                CompletionItem(
                    label = member,
                    kind = when {
                        member.first().isUpperCase() && member.any { c -> c.isLowerCase() } ->
                            CompletionKind.CLASS

                        member.all { c -> !c.isLowerCase() } -> CompletionKind.VALUE
                        else -> CompletionKind.METHOD
                    },
                    detail = detail,
                    insertText = member
                )
            }
    }

    /**
     * Parameters of the known call whose argument starts with the word at
     * [wordStart], such as `mode` in `Pin(2, mo`. Parameters already named in
     * the call are left out. Picking one inserts `name=`.
     */
    private fun getParameterCompletions(
        text: String,
        wordStart: Int,
        prefix: String
    ): List<CompletionItem> {
        val open = argumentListStart(text, wordStart) ?: return emptyList()
        val (qualifier, name) = callBefore(text, open) ?: return emptyList()
        val key = parameterKey(text, qualifier, name) ?: return emptyList()
        val named = namedArguments(text, open, wordStart)

        return MicroPythonBindings.parameters.getValue(key)
            .filter { it !in named && it.startsWith(prefix, ignoreCase = true) }
            .map {
                CompletionItem(
                    label = it,
                    kind = CompletionKind.PROPERTY,
                    detail = "parameter of $key",
                    insertText = "$it="
                )
            }
    }

    /** The key in [MicroPythonBindings.parameters] for a call to [qualifier].[name]. */
    private fun parameterKey(text: String, qualifier: String?, name: String): String? {
        val parameters = MicroPythonBindings.parameters
        val candidates = buildList {
            if (qualifier == null) {
                add(name)
                // A function imported with `from time import sleep_ms`
                importedFrom(text, name)?.let { add("${normalizeModule(it)}.$name") }
            } else {
                add("${normalizeModule(qualifier)}.$name")
                add("$qualifier.$name")
                inferVariableType(text, qualifier)?.let { add("$it.$name") }
                // A class reached through its module, such as `machine.Pin(`
                if (name.first().isUpperCase()) add(name)
            }
        }
        return candidates.firstOrNull { it in parameters }
    }

    /** Resolves `led = Pin(2, Pin.OUT)` or `r = requests.get(url)` back to its class. */
    private fun inferVariableType(text: String, variable: String): String? {
        val match = Regex("\\b${Regex.escape(variable)}\\s*=\\s*(?:([\\w.]+)\\.)?(\\w+)\\s*\\(")
            .findAll(text)
            .lastOrNull() ?: return null
        val qualifier = match.groupValues[1].ifEmpty { null }
        return typeOfCall(text, qualifier, match.groupValues[2])
    }

    /** The class returned by the call that ends just before the dot at [dot]. */
    private fun typeOfCallBefore(text: String, dot: Int): String? {
        val close = skipWhitespaceBack(text, dot - 1)
        if (close < 0 || text[close] != ')') return null
        val open = openingParenOf(text, close) ?: return null
        val (qualifier, name) = callBefore(text, open) ?: return null
        return typeOfCall(text, qualifier, name)
    }

    /** The class a call to [qualifier].[name] returns, when it is a known one. */
    private fun typeOfCall(text: String, qualifier: String?, name: String): String? {
        if (classMembers.containsKey(name)) return name
        val module = qualifier?.substringAfterLast('.') ?: importedFrom(text, name) ?: return null
        return MicroPythonBindings.returnTypes["${normalizeModule(module)}.$name"]
    }

    /**
     * The qualifier and name of the function called with the `(` at [open],
     * such as `machine` and `Pin` for `machine.Pin(`.
     */
    private fun callBefore(text: String, open: Int): Pair<String?, String>? {
        val nameEnd = skipWhitespaceBack(text, open - 1)
        val name = identifierEndingAt(text, nameEnd)
        if (name.isEmpty()) return null
        val nameStart = nameEnd - name.length + 1
        val qualifier = if (nameStart > 0 && text[nameStart - 1] == '.') {
            identifierEndingAt(text, nameStart - 2).ifEmpty { null }
        } else null
        return qualifier to name
    }

    /** The module [name] is imported from with `from module import name`, if any. */
    private fun importedFrom(text: String, name: String): String? =
        FROM_IMPORT_REGEX.findAll(text).firstOrNull { match ->
            match.groupValues[2].split(',').any { it.trim() == name }
        }?.groupValues?.get(1)?.substringAfterLast('.')

    /** `utime` is `time`, `urequests` is `requests`, and so on. */
    private fun normalizeModule(name: String): String =
        if (name !in moduleMembers && name.startsWith("u") && name.drop(1) in moduleMembers) {
            name.drop(1)
        } else name

    /** The indentation of the line that holds [index]. */
    private fun indentAt(text: String, index: Int): String {
        val lineStart = text.lastIndexOf('\n', index - 1) + 1
        return text.substring(lineStart, index).takeWhile { it == ' ' || it == '\t' }
    }

    /** [body] with each field replaced by its default text, and each line indented. */
    private fun expandSnippet(body: String, indent: String): String =
        SNIPPET_FIELD_REGEX.replace(body) { it.groupValues[1] }.replace("\n", "\n$indent")

    private companion object {
        const val MAX_ITEMS = 30

        val IMPORT_LINE_REGEX = Regex("^\\s*(import|from)\\s+[\\w.]*$")

        /** Matches `from module import a, b`, with the module and the names. */
        val FROM_IMPORT_REGEX = Regex("from\\s+([\\w.]+)\\s+import\\s+([^\\n]+)")

        /** Matches a snippet field, such as `${1:name}` or `${0}`. */
        val SNIPPET_FIELD_REGEX = Regex("""\$\{\d+(?::([^}]*))?\}""")
    }
}
