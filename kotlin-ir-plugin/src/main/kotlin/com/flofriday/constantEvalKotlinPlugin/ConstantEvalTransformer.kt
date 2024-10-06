package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
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

    // Function must return a constant type
    val constantTypes = listOf(
      pluginContext.irBuiltIns.intType,
      pluginContext.irBuiltIns.booleanType,
      pluginContext.irBuiltIns.stringType
    )
    if (!constantTypes.contains(callee.returnType)) {
      println("StopEval: return type is not supported ${callee.returnType}")
      return super.visitCall(expression)
    }

    // All arguments must be of constant type
    val environment = Environment()
    for (i in 0..<expression.valueArgumentsCount) {
      val arg = expression.valueArguments[i]
      var argName = callee.valueParameters[i].name.asString()

      if (arg !is IrConst<*>) {
        println("StopEval: argument `$argName` is not a constant")
        return super.visitCall(expression)
      }

      if (!constantTypes.contains(arg.type)) {
        println("StopEval: argument `$argName` is a unsupported type ${arg.type}")
        return super.visitCall(expression)
      }

      if (arg.value == null) {
        throw NotImplementedError("Honestly, I don't know what to do here...")
      }

      environment.put(argName, arg.value!!)
    }

    try {
      // FIXME: Call the evaluator here 🪄
      //Int::class.members.single
      val body = expression.symbol.owner.body!!
      val evaluator = Evaluator(environment, body, constantTypes)
      val result = evaluator.evaluate()
      return toConstant(result)
    } catch (e: StopEvalSignal) {
      // We cannot evaluate the function for some reason.
      println("StopEvalSignal: '${e.message}'")
      return super.visitCall(expression)
    }
  }

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

