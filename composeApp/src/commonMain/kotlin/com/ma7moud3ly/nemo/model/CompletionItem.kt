package com.ma7moud3ly.nemo.model

import com.ma7moud3ly.nemo.lsp.completion.CompletionKind

data class CompletionItem(
    val label: String,
    val detail: String? = null,
    val documentation: String? = null,
    val insertText: String = label,
    val kind: CompletionKind = CompletionKind.TEXT
)