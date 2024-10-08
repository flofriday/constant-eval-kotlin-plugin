package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment

class ConstantEvalGenerationExtension(
) : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    moduleFragment.transform(Transformer(pluginContext), null)
    // println("=== AFTER ===")
    // println(moduleFragment.dump())
  }
}
