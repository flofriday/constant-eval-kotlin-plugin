package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment
import org.jetbrains.kotlin.ir.util.dump

class ConstantEvalGenerationExtension(
  private val messageCollector: MessageCollector,
  private val string: String,
  private val file: String
) : IrGenerationExtension {
  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    messageCollector.report(CompilerMessageSeverity.INFO, "Argument 'string' = $string")
    messageCollector.report(CompilerMessageSeverity.INFO, "Argument 'file' = $file")

    // println(moduleFragment.dump())
    // Thought: can I even do it with a single transform or do I need a visitor first to collect the function bodies and
    // then in a second iteration transform it?
    moduleFragment.transform(ConstantEvalTransformer(pluginContext), null)
    // FIXME: Remove debug log in the future
    println("=== AFTER ===")
    println(moduleFragment.dump())
  }
}
