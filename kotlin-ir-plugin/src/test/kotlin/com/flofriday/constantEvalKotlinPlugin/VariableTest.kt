package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.SourceFile
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals


/**
 * These tests verify that variables work and are correctly scoped.
 */
class VariableTest {
  @Test
  fun `Declare a variable`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalThree())
          }
          
          fun evalThree(): Int {
            val a = 3
            return a
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(3)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Update a variable`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalThree())
          }
          
          fun evalThree(): Int {
            var a = 2
            a = a + 1
            return a
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(3)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Variable in inner scope doesn't affect outer scope num2`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalThree())
          }
          
          fun evalThree(): Int {
            val a = 3
            if (true) {
                val a = 42
            }
            return a
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(3)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Variable in inner scope doesn't affect outer scope with loops`() {
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
                if(true) {
                    var n = 13
                    break
                }
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
  fun `Variable in outer scope doesn't affect inner scope`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalThree())
          }
          
          fun evalThree(): Int {
            val a = 47
            if (true) {
                val a = 3
                return a
            }
            return a
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(3)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }


  @Test
  fun `Variable in outer scope can be updated from inner scope`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalThree())
          }
          
          fun evalThree(): Int {
            var a = 47
            if (true) {
                a = 3
            }
            return a
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(3)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

}
