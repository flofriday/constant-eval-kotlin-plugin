package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.toIrConst


class ConstantEvalTransformer(
  private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  override fun visitCall(expression: IrCall): IrExpression {

    // Only eval functions with the "eval" prefix
    val callee = expression.symbol.owner
    val functionName = callee.name.asString()
    if (!functionName.startsWith("eval")) {
      return super.visitCall(expression)
    }

    val constantTypes = listOf(
      pluginContext.irBuiltIns.intType,
      pluginContext.irBuiltIns.booleanType,
      pluginContext.irBuiltIns.stringType
    )

    // Call the evaluator
    try {
      val evaluator = Evaluator(constantTypes)
      val result = evaluator.evaluate(expression)
      return toConstant(result)
    } catch (e: StopEvalSignal) {
      // We cannot evaluate the function for some reason.
      println("StopEvalSignal: '${e.message}'")
      return super.visitCall(expression)
    }
  }

  /**
   * Converts any of the values we support to a constant.
   */
  private fun toConstant(value: Any?): IrConst<*> {
    return when (value) {
      is Boolean -> value.toIrConst(pluginContext.irBuiltIns.booleanType)
      is Int -> value.toIrConst(pluginContext.irBuiltIns.intType)
      is String -> value.toIrConst(pluginContext.irBuiltIns.stringType)
      else -> {
        val typeName = when (value) {
          null -> "null"
          else -> value::class.simpleName!!
        }
        throw StopEvalSignal("Unsupported Type `$typeName`")
      }
    }
  }

}

