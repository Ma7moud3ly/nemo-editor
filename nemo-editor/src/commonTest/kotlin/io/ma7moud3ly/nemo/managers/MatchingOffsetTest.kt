package io.ma7moud3ly.nemo.managers

import kotlin.test.Test
import kotlin.test.assertEquals

class MatchingOffsetTest {

    /** The text before the caret, in [formatted], for a caret before [marker] in [original]. */
    private fun before(original: String, formatted: String, marker: String): String {
        val offset = original.indexOf(marker)
        return formatted.take(matchingOffset(original, formatted, offset))
    }

    @Test
    fun staysOnTheSameCode() {
        val original = "fun a(){\nval x=1\n}"
        val formatted = "fun a() {\n    val x = 1\n}\n"
        assertEquals("fun a() {\n    val x", before(original, formatted, "=1"))
    }

    @Test
    fun staysInTheIndentationOfTheSameLine() {
        assertEquals("if a:\n    ", before("if a:\n  b", "if a:\n    b\n", "b"))
    }

    @Test
    fun staysAtTheStartOfALine() {
        assertEquals("a\n", before("a\nb", "a\n    b\n", "b"))
    }

    @Test
    fun staysBeforeSpacesOnTheSameLine() {
        assertEquals("foo", before("foo  bar", "foo bar\n", "  bar"))
    }

    @Test
    fun startAndEnd() {
        assertEquals(0, matchingOffset("a=1", "a = 1\n", 0))
        assertEquals("a = 1", "a = 1\n".take(matchingOffset("a=1", "a = 1\n", 3)))
    }
}
