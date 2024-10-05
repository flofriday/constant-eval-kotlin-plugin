package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.util.toIrConst
import kotlin.math.exp


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

    // Function must return a constant type
    val constantTypes = listOf(pluginContext.irBuiltIns.intType)
    if (! constantTypes.contains(callee.returnType)) {
      return super.visitCall(expression)
    }

    // All arguments must be of constant type
    val arguments = mutableMapOf<String, Any?>()
    for (i in 0..<expression.valueArgumentsCount) {
      val arg = expression.valueArguments[i]
      if (arg !is IrConst<*>) {
        return super.visitCall(expression)
      }

      if (! constantTypes.contains(arg.type)) {
        return super.visitCall(expression)
      }

      if (arg.value == null) {
        throw NotImplementedError("Honestly, I don't know what to do here...")
      }

      arguments[callee.valueParameters[i].name.asString()] = arg.value!!
    }

    try {
      // FIXME: Call the evaluator here 🪄
      //Int::class.members.single
      val body = expression.symbol.owner.body!!
      val evaluator = Evaluator(arguments, body, constantTypes)
      val result = evaluator.evaluate()
      return toConstant(result)
    } catch (e: StopEvalSignal) {
      // We cannot evaluate the function for some reason.
      println("StopEvalSignal: '${e.message}'")
      return super.visitCall(expression)
    }
  }

  private fun toConstant(value: Any?): IrConst<*> {

    return when(value) {
      is Int -> value.toIrConst(pluginContext.irBuiltIns.intType)
      else -> {
        val typeName = when (value) {
          null -> "null"
          else -> value::class.simpleName!!
        }
        throw StopEvalSignal("Unsupported Type $typeName")
      }
    }
  }

}

