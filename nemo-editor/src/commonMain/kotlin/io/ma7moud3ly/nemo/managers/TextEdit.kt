package io.ma7moud3ly.nemo.managers

/**
 * The single contiguous change between two revisions of a text.
 *
 * @param start index where the change begins, in both revisions.
 * @param removed text present in the old revision and gone from the new one.
 * @param inserted text present in the new revision and absent from the old one.
 */
internal data class TextEdit(
    val start: Int,
    val removed: String,
    val inserted: String
) {
    /** Index just past the inserted text, in the new revision. */
    val insertionEnd: Int get() = start + inserted.length
}

/**
 * Derives what changed between [old] and [new] by comparing the texts, and
 * returns `null` when they are identical.
 *
 * Editors normally read the edit off the reported caret position, but platforms
 * disagree about when that position is updated: some input methods and the web
 * backend deliver the new text with the caret still on its previous offset. A
 * comparison of the two strings depends on nothing but the strings, so it
 * behaves the same on every target.
 */
internal fun diffText(old: String, new: String): TextEdit? {
    if (old == new) return null

    val limit = minOf(old.length, new.length)

    var prefix = 0
    while (prefix < limit && old[prefix] == new[prefix]) prefix++

    var suffix = 0
    while (suffix < limit - prefix &&
        old[old.length - 1 - suffix] == new[new.length - 1 - suffix]
    ) suffix++

    var start = prefix
    var removed = old.substring(prefix, old.length - suffix)
    var inserted = new.substring(prefix, new.length - suffix)

    // Where the change sits can be ambiguous when the text around it repeats
    // the text that changed. Pressing Enter at the end of a line that has more
    // lines below it turns "a\n" into "a\n\n": matching the common prefix
    // greedily blames the *existing* newline and puts the edit one line too
    // late, which leaves the caller looking at the wrong previous line. Slide
    // the edit to the earliest position that produces the same result, which is
    // the one the caret was actually at.
    if (removed.isEmpty() && inserted.isNotEmpty()) {
        val length = inserted.length
        while (start > 0 && new[start - 1] == new[start + length - 1]) start--
        inserted = new.substring(start, start + length)
    } else if (inserted.isEmpty() && removed.isNotEmpty()) {
        val length = removed.length
        while (start > 0 && old[start - 1] == old[start + length - 1]) start--
        removed = old.substring(start, start + length)
    }

    return TextEdit(start = start, removed = removed, inserted = inserted)
}
