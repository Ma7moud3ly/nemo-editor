package io.ma7moud3ly.nemo.syntax.formatter

import kotlin.test.Test
import kotlin.test.assertEquals

class PythonFormatterTest {

    private val formatter = PythonFormatter()

    private fun format(code: String) = formatter.format(code.trimIndent())

    private fun expected(code: String) = code.trimIndent() + "\n"

    @Test
    fun bracketsAndKeywordArguments() {
        val formatted = expected(
            """
            # A 2D list representing a 3x3 grid
            matrix = [
                [1, 2, 3],
                [4, 5, 6],
                [7, 8, 9]
            ]

            print("Iterating through a matrix:")
            # Outer loop iterates through each row
            for row in matrix:
                # Inner loop iterates through each item in the current row
                for item in row:
                    print(item, end=" ")
                print()  # New line after each row
            """
        )
        assertEquals(formatted, formatter.format(formatted))

        val misformatted = """
            # A 2D list representing a 3x3 grid
            matrix = [
            [1, 2, 3],
            [4, 5, 6],
            [7, 8, 9]
            ]

            print("Iterating through a matrix:")
            # Outer loop iterates through each row
            for row in matrix:
                # Inner loop iterates through each item in the current row
                for item in row:
                    print(item, end = " ")
                print() # New line after each row
            """
        assertEquals(formatted, format(misformatted))
    }

    @Test
    fun continuationLinesLineUpAfterTheBracket() {
        val code = """
            i2c = I2C(0, scl=Pin(22),
            sda=Pin(21))
            """
        val formatted = """
            i2c = I2C(0, scl=Pin(22),
                      sda=Pin(21))
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun operatorsAndParameters() {
        assertEquals("x = 1 + 2 * -3\n", formatter.format("x=1+2*-3"))
        assertEquals("y = a[1:-1]\n", formatter.format("y=a[1:-1]"))
        assertEquals("n = 1e-5\n", formatter.format("n=1e-5"))
        assertEquals("f = lambda x=1: x\n", formatter.format("f=lambda x = 1:x"))
        assertEquals(
            "def f(a, b=2, *args, c: int = 3, **kw) -> int:\n    pass\n",
            formatter.format("def f(a,b = 2,*args,c:int=3,**kw)->int:\n    pass")
        )
        assertEquals("d = {'a': 1, **e}\n", formatter.format("d={'a':1,**e}"))
        assertEquals("print(a, b)\n", formatter.format("print (a,b)"))
    }

    @Test
    fun stringsAndCommentsAreKept() {
        assertEquals("s = \"a=b,c\"  # note\n", formatter.format("s=\"a=b,c\" #note"))
        assertEquals("#!/usr/bin/env python\n", formatter.format("#!/usr/bin/env python"))
    }

    @Test
    fun multiLineStringsAreKept() {
        val code = """
            def f():
                ""${'"'}
                  keep   this
                ""${'"'}
                return 1
            """
        assertEquals(expected(code), format(code))
    }

    @Test
    fun importsInsideBlocksStayInPlace() {
        val code = """
            try:
                import ujson as json
            except ImportError:
                import json
            """
        assertEquals(expected(code), format(code))
    }

    @Test
    fun topLevelImportsAreSorted() {
        val code = """
            import time
            from machine import Pin
            import network
            """
        val formatted = """
            from machine import Pin
            import network
            import time
            """
        assertEquals(expected(formatted), format(code))
    }

    @Test
    fun atMostTwoBlankLines() {
        assertEquals("a = 1\n\n\nb = 2\n", formatter.format("a = 1\n\n\n\n\nb = 2\n\n"))
    }
}
