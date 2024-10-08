package com.flofriday.constantEvalKotlinPlugin

/**
 * The environment holds all variables and their values of the current scope.
 *
 * It points to it's parent since all their variables should also be reachable.
 */
class Environment(
  private var enclosingEnvironment: Environment? = null,
  private val variables: MutableMap<String, Any?> = mutableMapOf(),
) {

  /**
   * A new variable declaration.
   *
   * This won't update any variable in any enclosing scope.
   */
  fun put(name: String, value: Any?) {
    variables[name] = value
  }

  /**
   * A new variable declaration.
   *
   * This won't update any variable in any enclosing scope.
   */
  fun update(name: String, value: Any?) {
    if (variables.containsKey(name)) {
      variables[name] = value
      return
    }

    if (enclosingEnvironment != null) {
      enclosingEnvironment!!.update(name, value)
      return
    }

    throw IllegalStateException("Environment variables do not exist: $name")
  }

  /**
   * Retrieve the value of a variable in the current or any enclosing scopes.
   */
  fun get(name: String): Any? {
    if (variables.containsKey(name)) {
      return variables[name]
    }

    if (enclosingEnvironment != null) {
      return enclosingEnvironment!!.get(name)
    }

    throw IllegalStateException("Environment variables do not exist: $name")
  }

  /**
   * Checks if the current or any enclosing scope holds a variable with that name.
   */
  fun has(name: String): Boolean {
    if (variables.containsKey(name)) {
      return true
    }

    if (enclosingEnvironment != null) {
      return enclosingEnvironment!!.has(name)
    }

    return false;
  }
}



