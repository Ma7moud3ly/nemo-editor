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

    @Test
    fun composeIdentifiersStartingWithKeywordsAreKept() {
        val code = """
            val foreground = Color.White
            stars.forEach {
                drawStar(it, foreground)
            }
            """
        assertEquals(expected(code), format(code))
    }

    @Test
    fun keywordPrefixesInIdentifiersAreNotSplit() {
        val code = """
            val double = 1.0
            val returnValue = double
            val for2 = 2
            val for_each = for2
            """
        assertEquals(expected(code), format(code))
    }

    @Test
    fun controlFlowKeywordsStillFormat() {
        val code = """
            if(visible){
            for(star in stars){
            drawStar(star)
            }
            }
            """
        val formatted = """
            if (visible) {
                for (star in stars) {
                    drawStar(star)
                }
            }
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun callsHaveNoSpaceBeforeTheirParenthesis() {
        val code = """
            val (a, b) = pair
            val f = fun(x: Int) = x
            when(x) {
            }
            drawStar (star)
            """
        val formatted = """
            val (a, b) = pair
            val f = fun(x: Int) = x
            when (x) {
            }
            drawStar(star)
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun operatorsAreReadWhole() {
        val code = """
            val radius = if (i % 2==0) cell/2.5f else cell / 5.5f
            count+=1
            val same = a!=b && c<=d || e>=f
            val less = a<b
            val name = user?.name ?: "none"
            """
        val formatted = """
            val radius = if (i % 2 == 0) cell / 2.5f else cell / 5.5f
            count += 1
            val same = a != b && c <= d || e >= f
            val less = a < b
            val name = user?.name ?: "none"
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun signsAndExponentsStayAttached() {
        val code = """
            val angle = -PI / 2 + i * PI / 5
            val small = 1e-5
            val sign = if (x < 0) -1 else 1
            foo(*args)
            """
        assertEquals(expected(code), format(code))
    }

    @Test
    fun genericsAreKept() {
        val code = """
            val map: Map<String, List<Int>> = mapOf()
            fun <T : Comparable<T>> sort(list: List<T>) {}
            val all = listOf<Int>()
            val onClick: Lazy<() -> Unit> = lazy {}
            """
        assertEquals(expected(code), format(code))
    }
}
