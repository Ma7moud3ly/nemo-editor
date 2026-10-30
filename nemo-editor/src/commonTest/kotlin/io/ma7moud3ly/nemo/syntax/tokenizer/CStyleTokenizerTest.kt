package io.ma7moud3ly.nemo.syntax.tokenizer

import io.ma7moud3ly.nemo.model.TokenType
import kotlin.test.Test
import kotlin.test.assertEquals

class CStyleTokenizerTest {

    private fun typeOf(tokenizer: CStyleTokenizer, code: String, word: String): TokenType? =
        tokenizer.tokenize(code).firstOrNull { it.text == word }?.type

    @Test
    fun javaKeywordsAndAnnotations() {
        val java = CStyleTokenizer.java()
        val code = "@Override\npublic static void main(String[] args) { int x = new Foo(); }"
        assertEquals(TokenType.TYPE, typeOf(java, code, "@Override"))
        assertEquals(TokenType.KEYWORD, typeOf(java, code, "void"))
        assertEquals(TokenType.KEYWORD, typeOf(java, code, "new"))
        assertEquals(TokenType.FUNCTION, typeOf(java, code, "main"))
    }

    @Test
    fun javaScriptTemplateStrings() {
        val tokens = CStyleTokenizer.javaScript().tokenize("const s = `a\nb`;")
        assertEquals(TokenType.STRING, tokens.first { it.text == "`a\nb`" }.type)
    }

    @Test
    fun cPreprocessorDirectives() {
        assertEquals(
            TokenType.KEYWORD,
            typeOf(CStyleTokenizer.cFamily(), "#include <stdio.h>", "#include")
        )
    }

    @Test
    fun unterminatedStringsEndAtTheLine() {
        val tokens = CStyleTokenizer.java().tokenize("String s = \"abc\nint x;")
        assertEquals(TokenType.KEYWORD, tokens.first { it.text == "int" }.type)
    }
}
