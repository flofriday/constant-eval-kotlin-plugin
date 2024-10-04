@file:OptIn(ExperimentalCompilerApi::class)

package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class IrPluginTest {

  @Test
  fun `Single constant inlining`() {
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }


  @Test
  fun `Single constant inlining incorrect`() {
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertNotEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Inline Id function`() {
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

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }


}
