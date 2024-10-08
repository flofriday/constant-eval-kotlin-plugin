package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.SourceFile
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals


/**
 * These tests verify that all control flow constructs like if/when and loops work as expected.
 */
class ControlFlowTest {
  @Test
  fun `If with condition true hit's body`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalUniverse())
          }
          
          fun evalUniverse(): Int {
            if (true) {
                return 42
            } 
            return 13
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(42)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `If with condition false doesn't hit body`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalUniverse())
          }
          
          fun evalUniverse(): Int {
            if (false) {
                return 13
            } 
            return 42
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(42)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `If else with condition true hit's then body`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalUniverse())
          }
          
          fun evalUniverse(): Int {
            if (true) {
                return 42
            } else {
                return 13
            }
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(42)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `If else with condition else hit's else body`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalUniverse())
          }
          
          fun evalUniverse(): Int {
            if (false) {
                return 13
            } else {
                return 42
            }
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(42)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `If else as expression`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalMargin(true))
            println(evalMargin(false))
          }
          
          fun evalMargin(isMobile: Boolean): Int {
            return if (isMobile) 12 else 24
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(12)
            println(24)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `When expression`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          // Insipred by the official kotlin example:
          // https://kotlinlang.org/docs/control-flow.html#when-expression
          fun main() {
            println(evalDescribeInt(1))
            println(evalDescribeInt(2))
            println(evalDescribeInt(3))
          }
          
          fun evalDescribeInt(x: Int): String {
            return when (x) {
                1 -> "x == 1"
                2 -> "x == 2"
                else -> {
                    "x is neither 1 nor 2"
                }
            }
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println("x == 1")
            println("x == 2")
            println("x is neither 1 nor 2")
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `While loop with condition true hit's body`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalUniverse())
          }
          
          fun evalUniverse(): Int {
            while (true) {
                return 42
            } 
            return 13
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(42)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `While loop with condition false doesn't hit body`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalUniverse())
          }
          
          fun evalUniverse(): Int {
            while (false) {
                return 13
            } 
            return 42
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(42)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `While loop to multiply`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalMultiply(3, 7))
          }
          
          // Multiply by adding in a loop
          fun evalMultiply(a: Int, b: Int): Int {
            var index = 0
            var result = 0
            while(index < b)  {
              result += a
              index += 1
            }
            return result
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(21)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `While loop hit's break`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalOne())
          }
          
          fun evalOne(): Int {
            var n = 1
            while (true) {
                break
                n = 13
            }
            return n
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(1)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Nested while loop hit's inner break`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalOne())
          }
          
          fun evalOne(): Int {
            var n = 666
            while (true) {
              while (true) {
                  break
                  n = 13
              }
              n = 1
              break
            }
            return n
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(1)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Nested while loop hit's outer labeled break`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalOne())
          }
          
          fun evalOne(): Int {
            var n = 1
            outer@ while (true) {
              inner@ while (true) {
                  break@outer
                  n = 13
              }
              n = 43
            }
            return n
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(1)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `While loop hit's continue`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalFive())
          }
          
          fun evalFive(): Int {
            var i = 0
            var n = 0
            while (i < 10) {
                if (i < 5) {
                    i += 1
                    continue
                }
                n += 1
                i += 1
            }
            return n
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(5)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `While loop hit's inner continue`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalNum())
          }
          
          fun evalNum(): Int {
            var i = 0
            var n = 0
            while (i < 10) {
                var j = 0
                while (j < 10) {
                  if (j < 8) {
                      j += 1
                      continue
                  }
                  n += 1
                  j += 1
                }
              n += 100
              i += 1 
            }
            return n
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(1020)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `While loop hit's outer labeled continue`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalNum())
          }
          
          fun evalNum(): Int {
            var i = 0
            var n = 0
            outer@ while (i < 10) {
                var j = 0
                while (j < 10) {
                  if (j >= 8) {
                      n += 1
                      j += 1
                      i += 1 
                      continue@outer
                  }
                  j += 1
                }
              n += 100
              i += 1 
            }
            return n
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(10)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }
}
