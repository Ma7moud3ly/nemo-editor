package com.ma7moud3ly.nemo.syntax.analysis

import com.ma7moud3ly.nemo.model.Language

object ErrorDetectorFactory {
    fun getErrorDetector(language: Language): ErrorDetector? {
        return when (language) {
            Language.KOTLIN -> KotlinErrorDetector()
            Language.PYTHON -> PythonErrorDetector()
            else -> null
        }
    }
}