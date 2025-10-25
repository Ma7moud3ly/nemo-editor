package com.ma7moud3ly.nemo.model

data class SyntaxError(
    val start: Int,
    val end: Int,
    val message: String
)