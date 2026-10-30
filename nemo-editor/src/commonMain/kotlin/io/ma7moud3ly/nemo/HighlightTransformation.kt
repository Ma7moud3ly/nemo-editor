package io.ma7moud3ly.nemo

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

/**
 * Shows the text of a text field with the colors of [highlighted].
 *
 * The text field then lays out and draws the colored text itself, so the
 * editor needs a single text layout, and the caret and selection always match
 * the glyphs. The text is not changed, only styled, so offsets map one to one.
 *
 * Keep one instance per [highlighted]: the text field filters its text again
 * whenever the transformation changes.
 */
internal class HighlightTransformation(private val highlighted: AnnotatedString) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        val styled = when {
            text.text == highlighted.text -> highlighted
            // The field may hold a newer text than the highlight; move the
            // colors onto it so they still match
            else -> shiftStyles(highlighted, text.text)
        }
        return TransformedText(styled, OffsetMapping.Identity)
    }
}
