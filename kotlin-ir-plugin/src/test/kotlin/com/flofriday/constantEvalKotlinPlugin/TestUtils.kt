package com.flofriday.constantEvalKotlinPlugin

import com.flofriday.constantEvalKotlinPlugin.CaptureIrPlugin.CaptureIrPluginRegistrar
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi

class CompilationResult(
  val wasSuccessful: Boolean,
  val mainIrDump: String,
)

@OptIn(ExperimentalCompilerApi::class)
fun compileWithOutEval(
  sourceFile: SourceFile,
): CompilationResult {
  return compile(sourceFile, listOf())
}

@OptIn(ExperimentalCompilerApi::class)
fun compileWithEval(
  sourceFile: SourceFile,
): CompilationResult {
  return compile(sourceFile, listOf(ConstantEvalCompilerRegistrar()))
}

@OptIn(ExperimentalCompilerApi::class)
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
