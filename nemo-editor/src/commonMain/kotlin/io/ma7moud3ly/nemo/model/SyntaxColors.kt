package io.ma7moud3ly.nemo.model

data class SyntaxColors(
    val keyword: Long,
    val string: Long,
    val comment: Long,
    val number: Long,
    val function: Long,
    val type: Long,
    val variable: Long,
    val operator: Long
)