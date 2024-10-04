package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.common.IrElementTransformerVoidWithContext
import org.jetbrains.kotlin.backend.common.extensions.IrPluginContext
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrExpression
import org.jetbrains.kotlin.ir.expressions.impl.IrConstImpl
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.util.dump
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
    val constantTypes = listOf(pluginContext.irBuiltIns.intType)
    if (! constantTypes.contains(callee.returnType)) {
      return super.visitCall(expression)
    }

    // All arguments must be of constant type
    val arguments = mutableListOf<Any>()
    for (arg in expression.valueArguments) {
      if (arg !is IrConst<*>) {
        return super.visitCall(expression)
      }

      if (! constantTypes.contains(arg.type)) {
        return super.visitCall(expression)
      }

      if (arg.value == null) {
        throw NotImplementedError("Honestly, I don't know what to do here...")
      }

      arguments.add(arg.value!!)
    }

    // callee.valueParameters.get(0).name
    println(callee.dump())

    // FIXME: Call the evaluator here 🪄

    println("eval body: ${expression.symbol.owner.body!!.dump()}")

    println("constant eval: ${expression.dump()}")
    val evaluated = 3
    return evaluated.toIrConst(pluginContext.irBuiltIns.intType)
  }


}

