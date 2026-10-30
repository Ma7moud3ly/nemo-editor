package io.ma7moud3ly.nemo.lsp.completion

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class KotlinCompletionProviderTest {

    private val provider = KotlinCompletionProvider()

    private suspend fun labels(text: String) =
        provider.provideCompletions(text, text.length).map { it.label }

    @Test
    fun annotationsAfterAt() = runTest {
        val labels = labels("@Comp")
        assertTrue("@Composable" in labels)
    }

    @Test
    fun composables() = runTest {
        val labels = labels("Col")
        assertTrue("Column" in labels)
    }

    @Test
    fun modifierMembers() = runTest {
        assertTrue("padding" in labels("Modifier.pad"))
        assertTrue("fillMaxSize" in labels("Modifier.padding(8.dp)\n    .fill"))
    }

    @Test
    fun receiverMembers() = runTest {
        assertTrue("Red" in labels("Color.R"))
        assertTrue("primary" in labels("MaterialTheme.colorScheme.pri"))
        assertTrue("dp" in labels("16."))
        assertTrue("Add" in labels("Icons.Default.Ad"))
    }

    @Test
    fun scopeMembers() = runTest {
        val labels = labels("LazyColumn {\n    ite")
        assertTrue("items" in labels)
    }

    @Test
    fun parametersOfTheCallComeFirst() = runTest {
        val items = provider.provideCompletions("Column(mo", "Column(mo".length)
        assertEquals("modifier", items.first().label)
        assertEquals("modifier = ", items.first().insertText)
    }

    @Test
    fun namedParametersAreNotSuggestedAgain() = runTest {
        val labels = labels("Column(modifier = Modifier, \n    ver")
        assertTrue("verticalArrangement" in labels)
        assertFalse("modifier" in labels("Column(modifier = Modifier, mo"))
    }

    @Test
    fun parametersOfMemberCalls() = runTest {
        assertTrue("horizontal" in labels("Modifier.padding(ho"))
    }

    @Test
    fun noParametersInsideALambda() = runTest {
        assertFalse("modifier" in labels("Column {\n    mo"))
    }

    @Test
    fun kotlinKeywordsStillSuggested() = runTest {
        val labels = labels("ret")
        assertTrue("return" in labels)
    }
}
