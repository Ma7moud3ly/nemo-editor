package io.ma7moud3ly.nemo.managers

import io.ma7moud3ly.nemo.InternalNemoApi
import io.ma7moud3ly.nemo.model.CodeState
import io.ma7moud3ly.nemo.model.EditorSettings
import io.ma7moud3ly.nemo.model.Language
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(InternalNemoApi::class)
class LineBreakTest {

    /** Presses Enter in [code], where `|` marks the caret, and returns the result the same way. */
    private fun enter(code: String, language: Language = Language.KOTLIN): String {
        val state = CodeState(
            initialCode = code.replace("|", ""),
            language = language,
            initialCursorPosition = code.indexOf('|')
        )
        state.insertLineBreak(AutoIndentHandler(language, EditorSettings()))
        val caret = state.cursorPosition
        return state.code.substring(0, caret) + "|" + state.code.substring(caret)
    }

    @Test
    fun theClosingBraceGoesOnItsOwnLine() {
        assertEquals("    Column{\n        |\n    }", enter("    Column{|}"))
    }

    @Test
    fun theClosingParenthesisGoesOnItsOwnLine() {
        assertEquals("Text(\n    |\n)", enter("Text(|)"))
    }

    @Test
    fun anOpeningBracketIndentsTheNextLine() {
        assertEquals("if (x) {\n    |", enter("if (x) {|"))
        assertEquals("Column(\n    |", enter("Column(|"))
    }

    @Test
    fun aPlainLineKeepsItsIndentation() {
        assertEquals("    val x = 1\n    |", enter("    val x = 1|"))
    }

    @Test
    fun pythonBrackets() {
        assertEquals("x = [\n    |\n]", enter("x = [|]", Language.PYTHON))
    }

    @Test
    fun plainTextIsNotSplit() {
        assertEquals("a{\n|}", enter("a{|}", Language.TEXT))
    }
}
