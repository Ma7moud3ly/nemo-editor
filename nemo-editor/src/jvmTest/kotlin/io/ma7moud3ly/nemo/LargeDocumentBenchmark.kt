package io.ma7moud3ly.nemo

import io.ma7moud3ly.nemo.lsp.completion.KotlinCompletionProvider
import io.ma7moud3ly.nemo.lsp.completion.MicroPythonCompletionProvider
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.model.Language
import io.ma7moud3ly.nemo.syntax.analysis.KotlinErrorDetector
import io.ma7moud3ly.nemo.syntax.analysis.PythonErrorDetector
import io.ma7moud3ly.nemo.syntax.highlighting.SyntaxHighlighter
import io.ma7moud3ly.nemo.syntax.indent.IndentGuideCalculator
import io.ma7moud3ly.nemo.syntax.tokenizer.KotlinTokenizer
import io.ma7moud3ly.nemo.syntax.tokenizer.PythonTokenizer
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.time.measureTime

/**
 * Times the work the editor does on the whole document for every edit, on
 * generated Kotlin and Python files of growing size, and prints a table.
 *
 * It asserts nothing; it is a baseline to compare changes against. To see the
 * table, run:
 * `./gradlew :nemo-editor:jvmTest --tests "*LargeDocumentBenchmark*" -i`
 */
class LargeDocumentBenchmark {

    private val sizes = listOf(1_000, 5_000, 20_000)

    @Test
    fun kotlin() = benchmark(
        language = "Kotlin",
        chunk = KOTLIN_CHUNK,
        highlighter = SyntaxHighlighter(KotlinTokenizer(), KotlinErrorDetector(), EditorThemes.MONOKAI),
        tokenize = { KotlinTokenizer().tokenize(it) },
        complete = { runBlocking { KotlinCompletionProvider().provideCompletions(it, it.length) } },
        codeLanguage = Language.KOTLIN
    )

    @Test
    fun python() = benchmark(
        language = "Python",
        chunk = PYTHON_CHUNK,
        highlighter = SyntaxHighlighter(PythonTokenizer(), PythonErrorDetector(), EditorThemes.MONOKAI),
        tokenize = { PythonTokenizer().tokenize(it) },
        complete = { runBlocking { MicroPythonCompletionProvider().provideCompletions(it, it.length) } },
        codeLanguage = Language.MICRO_PYTHON
    )

    private fun benchmark(
        language: String,
        chunk: String,
        highlighter: SyntaxHighlighter,
        tokenize: (String) -> Unit,
        complete: (String) -> Unit,
        codeLanguage: Language
    ) {
        val chunkLines = chunk.count { it == '\n' }
        println("\n$language, milliseconds per call")
        println("%8s %10s %10s %10s %10s %10s %10s".format(
            "lines", "chars", "tokenize", "highlight", "guides", "complete", "caret"
        ))

        for (lines in sizes) {
            // End with a partial word, so completion has a prefix to look up
            val code = chunk.repeat(lines / chunkLines) + "pri"
            val state = CodeState(code, codeLanguage)

            println("%8d %10d %10.1f %10.1f %10.1f %10.1f %10.3f".format(
                code.count { it == '\n' } + 1,
                code.length,
                time { tokenize(code) },
                time { highlighter.highlight(code) },
                time { IndentGuideCalculator.compute(code, 4) },
                time { complete(code) },
                // Moving the caret: the current line of every 100th offset
                time {
                    for (offset in code.indices step 100) {
                        state.setSelection(offset, offset)
                        state.currentLine
                    }
                } / (code.length / 100).coerceAtLeast(1)
            ))
        }
    }

    /**
     * Median milliseconds of [block] over a few runs, after warming up. The
     * median ignores a run slowed by garbage collection or another process.
     */
    private fun time(block: () -> Unit): Double {
        repeat(WARMUP_RUNS) { block() }
        val runs = List(RUNS) { measureTime(block).inWholeMicroseconds / 1000.0 }.sorted()
        return runs[runs.size / 2]
    }

    private companion object {
        const val WARMUP_RUNS = 5
        const val RUNS = 5

        val KOTLIN_CHUNK = """
            /** Shows a greeting. */
            @Composable
            fun Greeting(name: String, modifier: Modifier = Modifier) {
                val count = remember { mutableStateOf(0) }
                Column(modifier = modifier.padding(16.dp)) {
                    Text(text = "Hello ${'$'}name!", fontSize = 20.sp) // a comment
                    Button(onClick = { count.value++ }) { Text("Clicked ${'$'}{count.value}") }
                }
            }

        """.trimIndent() + "\n"

        val PYTHON_CHUNK = """
            # Blink an LED
            def blink(pin, times=3, delay=0.5):
                led = Pin(pin, Pin.OUT)
                for i in range(times):
                    led.value(1)
                    time.sleep(delay)  # wait
                    led.value(0)
                print("done", i, end="\n")

        """.trimIndent() + "\n"
    }
}
