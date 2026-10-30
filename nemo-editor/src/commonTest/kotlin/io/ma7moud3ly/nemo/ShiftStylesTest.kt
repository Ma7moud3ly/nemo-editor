package io.ma7moud3ly.nemo

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import kotlin.test.Test
import kotlin.test.assertEquals

class ShiftStylesTest {

    private val red = SpanStyle(color = Color.Red)
    private val blue = SpanStyle(color = Color.Blue)
    private val green = SpanStyle(color = Color.Green)

    // "val x = 1" with `val` red, `x` blue and `1` green
    private val highlighted = AnnotatedString(
        "val x = 1",
        listOf(
            AnnotatedString.Range(red, 0, 3),
            AnnotatedString.Range(blue, 4, 5),
            AnnotatedString.Range(green, 8, 9)
        )
    )

    private fun ranges(text: AnnotatedString) =
        text.spanStyles.map { Triple(it.item, it.start, it.end) }

    @Test
    fun stylesAfterAnInsertionMove() {
        val shifted = shiftStyles(highlighted, "val xy = 1")
        assertEquals("val xy = 1", shifted.text)
        assertEquals(
            listOf(Triple(red, 0, 3), Triple(blue, 4, 5), Triple(green, 9, 10)),
            ranges(shifted)
        )
    }

    @Test
    fun stylesAfterADeletionMove() {
        val shifted = shiftStyles(highlighted, "val x=1")
        assertEquals(
            listOf(Triple(red, 0, 3), Triple(blue, 4, 5), Triple(green, 6, 7)),
            ranges(shifted)
        )
    }

    @Test
    fun aStyleThatHoldsTheEditKeepsIt() {
        val shifted = shiftStyles(highlighted, "var x = 1")
        assertEquals(
            listOf(Triple(red, 0, 3), Triple(blue, 4, 5), Triple(green, 8, 9)),
            ranges(shifted)
        )
    }

    @Test
    fun stylesTheEditCutsThroughAreDropped() {
        val shifted = shiftStyles(highlighted, "vaX = 1")
        assertEquals(listOf(Triple(green, 6, 7)), ranges(shifted))
    }
}
