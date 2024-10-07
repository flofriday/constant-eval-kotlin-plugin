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

- Inside "eval" functions calls to other "eval" functions are allowed (including recursion)
- For recursion to work the arguments to an "eval" function, can no longer be restricted to just constants but any
  expression that the evaluator can calculate.
- Default arguments for "eval" functions can be correctly evaluated and are inserted if the corresponding value is
  missing.

<!-- String templates? -->
<!-- Null safety operators? -->
<!-- continue, break -->
<!-- do-while -->

## Build and run all tests

To build the plugin run:

```bash
./gradlew build
```

Since this is a compiler plugin that implements a performance optimization there aren't any fancy examples. However,
to verify that my implementation is correct I wrote some demanding tests which you can find in `ComplexTest.kt`
(like a test that renders a mandelbrot at compile-time).

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

## Implementation Details

For my test strategy I needed to dump the IR of the main function, however there isn't an easy way to access the IR with
the otherwise amazing [Kotlin Compile Testing](https://github.com/tschuchortdev/kotlin-compile-testing)
library. So I wrote another custom compiler plugin called `CaptureIrPlugin` that just dumps
the IR and registered it to run after the constant evaluation plugin ran. I am sure there must be a better way to
access it but this approach quite worked well.

The main compiler plugin has two major classes that do most of the work `Transformer` and `Evaluator`. `Transformer`
finds all calls to "eval" functions and replaces the call with the correct constant. To figure out which
value the constant should hold it passes the IR of the call to the evaluator which will evaluate the call.
