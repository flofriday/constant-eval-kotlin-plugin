@file:OptIn(ExperimentalCompilerApi::class)

package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/**
 * These tests are rather simple and only ever inline constants.
 * There purpose is to check whether the most basic features work.
 */
class InlineTest {
  @Test
  fun `Inline single int constant`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalThree())
          }
          
          fun evalThree() = 3
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

  // This is a super basic test because I once had a bug in my testing setup where all compiled results were emtpy.
  @Test
  fun `Inline single int constant incorrectly`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalThree())
          }
          
          fun evalThree() = 3
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(4)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertNotEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Inline int identity function`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalId(42))
          }
          
          fun evalId(n: Int) = n
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
  fun `Inline bool identity function`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalId(false))
          }
          
          fun evalId(n: Boolean) = n
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(false)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Inline string identity function`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalId("dance"))
          }
          
          fun evalId(n: String) = n
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println("dance")
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessful)
    assertTrue(expectedResult.wasSuccessful)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }


}
