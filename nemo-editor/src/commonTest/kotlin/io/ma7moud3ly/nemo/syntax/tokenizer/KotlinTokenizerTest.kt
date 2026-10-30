package io.ma7moud3ly.nemo.syntax.tokenizer

import io.ma7moud3ly.nemo.model.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals

class KotlinTokenizerTest {

    private fun typeOf(code: String, word: String): TokenType? =
        KotlinTokenizer().tokenize(code).firstOrNull { it.text == word }?.type

    @Test
    fun emptyBacktickNameDoesNotThrow() {
        listOf("`", "``", "a = `", "```kotlin\nprint('Hello')\n}\n```").forEach {
            KotlinTokenizer().tokenize(it)
        }
    }

    @Test
    fun composableCallsAreFunctions() {
        assertEquals(TokenType.FUNCTION, typeOf("Column {\n}", "Column"))
        assertEquals(TokenType.FUNCTION, typeOf("Text(\"Hi\")", "Text"))
    }

    @Test
    fun composablesDeclaredInTheFileAreFunctions() {
        val code = "@Composable\nfun Greeting() {}\n\nGreeting()"
        assertEquals(TokenType.FUNCTION, typeOf(code, "Greeting"))
    }

    @Test
    fun otherCapitalizedNamesStayTypes() {
        assertEquals(TokenType.TYPE, typeOf("val c = Color(0xFF000000)", "Color"))
        assertEquals(TokenType.TYPE, typeOf("val m: Modifier", "Modifier"))
    }
}
