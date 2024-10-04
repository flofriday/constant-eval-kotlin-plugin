package com.flofriday.constantEvalKotlinPlugin.CaptureIrPlugin

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitor

class CaptureIrPluginVisitor : IrElementVisitor<Unit, Nothing?> {

  private var dumpedIr = ""

  override fun visitFunction(declaration: IrFunction, data: Nothing?) {
    if (declaration.name.asString() == "main") {
      dumpedIr = declaration.dump()
    }
    super.visitFunction(declaration, data)
  }

  fun getIrDump(): String {
    return dumpedIr
  }

  override fun visitElement(element: IrElement, data: Nothing?) {
    element.acceptChildren(this, data)
  }
}

