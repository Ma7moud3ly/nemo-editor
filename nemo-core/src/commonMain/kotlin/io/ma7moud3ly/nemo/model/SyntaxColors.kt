package io.ma7moud3ly.nemo.model

/**
 * Colors of the syntax tokens.
 *
 * @param docComment color of documentation comments, such as KDoc. It is the
 *   same as [comment] unless set.
 * @param docTag color of tags and links inside a documentation comment, such
 *   as `@param` and `[name]`. It is the same as [keyword] unless set.
 */
data class SyntaxColors(
    val keyword: Long,
    val string: Long,
    val comment: Long,
    val number: Long,
    val function: Long,
    val type: Long,
    val variable: Long,
    val operator: Long,
    val docComment: Long = comment,
    val docTag: Long = keyword
)
