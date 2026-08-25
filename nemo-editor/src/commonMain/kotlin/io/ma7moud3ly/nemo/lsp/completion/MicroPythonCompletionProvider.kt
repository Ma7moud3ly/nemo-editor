package io.ma7moud3ly.nemo.lsp.completion

import io.ma7moud3ly.nemo.model.CompletionItem

/**
 * Completion provider for MicroPython.
 *
 * Extends the plain Python vocabulary with the hardware oriented modules,
 * classes and constants that MicroPython firmwares expose (machine, network,
 * time, esp32, neopixel ...), plus member completion after a known module,
 * peripheral class, or a variable assigned from one.
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
        "Pin", "Signal", "ADC", "PWM", "DAC", "UART", "SPI", "SoftSPI",
        "I2C", "SoftI2C", "I2S", "RTC", "Timer", "WDT", "SDCard",
        // other commonly used classes
        "NeoPixel", "WLAN", "BLE", "OneWire", "DS18X20", "DHT11", "DHT22",
        "FrameBuffer", "StateMachine", "Partition"
    )

    /** Modules shipped with most MicroPython ports (both `u`-prefixed and plain). */
    private val modules = listOf(
        "machine", "micropython", "network", "time", "utime", "sys", "usys",
        "os", "uos", "gc", "json", "ujson", "math", "cmath", "random",
        "urandom", "struct", "ustruct", "binascii", "ubinascii", "hashlib",
        "uhashlib", "socket", "usocket", "ssl", "ussl", "select", "uselect",
        "collections", "ucollections", "io", "uio", "re", "ure", "errno",
        "uerrno", "heapq", "uheapq", "asyncio", "uasyncio", "array", "esp",
        "esp32", "rp2", "pyb", "bluetooth", "neopixel", "onewire", "ds18x20",
        "dht", "framebuf", "webrepl", "_thread", "urequests", "umqtt"
    )

    /** Members suggested right after `<module>.` */
    private val moduleMembers: Map<String, List<String>> = mapOf(
        "machine" to listOf(
            "Pin", "Signal", "ADC", "PWM", "DAC", "UART", "SPI", "SoftSPI",
            "I2C", "SoftI2C", "I2S", "RTC", "Timer", "WDT", "SDCard",
            "reset", "soft_reset", "reset_cause", "bootloader", "freq",
            "deepsleep", "lightsleep", "idle", "wake_reason", "unique_id",
            "disable_irq", "enable_irq", "time_pulse_us", "mem8", "mem16",
            "mem32", "PWRON_RESET", "HARD_RESET", "WDT_RESET", "DEEPSLEEP_RESET"
        ),
        "time" to listOf(
            "sleep", "sleep_ms", "sleep_us", "ticks_ms", "ticks_us", "ticks_cpu",
            "ticks_add", "ticks_diff", "time", "time_ns", "localtime", "gmtime",
            "mktime"
        ),
        "network" to listOf(
            "WLAN", "LAN", "STA_IF", "AP_IF", "hostname", "country", "phy_mode",
            "STAT_IDLE", "STAT_CONNECTING", "STAT_GOT_IP", "STAT_WRONG_PASSWORD",
            "STAT_NO_AP_FOUND", "STAT_CONNECT_FAIL"
        ),
        "os" to listOf(
            "listdir", "ilistdir", "mkdir", "rmdir", "remove", "rename", "stat",
            "statvfs", "chdir", "getcwd", "sync", "uname", "urandom", "mount",
            "umount", "VfsFat", "VfsLfs2", "dupterm"
        ),
        "sys" to listOf(
            "argv", "byteorder", "exit", "implementation", "maxsize", "modules",
            "path", "platform", "print_exception", "stdin", "stdout", "stderr",
            "version", "version_info"
        ),
        "gc" to listOf(
            "collect", "disable", "enable", "isenabled", "mem_alloc", "mem_free",
            "threshold"
        ),
        "micropython" to listOf(
            "const", "opt_level", "alloc_emergency_exception_buf", "mem_info",
            "qstr_info", "stack_use", "heap_lock", "heap_unlock", "kbd_intr",
            "schedule", "native", "viper", "asm_thumb"
        ),
        "json" to listOf("dump", "dumps", "load", "loads"),
        "math" to listOf(
            "pi", "e", "sqrt", "pow", "exp", "log", "sin", "cos", "tan", "asin",
            "acos", "atan", "atan2", "ceil", "floor", "trunc", "fabs", "fmod",
            "radians", "degrees", "isnan", "isinf", "modf", "copysign"
        ),
        "random" to listOf(
            "random", "randint", "randrange", "uniform", "choice", "getrandbits",
            "seed", "shuffle"
        ),
        "struct" to listOf("calcsize", "pack", "pack_into", "unpack", "unpack_from"),
        "binascii" to listOf("hexlify", "unhexlify", "a2b_base64", "b2a_base64", "crc32"),
        "esp" to listOf(
            "osdebug", "flash_size", "flash_read", "flash_write", "flash_erase",
            "flash_user_start", "sleep_type", "deepsleep"
        ),
        "esp32" to listOf(
            "Partition", "NVS", "RMT", "ULP", "wake_on_touch", "wake_on_ext0",
            "wake_on_ext1", "raw_temperature", "hall_sensor", "gpio_deep_sleep_hold",
            "HEAP_DATA", "HEAP_EXEC", "WAKEUP_ALL_LOW", "WAKEUP_ANY_HIGH"
        ),
        "rp2" to listOf(
            "PIO", "StateMachine", "asm_pio", "asm_pio_encode", "bootsel_button",
            "country", "Flash"
        ),
        "bluetooth" to listOf("BLE", "UUID", "FLAG_READ", "FLAG_WRITE", "FLAG_NOTIFY"),
        "neopixel" to listOf("NeoPixel"),
        "framebuf" to listOf(
            "FrameBuffer", "MONO_VLSB", "MONO_HLSB", "MONO_HMSB", "RGB565",
            "GS2_HMSB", "GS4_HMSB", "GS8"
        ),
        "asyncio" to listOf(
            "run", "sleep", "sleep_ms", "create_task", "gather", "wait_for",
            "wait_for_ms", "Event", "Lock", "Task", "get_event_loop",
            "new_event_loop", "run_until_complete", "current_task"
        ),
        "socket" to listOf(
            "socket", "getaddrinfo", "AF_INET", "AF_INET6", "SOCK_STREAM",
            "SOCK_DGRAM", "SOL_SOCKET", "SO_REUSEADDR"
        ),
        "dht" to listOf("DHT11", "DHT22"),
        "onewire" to listOf("OneWire"),
        "ds18x20" to listOf("DS18X20")
    )

    /** Members suggested after an instance of a known peripheral class. */
    private val classMembers: Map<String, List<String>> = mapOf(
        "Pin" to listOf(
            "value", "on", "off", "toggle", "init", "irq", "mode", "pull",
            "drive", "IN", "OUT", "OPEN_DRAIN", "PULL_UP", "PULL_DOWN",
            "IRQ_RISING", "IRQ_FALLING", "IRQ_LOW_LEVEL", "IRQ_HIGH_LEVEL"
        ),
        "Signal" to listOf("value", "on", "off"),
        "ADC" to listOf(
            "read", "read_u16", "read_uv", "atten", "width", "init",
            "ATTN_0DB", "ATTN_2_5DB", "ATTN_6DB", "ATTN_11DB"
        ),
        "PWM" to listOf("freq", "duty", "duty_u16", "duty_ns", "init", "deinit"),
        "DAC" to listOf("write", "write_timed"),
        "UART" to listOf(
            "init", "deinit", "read", "readline", "readinto", "write", "any",
            "sendbreak", "irq", "flush", "txdone"
        ),
        "I2C" to listOf(
            "init", "deinit", "scan", "start", "stop", "readfrom", "readfrom_into",
            "writeto", "writevto", "readfrom_mem", "readfrom_mem_into", "writeto_mem"
        ),
        "SoftI2C" to listOf(
            "init", "deinit", "scan", "start", "stop", "readfrom", "writeto",
            "readfrom_mem", "writeto_mem"
        ),
        "SPI" to listOf("init", "deinit", "read", "readinto", "write", "write_readinto"),
        "SoftSPI" to listOf("init", "deinit", "read", "readinto", "write", "write_readinto"),
        "Timer" to listOf("init", "deinit", "value", "ONE_SHOT", "PERIODIC"),
        "RTC" to listOf(
            "datetime", "init", "now", "alarm", "alarm_left", "cancel", "irq", "memory"
        ),
        "WDT" to listOf("feed"),
        "WLAN" to listOf(
            "active", "connect", "disconnect", "isconnected", "scan", "status",
            "ifconfig", "ipconfig", "config"
        ),
        "NeoPixel" to listOf("fill", "write"),
        "DS18X20" to listOf("scan", "convert_temp", "read_temp", "read_scratch"),
        "DHT11" to listOf("measure", "temperature", "humidity"),
        "DHT22" to listOf("measure", "temperature", "humidity"),
        "FrameBuffer" to listOf(
            "fill", "pixel", "hline", "vline", "line", "rect", "fill_rect",
            "text", "scroll", "blit", "ellipse", "poly"
        )
    )

    override val snippets = mapOf(
        "def" to "def \${1:name}(\${2:params}):\n    \${0}",
        "class" to "class \${1:Name}:\n    def __init__(self\${2:, params}):\n        \${0}",
        "if" to "if \${1:condition}:\n    \${0}",
        "for" to "for \${1:item} in \${2:items}:\n    \${0}",
        "while" to "while \${1:condition}:\n    \${0}",
        "try" to "try:\n    \${1}\nexcept Exception as e:\n    \${0}",
        "loop" to "while True:\n    \${0}\n    time.sleep(1)",
        "led" to "led = Pin(\${1:2}, Pin.OUT)\nled.value(\${0:1})",
        "button" to "\${1:button} = Pin(\${2:0}, Pin.IN, Pin.PULL_UP)\${0}",
        "pwm" to "pwm = PWM(Pin(\${1:2}), freq=\${2:1000}, duty=\${0:512})",
        "adc" to "adc = ADC(Pin(\${1:34}))\nvalue = adc.read()\${0}",
        "i2c" to "i2c = I2C(\${1:0}, scl=Pin(\${2:22}), sda=Pin(\${3:21}), freq=400000)\${0}",
        "uart" to "uart = UART(\${1:1}, baudrate=\${2:115200}, tx=Pin(\${3:17}), rx=Pin(\${4:16}))\${0}",
        "timer" to "timer = Timer(\${1:0})\ntimer.init(period=\${2:1000}, mode=Timer.PERIODIC, " +
                "callback=\${0:handler})",
        "irq" to "\${1:pin}.irq(trigger=Pin.IRQ_FALLING, handler=\${0:handler})",
        "wifi" to "wlan = network.WLAN(network.STA_IF)\nwlan.active(True)\n" +
                "wlan.connect('\${1:ssid}', '\${2:password}')\nwhile not wlan.isconnected():\n" +
                "    time.sleep(0.5)\nprint(wlan.ifconfig())\${0}"
    )

    override suspend fun provideCompletions(text: String, position: Int): List<CompletionItem> {
        val cursor = position.coerceIn(0, text.length)
        val separators = " \n\t(){}[],;:\"'="
        val wordStart =
            text.lastIndexOfAny(separators.toCharArray(), (cursor - 1).coerceAtLeast(0)) + 1
        val rawPrefix = text.substring(wordStart.coerceAtMost(cursor), cursor)

        // Member access: complete against the receiver instead of the global scope.
        if (rawPrefix.contains('.')) {
            return getMemberCompletions(text, rawPrefix).take(MAX_ITEMS)
        }

        if (rawPrefix.isEmpty()) return emptyList()
        val prefix = rawPrefix.lowercase()

        val completions = mutableListOf<CompletionItem>()

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

        return completions.distinctBy { it.label }.take(MAX_ITEMS)
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
        if (Regex("^\\s*(import|from)\\s+[\\w.]*$").containsMatchIn(currentLine)) {
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
        Regex("from\\s+(\\w+)\\s+import\\s+([^\\n]+)").findAll(text).forEach { match ->
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
     * Completions for `receiver.member`, where the receiver is either a known
     * module, a known peripheral class, or a variable assigned from one.
     */
    private fun getMemberCompletions(text: String, rawPrefix: String): List<CompletionItem> {
        val receiver = rawPrefix.substringBeforeLast('.').substringAfterLast('.')
        val memberPrefix = rawPrefix.substringAfterLast('.')
        if (receiver.isEmpty()) return emptyList()

        val members = moduleMembers[receiver]
            ?: moduleMembers[receiver.removePrefix("u")]
            ?: classMembers[receiver]
            ?: classMembers[inferVariableType(text, receiver)]
            ?: return emptyList()

        val detail = when {
            moduleMembers.containsKey(receiver) ||
                    moduleMembers.containsKey(receiver.removePrefix("u")) -> "$receiver module"

            classMembers.containsKey(receiver) -> receiver
            else -> "member"
        }

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

    /** Resolves `led = Pin(2, Pin.OUT)` or `led = machine.Pin(...)` back to `Pin`. */
    private fun inferVariableType(text: String, variable: String): String? {
        val match = Regex("\\b${Regex.escape(variable)}\\s*=\\s*(?:\\w+\\.)?(\\w+)\\s*\\(")
            .findAll(text)
            .lastOrNull() ?: return null
        return match.groupValues[1].takeIf { classMembers.containsKey(it) }
    }

    private companion object {
        const val MAX_ITEMS = 20
    }
}
