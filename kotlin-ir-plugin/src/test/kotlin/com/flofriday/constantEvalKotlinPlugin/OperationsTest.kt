@file:OptIn(ExperimentalCompilerApi::class)

package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

/**
 * These tests are rather simple and only ever inline constants.
 * There purpose is to check whether the most basic features work.
 */
class OperationsTest {
  @Test
  fun `Add operator on ints`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalAdd(1, 2))
          }
          
          fun evalAdd(a: Int, b: Int) = a + b
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }
}
