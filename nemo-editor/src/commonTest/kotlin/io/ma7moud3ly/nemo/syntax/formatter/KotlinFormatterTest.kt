package io.ma7moud3ly.nemo.syntax.formatter

import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinFormatterTest {

    private val formatter = KotlinFormatter()

    private fun format(code: String) = formatter.format(code.trimIndent())

    private fun expected(code: String) = code.trimIndent() + "\n"

    @Test
    fun argumentsAfterTheOpeningParenthesis() {
        val formatted = expected(
            """
            @Preview
            @Composable
            fun MyScreen() {
                Column(modifier = Modifier.alpha(0.5f),
                    verticalArrangement = Arrangement.spacedBy(16.sp)
                ) {
                    Surface(color = Color.Green, onClick = {}) {
                        Text(
                            text = "Hello",
                            fontSize = 25.sp
                        )
                    }
                }
            }
            """
        )
        assertEquals(formatted, formatter.format(formatted))

        val misindented = """
            @Preview
            @Composable
            fun MyScreen() {
                Column(modifier = Modifier.alpha(0.5f),
                verticalArrangement = Arrangement.spacedBy(16.sp)
            ) {
                Surface(color = Color.Green, onClick = {}) {
                    Text(
                        text = "Hello",
                        fontSize = 25.sp
                    )
                }
            }
            }
            """
        assertEquals(formatted, format(misindented))
    }

    @Test
    fun lambdaAfterAWrappedArgumentList() {
        val code = """
            foo(a,
            b) {
            bar()
            }
            """
        val formatted = """
            foo(a,
                b) {
                bar()
            }
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun callChainsAreIndented() {
        val code = """
            val m = Modifier
            .fillMaxSize()
            .padding(8.dp)
            val x = 1
            """
        val formatted = """
            val m = Modifier
                .fillMaxSize()
                .padding(8.dp)
            val x = 1
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun stringsAndCommentsAreKept() {
        val code = """
            val s = "a=b{"
            val url = "http://a.b" // a=b
            val c = '{'
            val x = 1
            """
        assertEquals(expected(code), format(code))
    }

    @Test
    fun docCommentsAreAligned() {
        val code = """
            /**
            * Doc
            */
            fun a() {}
            """
        val formatted = """
            /**
             * Doc
             */
            fun a() {}
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun multiLineStringsAreKept() {
        val code = "val s = \"\"\"\n  keep {\n\"\"\"\nval t = 1"
        assertEquals("$code\n", formatter.format(code))
    }
}
