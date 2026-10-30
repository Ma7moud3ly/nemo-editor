package io.ma7moud3ly.nemo.model

import kotlin.test.Test
import kotlin.test.assertEquals

class CodeStateLinesTest {

    @Test
    fun countsLines() {
        assertEquals(1, CodeState("", Language.KOTLIN).totalLines)
        assertEquals(3, CodeState("a\nb\nc", Language.KOTLIN).totalLines)
        assertEquals(3, CodeState("a\n\n", Language.KOTLIN).totalLines)
    }

    @Test
    fun findsTheCaretLineAndColumn() {
        val state = CodeState("ab\ncd\n\nef", Language.KOTLIN)
        val expected = listOf(
            0 to (1 to 1), 2 to (1 to 3), 3 to (2 to 1), 5 to (2 to 3),
            6 to (3 to 1), 7 to (4 to 1), 9 to (4 to 3)
        )
        for ((offset, lineAndColumn) in expected) {
            state.setSelection(offset, offset)
            assertEquals(lineAndColumn, state.currentLine to state.currentColumn, "offset $offset")
        }
    }

    @Test
    fun followsEdits() {
        val state = CodeState("a", Language.KOTLIN)
        state.updateText("a\nb\nc")
        assertEquals(3, state.totalLines)
        assertEquals(3, state.currentLine)
    }
}
