@file:OptIn(ExperimentalCompilerApi::class)

package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.JvmCompilationResult
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import kotlin.test.assertEquals
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Assert
import org.junit.Test
import kotlin.math.exp

class IrPluginTest {

  @Test
  fun `Single constant inlining`() {
    val result = compileWithPlugin(
      sourceFile = SourceFile.kotlin(
        "main.kt", """
fun main() {
  println(evalThree())
}

fun evalThree() = 3
"""
      )
    )

    val expectedResult = compileWithOutPlugin(
      sourceFile = SourceFile.kotlin(
        "main.kt", """
fun main() {
  println(evalThree())
}

fun evalThree() = 3
"""
      )
    )

    assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    assertEquals(KotlinCompilation.ExitCode.OK, expectedResult.exitCode)
    Assert.assertArrayEquals(getByteCode(expectedResult), getByteCode(result))
  }
}


fun getByteCode(result: JvmCompilationResult): ByteArray {
  return result.classLoader.getResourceAsStream("MainKt.class")?.readAllBytes()!!
}

fun compileWithOutPlugin(
  sourceFile: SourceFile,
): JvmCompilationResult {
  return compile(sourceFile, listOf())
}

fun compileWithPlugin(
  sourceFile: SourceFile,
): JvmCompilationResult {
  return compile(sourceFile, listOf(ConstantEvalCompilerRegistrar()))
}

fun compile(
  sourceFile: SourceFile,
  plugins: List<CompilerPluginRegistrar>,
): JvmCompilationResult {
  return KotlinCompilation().apply {
    sources = listOf(sourceFile)
    compilerPluginRegistrars = plugins
    inheritClassPath = true
  }.compile()
}

