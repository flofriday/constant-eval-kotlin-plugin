package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.toIrConst

class ConstantEvalTransformer(
  private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  override fun visitCall(expression: IrCall): IrExpression {

    // Only eval functions with the "eval" prefix
    val functionName = expression.symbol.owner.name.asString()
    if (!functionName.startsWith("eval")) {
      return super.visitCall(expression)
    }

    if (expression.valueArgumentsCount > 0) {
      throw NotImplementedError("CANNOT EVAL FUNCTION WITH ARGUMENTS")
    }

    println("constant eval: ${expression.dump()}")
    val evaluated = 3
    return evaluated.toIrConst(pluginContext.irBuiltIns.intType)
  }

}

