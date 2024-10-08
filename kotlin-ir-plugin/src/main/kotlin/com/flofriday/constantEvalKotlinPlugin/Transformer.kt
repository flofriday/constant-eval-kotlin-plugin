package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.toIrConst


class Transformer(
  private val pluginContext: IrPluginContext,
) : IrElementTransformerVoidWithContext() {

  override fun visitFunctionNew(declaration: IrFunction): IrStatement {
    // Don't rewrite "eval" functions
    if (declaration.name.asString().startsWith("eval")) {
      return declaration
    }

    return super.visitFunctionNew(declaration)
  }

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
      pluginContext.irBuiltIns.stringType,
    )

    // Call the evaluator
    try {
      val evaluator = Evaluator(constantTypes)
      val result = evaluator.evaluate(expression)
      return toConstant(result)
    } catch (e: StopEvalSignal) {
      // We cannot evaluate the function for some reason.
      // This isn't really an issue though
      println("StopEvalSignal: '${e.message}'")
    } catch (e: Exception) {
      // In case we made a really bad programming mistake catch everything and leave the call as it was.
      println("Error during evaluation: ${e.stackTraceToString()}")
    }
    return super.visitCall(expression)
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

