package com.ma7moud3ly.nemo.syntax.formatter

import com.ma7moud3ly.nemo.model.Language

object FormatterFactory {
    fun getFormatter(language: Language): CodeFormatter? {
        return when (language) {
            Language.KOTLIN -> KotlinFormatter()
            Language.PYTHON -> PythonFormatter()
            else -> null
        }
    }
}