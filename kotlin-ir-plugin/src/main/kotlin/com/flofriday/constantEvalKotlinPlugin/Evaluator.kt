package com.flofriday.constantEvalKotlinPlugin

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.UnsafeDuringIrConstructionAPI
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class Evaluator(
  private val variables: MutableMap<String, Any?>,
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

  override fun visitConst(expression: IrConst<*>, data: Nothing?): Any? {
    if (!constantTypes.contains(expression.type)) {
      throw StopEvalSignal("Found constant of unsupported type: ${expression.type}")
    }

    return expression.value
  }

  override fun visitBlockBody(body: IrBlockBody, data: Nothing?): Any? {
    body.statements.forEach { statement -> statement.accept(this, null) }
    return null;
  }

  override fun visitElement(element: IrElement, data: Nothing?): Any? {
    // This function is only called on elements we don't know yet
    throw StopEvalSignal("Unknown element: ${element::class}")
  }

  override fun visitReturn(expression: IrReturn, data: Nothing?): Any? {
    val value = expression.value.accept(this, data)
    throw ReturnSignal(value)
  }

  @OptIn(UnsafeDuringIrConstructionAPI::class)
  override fun visitGetValue(expression: IrGetValue, data: Nothing?): Any? {
    val varName = expression.symbol.owner.name.toString()

    if(!variables.containsKey(varName)) {
      throw StopEvalSignal("Variable $varName not found")
    }

    return variables[varName]
  }

}
