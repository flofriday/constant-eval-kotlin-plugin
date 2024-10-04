package com.flofriday.constantEvalKotlinPlugin.CaptureIrPlugin

import org.jetbrains.kotlin.backend.common.extensions.IrGenerationExtension
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.declarations.IrModuleFragment


/**
 * This sure is a strange plugin, I guess I am doing something wrong here because this shouldn't be that complicated.
 *
 * Basically what I want to do is to return the IR dump of the main function.
 * The suggested `kotlin-compile-testing` plugin doesn't give me any access to the IR and just working with the compiled
 * bytecode is somewhat cumbersome.
 *
 * So the only way I currently know how to do this is to write a custom plugin that does that.
 * I also couldn't quite return the string since the tests only supply a registrar and that only registers a plugin that
 * will be run at some later point. So instead I pass a lambda to the extension.
 *
 * I hope I will figure out a better way at some point or that someone reads this code and helps me out.
 */
class CaptureIrPluginExtension (
  private val callback: (String) -> Unit
) : IrGenerationExtension {



  override fun generate(moduleFragment: IrModuleFragment, pluginContext: IrPluginContext) {
    val visitor = CaptureIrPluginVisitor()
    moduleFragment.accept(visitor, null)
    callback(visitor.getIrDump())
  }
}
