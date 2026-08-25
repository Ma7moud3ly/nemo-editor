package io.ma7moud3ly.nemo.syntax.tokenizer

import io.ma7moud3ly.nemo.model.Language

object TokenizerFactory {
    fun getTokenizer(language: Language): LanguageTokenizer {
        return when (language) {
            Language.KOTLIN -> KotlinTokenizer()
            Language.PYTHON, Language.MICRO_PYTHON -> PythonTokenizer()
            else -> KotlinTokenizer()
        }
    }
}