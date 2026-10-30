package io.ma7moud3ly.nemo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.input.OffsetMapping
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class HighlightTransformationTest {

    private val red = SpanStyle(color = Color.Red)
    private val highlighted = AnnotatedString("val x", listOf(AnnotatedString.Range(red, 0, 3)))
    private val transformation = HighlightTransformation(highlighted)

    @Test
    fun showsTheHighlightOfTheSameText() {
        val transformed = transformation.filter(AnnotatedString("val x"))
        assertSame(highlighted, transformed.text)
        assertSame(OffsetMapping.Identity, transformed.offsetMapping)
    }

    @Test
    fun movesTheColorsOntoANewerText() {
        val transformed = transformation.filter(AnnotatedString("  val x"))
        assertEquals("  val x", transformed.text.text)
        assertEquals(listOf(AnnotatedString.Range(red, 2, 5)), transformed.text.spanStyles)
    }
}
