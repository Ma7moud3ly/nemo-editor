package io.ma7moud3ly.nemo.managers

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.Language
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NormalizeLineEndingsTest {

    @Test
    fun crlfAndLoneCrBecomeLf() {
        val value = normalizeLineEndings(TextFieldValue("a\r\nb\rc"))
        assertEquals("a\nb\nc", value.text)
    }

    @Test
    fun selectionStaysOnTheSameCharacters() {
        // Selects "b" and everything up to the end.
        val value = normalizeLineEndings(
            TextFieldValue("a\r\nb\r\nc", selection = TextRange(3, 7))
        )
        assertEquals("a\nb\nc", value.text)
        assertEquals(TextRange(2, 5), value.selection)
    }

    @Test
    fun selectAllCoversTheWholeText() {
        val state = CodeState("one\r\ntwo\r\nthree", Language.KOTLIN)
        state.setSelection(0, state.code.length)

        assertEquals("one\ntwo\nthree", state.code)
        assertEquals(TextRange(0, state.code.length), state.selection)
        assertFalse(state.contentChanged)
    }
}
