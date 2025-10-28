package io.ma7moud3ly.nemo.lsp.completion

import io.ma7moud3ly.nemo.model.Language

object CompletionProviderFactory {
    fun getProvider(language: Language): CompletionProvider? {
        return when (language) {
            Language.KOTLIN -> KotlinCompletionProvider()
            Language.PYTHON -> PythonCompletionProvider()
            else -> null
        }
    }
}

