package io.ma7moud3ly.nemo.lsp.completion

/**
 * MicroPython names suggested by [MicroPythonCompletionProvider]: modules,
 * their members, the members of common classes, call parameters and the
 * classes that calls return.
 */
internal object MicroPythonBindings {

    /** Modules shipped with most MicroPython ports (both `u`-prefixed and plain). */
    val modules = listOf(
        "machine", "micropython", "network", "time", "utime", "sys", "usys",
        "os", "uos", "gc", "json", "ujson", "math", "cmath", "random",
        "urandom", "struct", "ustruct", "binascii", "ubinascii", "hashlib",
        "uhashlib", "socket", "usocket", "ssl", "ussl", "select", "uselect",
        "collections", "ucollections", "io", "uio", "re", "ure", "errno",
        "uerrno", "heapq", "uheapq", "asyncio", "uasyncio", "array", "esp",
        "esp32", "rp2", "pyb", "bluetooth", "neopixel", "onewire", "ds18x20",
        "dht", "framebuf", "webrepl", "_thread", "urequests", "requests",
        "umqtt", "ntptime", "espnow", "mip", "vfs", "deflate", "cryptolib",
        "ssd1306"
    )

    /** Members suggested right after `<module>.` */
    val moduleMembers: Map<String, List<String>> = mapOf(
        "machine" to listOf(
            "Pin", "Signal", "ADC", "ADCBlock", "PWM", "DAC", "UART", "SPI",
            "SoftSPI", "I2C", "SoftI2C", "I2S", "RTC", "Timer", "WDT", "SDCard",
            "reset", "soft_reset", "reset_cause", "bootloader", "freq",
            "deepsleep", "lightsleep", "idle", "wake_reason", "unique_id",
            "disable_irq", "enable_irq", "time_pulse_us", "bitstream", "mem8",
            "mem16", "mem32", "PWRON_RESET", "HARD_RESET", "WDT_RESET",
            "DEEPSLEEP_RESET", "SOFT_RESET"
        ),
        "time" to listOf(
            "sleep", "sleep_ms", "sleep_us", "ticks_ms", "ticks_us", "ticks_cpu",
            "ticks_add", "ticks_diff", "time", "time_ns", "localtime", "gmtime",
            "mktime"
        ),
        "network" to listOf(
            "WLAN", "LAN", "STA_IF", "AP_IF", "hostname", "country", "phy_mode",
            "ipconfig", "STAT_IDLE", "STAT_CONNECTING", "STAT_GOT_IP",
            "STAT_WRONG_PASSWORD", "STAT_NO_AP_FOUND", "STAT_CONNECT_FAIL"
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
            "pi", "e", "inf", "nan", "sqrt", "pow", "exp", "log", "log10", "log2",
            "sin", "cos", "tan", "asin", "acos", "atan", "atan2", "ceil", "floor",
            "trunc", "fabs", "fmod", "radians", "degrees", "isnan", "isinf",
            "isfinite", "modf", "copysign", "hypot"
        ),
        "random" to listOf(
            "random", "randint", "randrange", "uniform", "choice", "getrandbits",
            "seed", "shuffle"
        ),
        "struct" to listOf("calcsize", "pack", "pack_into", "unpack", "unpack_from"),
        "binascii" to listOf("hexlify", "unhexlify", "a2b_base64", "b2a_base64", "crc32"),
        "hashlib" to listOf("sha256", "sha1", "md5"),
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
            "wait_for_ms", "Event", "ThreadSafeFlag", "Lock", "Task",
            "get_event_loop", "new_event_loop", "run_until_complete",
            "current_task", "open_connection", "start_server"
        ),
        "socket" to listOf(
            "socket", "getaddrinfo", "AF_INET", "AF_INET6", "SOCK_STREAM",
            "SOCK_DGRAM", "SOCK_RAW", "IPPROTO_TCP", "IPPROTO_UDP", "SOL_SOCKET",
            "SO_REUSEADDR"
        ),
        "ssl" to listOf("wrap_socket", "SSLContext", "PROTOCOL_TLS_CLIENT", "PROTOCOL_TLS_SERVER"),
        "select" to listOf("poll", "select", "POLLIN", "POLLOUT", "POLLERR", "POLLHUP"),
        "requests" to listOf("get", "post", "put", "patch", "delete", "head", "request"),
        "ntptime" to listOf("settime", "time", "host"),
        "_thread" to listOf("start_new_thread", "allocate_lock", "get_ident", "exit"),
        "espnow" to listOf("ESPNow", "ESPNowAIO"),
        "mip" to listOf("install"),
        "array" to listOf("array"),
        "collections" to listOf("deque", "namedtuple", "OrderedDict"),
        "io" to listOf("open", "StringIO", "BytesIO"),
        "re" to listOf("compile", "match", "search", "sub", "split"),
        "dht" to listOf("DHT11", "DHT22"),
        "onewire" to listOf("OneWire"),
        "ds18x20" to listOf("DS18X20"),
        "ssd1306" to listOf("SSD1306_I2C", "SSD1306_SPI")
    )

    private val frameBufferMembers = listOf(
        "fill", "pixel", "hline", "vline", "line", "rect", "fill_rect", "text",
        "scroll", "blit", "ellipse", "poly"
    )

    /** Members suggested after an instance of a known class. */
    val classMembers: Map<String, List<String>> = mapOf(
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
        "I2S" to listOf("init", "deinit", "readinto", "write", "irq", "shift", "RX", "TX", "MONO", "STEREO"),
        "Timer" to listOf("init", "deinit", "value", "ONE_SHOT", "PERIODIC"),
        "RTC" to listOf(
            "datetime", "init", "now", "alarm", "alarm_left", "cancel", "irq", "memory"
        ),
        "WDT" to listOf("feed"),
        "SDCard" to listOf("readblocks", "writeblocks", "ioctl", "info"),
        "WLAN" to listOf(
            "active", "connect", "disconnect", "isconnected", "scan", "status",
            "ifconfig", "ipconfig", "config"
        ),
        "NeoPixel" to listOf("fill", "write"),
        "OneWire" to listOf(
            "reset", "readbit", "readbyte", "writebit", "writebyte", "write",
            "select_rom", "scan", "crc8"
        ),
        "DS18X20" to listOf("scan", "convert_temp", "read_temp", "read_scratch"),
        "DHT11" to listOf("measure", "temperature", "humidity"),
        "DHT22" to listOf("measure", "temperature", "humidity"),
        "FrameBuffer" to frameBufferMembers,
        "SSD1306_I2C" to frameBufferMembers + listOf(
            "show", "poweroff", "poweron", "contrast", "invert", "rotate"
        ),
        "SSD1306_SPI" to frameBufferMembers + listOf(
            "show", "poweroff", "poweron", "contrast", "invert", "rotate"
        ),
        "StateMachine" to listOf(
            "init", "active", "restart", "exec", "get", "put", "rx_fifo", "tx_fifo", "irq"
        ),
        "BLE" to listOf(
            "active", "config", "irq", "gap_advertise", "gap_scan", "gap_connect",
            "gap_disconnect", "gatts_register_services", "gatts_read", "gatts_write",
            "gatts_notify", "gatts_indicate", "gattc_discover_services",
            "gattc_read", "gattc_write"
        ),
        "Socket" to listOf(
            "accept", "bind", "close", "connect", "listen", "recv", "recvfrom",
            "send", "sendall", "sendto", "setblocking", "setsockopt", "settimeout",
            "makefile", "read", "readinto", "readline", "write"
        ),
        "Response" to listOf(
            "text", "content", "json", "status_code", "reason", "headers",
            "encoding", "close"
        ),
        "MQTTClient" to listOf(
            "connect", "disconnect", "ping", "publish", "subscribe", "set_callback",
            "set_last_will", "wait_msg", "check_msg"
        ),
        "ESPNow" to listOf(
            "active", "config", "add_peer", "del_peer", "get_peer", "get_peers",
            "mod_peer", "send", "recv", "irecv", "any", "irq", "stats"
        ),
        "Event" to listOf("set", "clear", "is_set", "wait"),
        "Lock" to listOf("acquire", "release", "locked"),
        "Task" to listOf("cancel", "done"),
        "Partition" to listOf(
            "info", "readblocks", "writeblocks", "ioctl", "set_boot", "get_next_update",
            "mark_app_valid_cancel_rollback", "BOOT", "RUNNING"
        ),
        "NVS" to listOf("set_i32", "get_i32", "set_blob", "get_blob", "erase_key", "commit")
    )

    /**
     * Parameters suggested inside a call. A key is a constructor, such as
     * `Pin`, a method, such as `Pin.irq`, or a module function, such as
     * `time.sleep_ms`.
     */
    val parameters: Map<String, List<String>> = mapOf(
        // Built-ins
        "print" to listOf("sep", "end", "file"),
        "open" to listOf("file", "mode", "buffering", "encoding"),
        "sorted" to listOf("key", "reverse"),
        "max" to listOf("key", "default"),
        "min" to listOf("key", "default"),
        // machine
        "Pin" to listOf("id", "mode", "pull", "value", "drive", "alt"),
        "Pin.init" to listOf("mode", "pull", "value", "drive", "alt"),
        "Pin.irq" to listOf("handler", "trigger", "priority", "wake", "hard"),
        "Signal" to listOf("pin_obj", "invert"),
        "ADC" to listOf("pin", "atten"),
        "ADC.init" to listOf("atten"),
        "PWM" to listOf("dest", "freq", "duty_u16", "duty_ns", "invert"),
        "PWM.init" to listOf("freq", "duty_u16", "duty_ns", "invert"),
        "UART" to listOf(
            "id", "baudrate", "bits", "parity", "stop", "tx", "rx", "rts", "cts",
            "txbuf", "rxbuf", "timeout", "timeout_char", "invert", "flow"
        ),
        "UART.init" to listOf(
            "baudrate", "bits", "parity", "stop", "tx", "rx", "rts", "cts",
            "txbuf", "rxbuf", "timeout", "timeout_char", "invert", "flow"
        ),
        "UART.irq" to listOf("handler", "trigger", "hard"),
        "SPI" to listOf(
            "id", "baudrate", "polarity", "phase", "bits", "firstbit", "sck",
            "mosi", "miso"
        ),
        "SoftSPI" to listOf(
            "baudrate", "polarity", "phase", "bits", "firstbit", "sck", "mosi", "miso"
        ),
        "SPI.init" to listOf(
            "baudrate", "polarity", "phase", "bits", "firstbit", "sck", "mosi", "miso"
        ),
        "I2C" to listOf("id", "scl", "sda", "freq", "timeout"),
        "SoftI2C" to listOf("scl", "sda", "freq", "timeout"),
        "I2C.readfrom" to listOf("addr", "nbytes", "stop"),
        "I2C.writeto" to listOf("addr", "buf", "stop"),
        "I2C.readfrom_mem" to listOf("addr", "memaddr", "nbytes", "addrsize"),
        "I2C.writeto_mem" to listOf("addr", "memaddr", "buf", "addrsize"),
        "I2S" to listOf(
            "id", "sck", "ws", "sd", "mck", "mode", "bits", "format", "rate", "ibuf"
        ),
        "RTC" to listOf("id", "datetime"),
        "Timer" to listOf("id", "mode", "period", "freq", "callback"),
        "Timer.init" to listOf("mode", "period", "freq", "callback"),
        "WDT" to listOf("id", "timeout"),
        "SDCard" to listOf(
            "slot", "width", "cd", "wp", "sck", "miso", "mosi", "cs", "freq"
        ),
        "machine.deepsleep" to listOf("time_ms"),
        "machine.lightsleep" to listOf("time_ms"),
        "machine.time_pulse_us" to listOf("pin", "pulse_level", "timeout_us"),
        // time
        "time.sleep" to listOf("seconds"),
        "time.sleep_ms" to listOf("ms"),
        "time.sleep_us" to listOf("us"),
        "time.ticks_diff" to listOf("ticks1", "ticks2"),
        "time.ticks_add" to listOf("ticks", "delta"),
        // network
        "WLAN" to listOf("interface_id"),
        "WLAN.connect" to listOf("ssid", "key", "bssid"),
        "WLAN.config" to listOf(
            "ssid", "essid", "key", "password", "channel", "hidden", "security",
            "authmode", "txpower", "pm", "hostname", "mac", "reconnects"
        ),
        "WLAN.ipconfig" to listOf("dhcp4", "addr4", "gw4", "dns"),
        // drivers
        "NeoPixel" to listOf("pin", "n", "bpp", "timing"),
        "OneWire" to listOf("pin"),
        "DS18X20" to listOf("onewire"),
        "DHT11" to listOf("pin"),
        "DHT22" to listOf("pin"),
        "FrameBuffer" to listOf("buffer", "width", "height", "format", "stride"),
        "SSD1306_I2C" to listOf("width", "height", "i2c", "addr", "external_vcc"),
        "SSD1306_SPI" to listOf("width", "height", "spi", "dc", "res", "cs", "external_vcc"),
        "FrameBuffer.text" to listOf("s", "x", "y", "c"),
        "FrameBuffer.rect" to listOf("x", "y", "w", "h", "c", "f"),
        "FrameBuffer.ellipse" to listOf("x", "y", "xr", "yr", "c", "f", "m"),
        "StateMachine" to listOf(
            "id", "program", "freq", "in_base", "out_base", "set_base", "jmp_pin",
            "sideset_base", "in_shiftdir", "out_shiftdir", "push_thresh",
            "pull_thresh"
        ),
        "rp2.asm_pio" to listOf(
            "out_init", "set_init", "sideset_init", "in_shiftdir", "out_shiftdir",
            "autopush", "autopull", "push_thresh", "pull_thresh", "fifo_join"
        ),
        // networking
        "socket.socket" to listOf("af", "type", "proto"),
        "socket.getaddrinfo" to listOf("host", "port", "af", "type", "proto", "flags"),
        "Socket.settimeout" to listOf("value"),
        "requests.get" to listOf("url", "params", "data", "json", "headers", "auth", "timeout"),
        "requests.post" to listOf("url", "data", "json", "headers", "auth", "timeout"),
        "requests.put" to listOf("url", "data", "json", "headers", "auth", "timeout"),
        "requests.patch" to listOf("url", "data", "json", "headers", "auth", "timeout"),
        "requests.delete" to listOf("url", "data", "json", "headers", "auth", "timeout"),
        "requests.request" to listOf(
            "method", "url", "data", "json", "headers", "auth", "timeout"
        ),
        "MQTTClient" to listOf(
            "client_id", "server", "port", "user", "password", "keepalive", "ssl",
            "ssl_params"
        ),
        "MQTTClient.publish" to listOf("topic", "msg", "retain", "qos"),
        "MQTTClient.subscribe" to listOf("topic", "qos"),
        "MQTTClient.connect" to listOf("clean_session"),
        "ESPNow.send" to listOf("mac", "msg", "sync"),
        "ESPNow.recv" to listOf("timeout_ms"),
        "BLE.gap_advertise" to listOf("interval_us", "adv_data", "resp_data", "connectable"),
        "BLE.gap_scan" to listOf("duration_ms", "interval_us", "window_us", "active"),
        // others
        "json.dumps" to listOf("obj", "separators"),
        "asyncio.sleep" to listOf("t"),
        "asyncio.wait_for" to listOf("awaitable", "timeout"),
        "asyncio.gather" to listOf("return_exceptions"),
        "mip.install" to listOf("package", "index", "target", "version", "mpy")
    )

    /** Classes returned by calls, keyed as in [parameters]. */
    val returnTypes: Map<String, String> = mapOf(
        "socket.socket" to "Socket",
        "requests.get" to "Response",
        "requests.post" to "Response",
        "requests.put" to "Response",
        "requests.patch" to "Response",
        "requests.delete" to "Response",
        "requests.head" to "Response",
        "requests.request" to "Response",
        "asyncio.create_task" to "Task",
        "_thread.allocate_lock" to "Lock",
        "espnow.ESPNowAIO" to "ESPNow"
    )

    /**
     * Code inserted for a snippet's trigger. `${n:default}` marks a field and
     * is replaced with its default text.
     */
    val snippets = mapOf(
        "def" to "def \${1:name}(\${2:params}):\n    \${0}",
        "class" to "class \${1:Name}:\n    def __init__(self\${2:, params}):\n        \${0}",
        "if" to "if \${1:condition}:\n    \${0}",
        "for" to "for \${1:item} in \${2:items}:\n    \${0}",
        "while" to "while \${1:condition}:\n    \${0}",
        "try" to "try:\n    \${1}\nexcept Exception as e:\n    \${0}",
        "loop" to "while True:\n    \${0}\n    time.sleep(1)",
        "led" to "led = Pin(\${1:2}, Pin.OUT)\nled.value(\${0:1})",
        "button" to "\${1:button} = Pin(\${2:0}, Pin.IN, Pin.PULL_UP)\${0}",
        "pwm" to "pwm = PWM(Pin(\${1:2}), freq=\${2:1000}, duty_u16=\${0:32768})",
        "adc" to "adc = ADC(Pin(\${1:34}))\nvalue = adc.read_u16()\${0}",
        "i2c" to "i2c = I2C(\${1:0}, scl=Pin(\${2:22}), sda=Pin(\${3:21}), freq=400000)\${0}",
        "uart" to "uart = UART(\${1:1}, baudrate=\${2:115200}, tx=Pin(\${3:17}), rx=Pin(\${4:16}))\${0}",
        "timer" to "timer = Timer(\${1:0})\ntimer.init(period=\${2:1000}, mode=Timer.PERIODIC, " +
                "callback=\${0:handler})",
        "irq" to "\${1:pin}.irq(trigger=Pin.IRQ_FALLING, handler=\${0:handler})",
        "wifi" to "wlan = network.WLAN(network.STA_IF)\nwlan.active(True)\n" +
                "wlan.connect('\${1:ssid}', '\${2:password}')\nwhile not wlan.isconnected():\n" +
                "    time.sleep(0.5)\nprint(wlan.ifconfig())\${0}",
        "get" to "response = requests.get('\${1:url}')\nprint(response.status_code, response.text)\n" +
                "response.close()\${0}",
        "mqtt" to "client = MQTTClient('\${1:client_id}', '\${2:broker}')\nclient.connect()\n" +
                "client.publish(b'\${3:topic}', b'\${0:message}')",
        "neopixel" to "np = NeoPixel(Pin(\${1:4}), \${2:8})\nnp[0] = (\${3:255}, 0, 0)\nnp.write()\${0}",
        "async" to "async def main():\n    \${0}\n\nasyncio.run(main())"
    )
}
