package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.backend.konan.serialization.KonanManglerIr.signatureString
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.backend.js.utils.valueArguments
import org.jetbrains.kotlin.ir.declarations.IrVariable
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
  private val constantTypes: List<IrType>
) : IrElementVisitor<Any?, Nothing?> {

  private var environment: Environment = Environment()

  fun evaluate(call: IrCall): Any? {
    return call.accept(this, null)
  }

  override fun visitBlock(expression: IrBlock, data: Nothing?): Any? {
    // Push a new environment because we are in a new scope
    val oldEnv = environment
    val newEnv = Environment(oldEnv)
    environment = newEnv

    // FIXME: This doesn't make any sense to me
    // Even when using a `when` expression as a expression it gets wrapped in an block.
    // However, a block only contains statements which should not result in values if executed.
    // However, without returning the value from the last statement (which in that case is actually an expression)
    // I cannot work with when expressions.
    // Maybe I'm missing something huge here.

    // Evaluate the block in a new environment
    val values = expression.statements.map { statement -> statement.accept(this, null) }

    // Drop the environment
    environment = oldEnv
    return values.lastOrNull();
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

  override fun visitBreak(jump: IrBreak, data: Nothing?): Any? {
    throw BreakSignal(jump.label)
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  fun visitEvalCall(expression: IrCall, data: Nothing?): Any? {
    // Function must return a constant type
    val callee = expression.symbol.owner
    if (!constantTypes.contains(callee.returnType)) {
      throw StopEvalSignal("Return type is not supported `${callee.returnType}`")
    }

    // Construct the environment of the new function
    val newEnvironment = Environment()
    for (i in 0..<expression.valueArgumentsCount) {
      val argName = callee.valueParameters[i].name.asString()
      val arg = expression.valueArguments[i]

      // Evaluate the argument or if none provided the default argument.
      val value = if (arg != null) {
        arg.accept(this, null)
      } else {
        expression.symbol.owner.valueParameters[i].defaultValue?.accept(this, null)
          ?: throw StopEvalSignal("No argument provided and no default argument")
      }

      newEnvironment.put(argName, value)
    }

    // Store the current environment and activate the new one
    val oldEnvironment = environment
    environment = newEnvironment

    // Execute the body of the new function
    var result: Any?
    try {
      expression.symbol.owner.body!!.accept(this, null)
      throw StopEvalSignal("No return statement encountered")
    } catch (e: ReturnSignal) {
      result = e.value
    } catch (e: ContinueSignal) {
      throw StopEvalSignal("continue signal escaped function")
    } catch (e: BreakSignal) {
      throw StopEvalSignal("break signal escaped function")
    }

    // Reactivate the original environment and return
    environment = oldEnvironment
    return result
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  fun visitBuiltInCall(expression: IrCall, data: Nothing?): Any? {
    // Only functions in these builtin classes are allowed
    val allowedClasses = listOf("kotlin.Int", "kotlin.Boolean", "kotlin.String", "kotlin.internal.ir")

    val callee = expression.symbol.owner
    val funcName = callee.parent.kotlinFqName.toString() + "::" + callee.signatureString(false)
    if (callee.parent.kotlinFqName.toString() !in allowedClasses) {
      throw StopEvalSignal("Function `$funcName` is not supporeted")
    }

    // Evaluate all the arguments
    val allArgs = listOfNotNull(expression.dispatchReceiver) + expression.valueArguments
    val arguments = allArgs.map { valueArgument -> valueArgument!!.accept(this, null) }

    // Load and execute the function
    val func =
      builtinFunctionTable[funcName] ?: throw StopEvalSignal("Function `$funcName` is not found in builtinTable")

    return func(arguments)
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  override fun visitCall(expression: IrCall, data: Nothing?): Any? {
    if (expression.symbol.owner.name.asString().startsWith("eval")) {
      return visitEvalCall(expression, data)
    }
    return visitBuiltInCall(expression, data)
  }

  override fun visitConst(expression: IrConst<*>, data: Nothing?): Any? {
    if (!constantTypes.contains(expression.type)) {
      throw StopEvalSignal("Found constant of unsupported type: ${expression.type}")
    }

    return expression.value
  }

  override fun visitContinue(jump: IrContinue, data: Nothing?): Any? {
    throw ContinueSignal(jump.label)
  }

  override fun visitElement(element: IrElement, data: Nothing?): Any? {
    // This function is called on elements where we haven't overwritten the visit method yet.
    // This means we don't yet know how to deal with them.
    throw StopEvalSignal("Unknown element: ${element::class}")
  }

  override fun visitExpressionBody(body: IrExpressionBody, data: Nothing?): Any? {
    return body.expression.accept(this, null)
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

  override fun visitSetValue(expression: IrSetValue, data: Nothing?): Any? {
    val varName = expression.symbol.owner.name.toString()
    val value = expression.value.accept(this, data)
    if (!environment.has(varName)) {
      throw StopEvalSignal("Variable $varName not found")
    }

    environment.update(varName, value)
    return null
  }

  override fun visitStringConcatenation(expression: IrStringConcatenation, data: Nothing?): Any? {
    return expression.arguments.map { arg -> arg.accept(this, data).toString() }.joinToString("")
  }

  override fun visitVariable(declaration: IrVariable, data: Nothing?): Any? {
    val varName = declaration.name.asString()
    val value = declaration.initializer!!.accept(this, data)
    environment.put(varName, value)
    return null;
  }

  override fun visitWhen(expression: IrWhen, data: Nothing?): Any? {
    for (branch in expression.branches) {
      val condition = branch.condition.accept(this, data)
      if (condition as Boolean) {
        return branch.result.accept(this, data)
      }
    }

    return null
  }

  override fun visitWhileLoop(loop: IrWhileLoop, data: Nothing?): Any? {
    try {
      while (loop.condition.accept(this, null) as Boolean) {
        if (loop.body != null) {
          try {
            loop.body!!.accept(this, data)
          } catch (e: ContinueSignal) {
            if (e.label != null && e.label != loop.label) {
              // We want to continue but not in this loop but in an outer one, so let's rethrow the exception.
              throw e
            }
            // The execution of the current iteration was already stopped so do nothing here.
          }
        }
      }
    } catch (e: BreakSignal) {
      if (e.label != null && e.label != loop.label) {
        // We want to break but not only this loop but also from an outer one so, let's rethrow the exception.
        throw e
      }

      // The execution of the loop was already stopped so we don't need to do anything here.
    }
    return null;
  }

}
