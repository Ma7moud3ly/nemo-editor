package io.ma7moud3ly.nemo.lsp.completion

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MicroPythonCompletionProviderTest {

    private val provider = MicroPythonCompletionProvider()

    private suspend fun items(text: String) = provider.provideCompletions(text, text.length)

    private suspend fun labels(text: String) = items(text).map { it.label }

    @Test
    fun constructorParametersComeFirst() = runTest {
        val first = items("Pin(2, mo").first()
        assertEquals("mode", first.label)
        assertEquals("mode=", first.insertText)
    }

    @Test
    fun namedParametersAreNotSuggestedAgain() = runTest {
        val labels = labels("I2C(0, scl=Pin(22), s")
        assertTrue("sda" in labels)
        assertFalse("scl" in labels)
    }

    @Test
    fun methodParametersOfAnInferredVariable() = runTest {
        assertTrue("trigger" in labels("led = machine.Pin(2)\nled.irq(tr"))
        assertTrue("headers" in labels("import urequests\nurequests.get(url, he"))
    }

    @Test
    fun parametersOfAnImportedFunction() = runTest {
        assertTrue("ms" in labels("from time import sleep_ms\nsleep_ms(m"))
    }

    @Test
    fun membersOfACallResult() = runTest {
        assertTrue("value" in labels("Pin(2, Pin.OUT).va"))
        assertTrue("json" in labels("requests.get(url).js"))
    }

    @Test
    fun membersOfAVariableAssignedFromACall() = runTest {
        assertTrue("json" in labels("r = requests.get(url)\nr.js"))
        assertTrue("recv" in labels("s = socket.socket()\ns.re"))
        assertTrue("publish" in labels("from umqtt.simple import MQTTClient\nc = MQTTClient('id', 'host')\nc.pu"))
    }

    @Test
    fun snippetsAreIndentedToTheLine() = runTest {
        val snippet = items("def f():\n    le").first { it.label == "led" }
        assertEquals("led = Pin(2, Pin.OUT)\n    led.value(1)", snippet.insertText)
    }

    @Test
    fun snippetsAndKeywordsWithTheSameName() = runTest {
        val kinds = items("if").filter { it.label == "if" }.map { it.kind }
        assertTrue(CompletionKind.KEYWORD in kinds)
        assertTrue(CompletionKind.SNIPPET in kinds)
    }

    @Test
    fun onlyModulesOnImportLines() = runTest {
        assertEquals(listOf("machine"), labels("import mach"))
    }
}
