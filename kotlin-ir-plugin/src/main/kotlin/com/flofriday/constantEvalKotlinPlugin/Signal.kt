package com.flofriday.constantEvalKotlinPlugin

/**
 * Signals used during the evaluation of constant functions.
 *
 * Of course, they are exceptions because I find it much easier to unwind the call stack by throwing an exception than
 * manual conditions.
 * However, the name exception is often associated with errors and not all of those are used for errors.
 */

/**
 * This signal is used if for some reason we cannot evaluate the function and instead we will leave the original one.
 */
class StopEvalSignal(
  override val message: String? = null
) : RuntimeException() {}

/**
 * This signal is used to unwind the call stack once we found a solution.
 */
class ReturnSignal(
  val value: Any?
) : RuntimeException() {}
