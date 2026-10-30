package io.ma7moud3ly.nemo.syntax.tokenizer

import kotlin.test.Test

class KotlinTokenizerTest {

    @Test
    fun emptyBacktickNameDoesNotThrow() {
        listOf("`", "``", "a = `", "```kotlin\nprint('Hello')\n}\n```").forEach {
            KotlinTokenizer().tokenize(it)
        }
    }
}
