@file:OptIn(ExperimentalCompilerApi::class)

package com.flofriday.constantEvalKotlinPlugin

import com.google.auto.service.AutoService
import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.compiler.plugin.CompilerPluginRegistrar
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.jetbrains.kotlin.config.CompilerConfiguration

@AutoService(CompilerPluginRegistrar::class)
class ConstantEvalCompilerRegistrar(
) : CompilerPluginRegistrar() {
  override val supportsK2 = true


  @OptIn(ExperimentalCompilerApi::class)
  override fun ExtensionStorage.registerExtensions(configuration: CompilerConfiguration) {
    IrGenerationExtension.registerExtension(ConstantEvalGenerationExtension())
  }
}
