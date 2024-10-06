package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.SourceFile
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals


/**
 * These tests verify that the operators work as advertised.
 */
class ControllFlowTest {
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

}
