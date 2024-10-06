package com.flofriday.constantEvalKotlinPlugin

/**
 * A table that holds all builtin functions we support.
 *
 * This allows the functions to be called by any value and will provide tha appopate casts themself.
 * This might be somewhat unsafe, but we trust the typechecker that ran before that it did it job correctly.
 *
 *  I wish I could have used reflection here, but I couldn't get it to work without throwing an exception that the
 *  caller couldn't be resolved.
 *  From some research this is because apparently the function on the builtin classes don't exist in the compiled
 *  Java code and therefore reflection cannot find them.
 *  Source: https://stackoverflow.com/a/78597425
 */
val builtinFunctionTable = mapOf<String, (List<Any?>) -> Any?>(
  // Some internal methods
  "kotlin.internal.ir::EQEQ(kotlin.Any?;kotlin.Any?){}" to { args -> args[0] == args[1] }, // Note: I'm only kinda sure this is correct.
  "kotlin.internal.ir::less(kotlin.Int;kotlin.Int){}" to { args -> (args[0] as Int) < args[1] as Int },
  "kotlin.internal.ir::lessOrEqual(kotlin.Int;kotlin.Int){}" to { args -> args[0] as Int <= args[1] as Int },
  "kotlin.internal.ir::greater(kotlin.Int;kotlin.Int){}" to { args -> (args[0] as Int) > args[1] as Int },
  "kotlin.internal.ir::greaterOrEqual(kotlin.Int;kotlin.Int){}" to { args -> (args[0] as Int) >= args[1] as Int },

  // Boolean builtin methods
  "kotlin.Boolean::and(kotlin.Boolean){}" to wrapFunction(Boolean::and),
  "kotlin.Boolean::compareTo(kotlin.Boolean){}" to wrapFunction(Boolean::compareTo),
  "kotlin.Boolean::equals(kotlin.Any?){}" to wrapFunction<Any?>(Boolean::equals),
  "kotlin.Boolean::hashCode(){}" to wrapFunction(Boolean::hashCode),
  "kotlin.Boolean::not(){}" to wrapFunction(Boolean::not),
  "kotlin.Boolean::or(kotlin.Boolean){}" to wrapFunction(Boolean::or),
  "kotlin.Boolean::toString(){}" to wrapFunction(Boolean::toString),
  "kotlin.Boolean::xor(kotlin.Boolean){}" to wrapFunction(Boolean::xor),

  // Int builtin methods
  "kotlin.Int::and(kotlin.Int){}" to wrapFunction(Int::and),
  "kotlin.Int::compareTo(kotlin.Int){}" to wrapFunction<Int, Int>(Int::compareTo),
  "kotlin.Int::dec(){}" to wrapFunction(Int::dec),
  "kotlin.Int::div(kotlin.Int){}" to wrapFunction<Int, Int>(Int::div),
  "kotlin.Int::equals(kotlin.Any){}" to wrapFunction<Int, Any>(Int::equals),
  "kotlin.Int::inc(){}" to wrapFunction<Int>(Int::inc),
  "kotlin.Int::inv(){}" to wrapFunction<Int>(Int::inv),
  "kotlin.Int::minus(kotlin.Int){}" to wrapFunction<Int, Int>(Int::minus),
  "kotlin.Int::or(kotlin.Int){}" to wrapFunction<Int, Int>(Int::or),
  "kotlin.Int::plus(kotlin.Int){}" to wrapFunction<Int, Int>(Int::plus),
  "kotlin.Int::rem(kotlin.Int){}" to wrapFunction<Int, Int>(Int::rem),
  "kotlin.Int::shl(kotlin.Int){}" to wrapFunction<Int, Int>(Int::shl),
  "kotlin.Int::shr(kotlin.Int){}" to wrapFunction<Int, Int>(Int::shr),
  "kotlin.Int::times(kotlin.Int){}" to wrapFunction<Int, Int>(Int::times),
  "kotlin.Int::toInt(){}" to wrapFunction<Int>(Int::toInt),
  "kotlin.Int::toString(){}" to wrapFunction<Int>(Int::toString),
  "kotlin.Int::unaryMinus(){}" to wrapFunction<Int>(Int::unaryMinus),
  "kotlin.Int::unaryPlus(){}" to wrapFunction<Int>(Int::unaryPlus),
  "kotlin.Int::ushr(kotlin.Int){}" to wrapFunction<Int, Int>(Int::ushr),
  "kotlin.Int::xor(kotlin.Int){}" to wrapFunction<Int, Int>(Int::xor),
  "kotlin.Int::hashCode(){}" to wrapFunction<Int>(Int::hashCode),

  // String builtin methods
  // There are some deprecated functions, however I cannot even include them because they are not in my kotlin
  // installation.
  "kotlin.String::compareTo(kotlin.String){}" to wrapFunction<String, String>(String::compareTo),
  "kotlin.String::equals(kotlin.Any?){}" to wrapFunction<String, String>(String::equals),
  //"kotlin.String::indent(kotlin.Int){}" to wrapFunction<String, Int>(String::indent),
  "kotlin.String::plus(kotlin.Any?){}" to wrapFunction<String, Any?>(String::plus),
  //"kotlin.String::strip(){}" to wrapFunction<String>(String::strip),
  //"kotlin.String::stripIndent(){}" to wrapFunction<String>(String::stripIndent),
  //"kotlin.String::stripLeading(){}" to wrapFunction<String>(String::stripLeading),
  //"kotlin.String::stripTrailing(){}" to wrapFunction<String>(String::stripTrailing),
  "kotlin.String::toString(){}" to wrapFunction<String>(String::toString),
  //"kotlin.String::translateEscapes(){}" to wrapFunction<String>(String::translateEscapes),
  "kotlin.String::hashCode(){}" to wrapFunction<String>(String::hashCode),
  "kotlin.String::isEmpty(){}" to wrapFunction<String>(String::isEmpty),
)

fun <T1> wrapFunction(
  function: (T1) -> Any?
): (List<Any?>) -> Any? {
  return { args ->
    if (args.size == 1) {
      val arg1 = args[0] as T1
      function(arg1)
    } else {
      throw IllegalStateException("The number of arguments for a buitlin was not expected. Got ${args.size} expected 1")
    }
  }
}

fun <T1, T2> wrapFunction(
  function: (T1, T2) -> Any?
): (List<Any?>) -> Any? {
  return { args ->
    if (args.size == 2) {
      val arg1 = args[0] as T1
      val arg2 = args[1] as T2
      function(arg1, arg2)
    } else {
      throw IllegalStateException("The number of arguments for a buitlin was not expected. Got ${args.size} expected 2")
    }
  }
}
