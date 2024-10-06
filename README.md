# Constant evaluation Kotlin compiler plugin

A compiler plugin that evaluates some constants.

## Description

All functions with the `eval` prefix are evaluated (if possible).

```Kotlin
fun main() {
    // evalAdd(1, 2) must be evaluated as 3
    println(evalAdd(1, 2))
}

fun evalAdd(a: Int, b: Int): Int {
    return a + b
}
```

Inside an eval function the following statements and expressions are allowed:

- Operations on primitives. Basically methods declared inside classes Int, Boolean and String. It ignores Byte, Short,
  Long, Float and Double to simplify things. To avoid repeating, let’s call these types constant types.
- If/when expressions.
- While loops.
- Create val/var variables and set values for them.
- The "eval" function must return a result of a constant type.
- The "eval" function can accept only arguments of a constant type.
- If the "eval" function cannot be computed at runtime, it is left as is.

### Bonus features

- Inside "eval" functions calls to other eval functions including itself (recursion) are allowed
- For recursion to work the arguments to an "eval" function, can no longer be restricted to just constants but any
  expression that the evaluator can calculate.

## Build and run all tests

To build the plugin run:

```bash
./gradlew build
```

## Testing Strategy

Testing constant evaluation can be tricky, because if it is correctly implemented the optimized program with all
constants evaluated should be semantically equal.
This means that if we run the optimized program and an unoptimized version we won't see any difference. While we can use
that to see if there are wrong evaluations we cannot tell if the optimized program was really optimized.
(Well technically it should be faster, because that's the point of the optimization but measuring timings can also be
tricky).

Instead, I decided that every testcase contains two programs, once with some constants to evaluate and once where I
evaluated them by hand. The first program will be compiled with my plugin enabled while the second uses the default
compiler. After the plugin ran both programs should be equally so I dump the IR of both programs and compare if the
dump is equal.

![Testing Pipeline](testing-pipeline.png)

With this strategy we not only know that the constant evaluation ran but also that it produced exactly what we expected.
We therefore don't even need to execute the compiled programs. The IR dump is even readable by humans and even though
it can get large in some cases it worked great to discover issues.
