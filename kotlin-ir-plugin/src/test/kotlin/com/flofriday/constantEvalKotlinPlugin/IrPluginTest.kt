@file:OptIn(ExperimentalCompilerApi::class)

package com.flofriday.constantEvalKotlinPlugin

import com.flofriday.constantEvalKotlinPlugin.CaptureIrPlugin.CaptureIrPluginRegistrar
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
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

  class CompilationResult(
    val wasSuccessfull: Boolean,
    val mainIrDump: String,
  )

  fun compileWithOutEval(
    sourceFile: SourceFile,
  ): CompilationResult {
    return compile(sourceFile, listOf())
  }

  fun compileWithEval(
    sourceFile: SourceFile,
  ): CompilationResult {
    return compile(sourceFile, listOf(ConstantEvalCompilerRegistrar()))
  }

  fun compile(
    sourceFile: SourceFile,
    plugins: List<CompilerPluginRegistrar>,
  ): CompilationResult {

    var dumpedIr = ""
    val capturePlugin = CaptureIrPluginRegistrar({ dumped: String -> dumpedIr = dumped })

    val jvmResult = KotlinCompilation().apply {
      sources = listOf(sourceFile)
      compilerPluginRegistrars = plugins + listOf(capturePlugin)
      inheritClassPath = true
    }.compile()

    return CompilationResult(
      jvmResult.exitCode.equals(KotlinCompilation.ExitCode.OK),
      dumpedIr,
    )
  }
}

