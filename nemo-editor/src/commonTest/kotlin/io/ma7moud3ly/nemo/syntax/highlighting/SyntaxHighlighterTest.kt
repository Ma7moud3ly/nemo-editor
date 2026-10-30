package io.ma7moud3ly.nemo.syntax.highlighting

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import io.ma7moud3ly.nemo.model.EditorThemes
import io.ma7moud3ly.nemo.syntax.tokenizer.KotlinTokenizer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SyntaxHighlighterTest {

    private val theme = EditorThemes.MONOKAI
    private val highlighter = SyntaxHighlighter(KotlinTokenizer(), null, theme)

    @Test
    fun theWholeTextGetsTheForegroundColor() {
        val first = highlighter.highlight("a b").spanStyles.first()
        assertEquals(Color(theme.foreground), first.item.color)
        assertEquals(0, first.start)
        assertEquals(3, first.end)
    }

    @Test
    fun neighboringKeywordsShareOneStyle() {
        val keywords = highlighter.highlight("private fun a() {}").spanStyles
            .filter { it.item.fontWeight == FontWeight.Bold }
        assertEquals(listOf(0 to 11), keywords.map { it.start to it.end })
    }

    @Test
    fun whitespaceGetsNoStyleOfItsOwn() {
        val styles = highlighter.highlight("val    x").spanStyles.drop(1)
        assertTrue(styles.none { it.start == 3 && it.end == 7 })
    }
}
