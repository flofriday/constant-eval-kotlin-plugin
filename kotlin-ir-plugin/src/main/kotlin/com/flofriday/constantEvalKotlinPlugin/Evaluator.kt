package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.konan.serialization.KonanManglerIr.signatureString
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.util.kotlinFqName
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

/**
 * The evaluator traverses a tree and returns the value to which the function body evaluates to.
 *
 * However, it obviously only supports a limited subset of all possible kotlin functions. In case it cannot evaluate a
 * function a `StopEvalSignal` is thrown with a message to explain why it was stopped.
 */
class Evaluator(
  private var environment: Environment,
  private val body: IrBody,
  private val constantTypes: List<IrType>
) : IrElementVisitor<Any?, Nothing?> {

  fun evaluate(): Any? {
    try {
      body.accept(this, null)
    } catch (e: ReturnSignal) {
      return e.value
    }

    throw StopEvalSignal("No return statement encountered")
  }

  override fun visitBlockBody(body: IrBlockBody, data: Nothing?): Any? {
    // Push a new environment because we are in a new scope
    val oldEnv = environment
    val newEnv = Environment(oldEnv)
    environment = newEnv

    // Evaluate the block in a new environment
    body.statements.forEach { statement -> statement.accept(this, null) }

    // Drop the environment
    environment = oldEnv
    return null;
  }

  override fun visitCall(expression: IrCall, data: Nothing?): Any? {
    // Only functions in these builtin classes are allowed
    val allowedClasses = listOf("kotlin.Int", "kotlin.Boolean", "kotlin.String")

    val callee = expression.symbol.owner
    val funcName = callee.parent.kotlinFqName.toString() + "::" + callee.signatureString(false)
    if (callee.parent.kotlinFqName.toString() !in allowedClasses) {
      throw StopEvalSignal("Function `funcName` is not supporeted")
    }

    // Evaluate all the arguments
    val allArgs = listOfNotNull(expression.dispatchReceiver) + expression.valueArguments
    val arguments = allArgs.map { valueArgument -> valueArgument!!.accept(this, null) }

    // Load and execute the function
    // I wish I could have used reflection here but I couldn't get it to work without throwing an exception that the
    // caller couldn't be resolved.
    val func =
      builtinFunctionTable[funcName] ?: throw StopEvalSignal("Function `$funcName` is not found in builtinTable")

    return func(arguments)
  }

  override fun visitConst(expression: IrConst<*>, data: Nothing?): Any? {
    if (!constantTypes.contains(expression.type)) {
      throw StopEvalSignal("Found constant of unsupported type: ${expression.type}")
    }

    return expression.value
  }

  override fun visitElement(element: IrElement, data: Nothing?): Any? {
    // This function is only called on elements we don't know yet
    throw StopEvalSignal("Unknown element: ${element::class}")
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  override fun visitGetValue(expression: IrGetValue, data: Nothing?): Any? {
    val varName = expression.symbol.owner.name.toString()

    if (!environment.has(varName)) {
      throw StopEvalSignal("Variable $varName not found")
    }

    return environment.get(varName)
  }

  override fun visitReturn(expression: IrReturn, data: Nothing?): Any? {
    val value = expression.value.accept(this, data)
    throw ReturnSignal(value)
  }


}
