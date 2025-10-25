package com.ma7moud3ly.nemo.model

data class EditorTheme(
    val name: String,
    val dark: Boolean,
    val background: Long,
    val foreground: Long,
    val currentLineBackground: Long,
    val selection: Long,
    val lineNumber: Long,
    val lineNumberActive: Long,
    val gutter: Long,
    val syntax: SyntaxColors
)