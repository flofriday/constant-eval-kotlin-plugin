package com.flofriday.constantEvalKotlinPlugin

// I wish I could have used reflection here but I couldn't get it to work without throwing an exception that the
// caller couldn't be resolved.
// We also handle with typechecking in a hand-wavey way here because we assume that the type-checker that ran before
// found all the violations.
val builtinFunctionTable = mapOf<String, (List<Any?>) -> Any?>(
  "kotlin.internal.ir::EQEQ(kotlin.Any?;kotlin.Any?){}" to { args -> args[0] == args[1] }, // Note: I'm only kinda sure this is correct.

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
  "kotlin.Int::toString(){}" to wrapFunction<Int>(Int::toString),
  "kotlin.Int::unaryMinus(){}" to wrapFunction<Int>(Int::unaryMinus),
  "kotlin.Int::unaryPlus(){}" to wrapFunction<Int>(Int::unaryPlus),
  "kotlin.Int::ushr(kotlin.Int){}" to wrapFunction<Int, Int>(Int::ushr),
  "kotlin.Int::xor(kotlin.Int){}" to wrapFunction<Int, Int>(Int::xor),
  "kotlin.Int::hashCode(){}" to wrapFunction<Int>(Int::hashCode),
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


fun <T1, T2, T3> wrapFunction(
  function: (T1, T2, T3) -> Any?
): (List<Any?>) -> Any? {
  return { args ->
    if (args.size == 3) {
      val arg1 = args[0] as T1
      val arg2 = args[1] as T2
      val arg3 = args[2] as T3
      function(arg1, arg2, arg3)
    } else {
      throw IllegalStateException("The number of arguments for a buitlin was not expected. Got ${args.size} expected 3")
    }
  }
}
