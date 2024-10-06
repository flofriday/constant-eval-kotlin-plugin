package com.flofriday.constantEvalKotlinPlugin

import com.tschuchort.compiletesting.SourceFile
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertEquals

class ComplexTest {
  @Test
  fun `MandelBrot Renderer`() {
    // A simple mandelbrot renderer
    // Since we only have 32bit Integers implemented we will create our own fixed point data type.
    // Implementing doubles would have been easy enough but that was a fun side challenge. ^^

    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
        fun main() {
            println(evalMandelbrot())
        }
          
        fun evalMandelbrot(): String {
            val xStart = evalToFixed(-2)
            val xEnd = evalToFixed(1)
            val yStart = evalToFixed(1)
            val yEnd = evalToFixed(-1)
            val rows = 28
            val cols = 90

            val xStep = (xEnd - xStart) / cols
            val yStep = (yEnd - yStart) / rows

            var output = ""

            var r = 0
            while (r < rows) {
                var c = 0
                while (c < cols) {
                    val x = xStart + (c * xStep)
                    val y = yStart + (r * yStep)
                    val iter = evalPixelAt(x, y)
                    output += if (iter == evalMaxIteration()) " " else (iter % 9).toString()
                    c = c + 1
                }  
                output += "\n"
                r = r + 1
            }

            return "\n" + output
        }

        fun evalPixelAt(xi: Int, yi: Int): Int {
            var iteration = 0
            var x = evalToFixed(0)
            var y = evalToFixed(0)
            while (evalFixedMul(x, x) + evalFixedMul(y,y) <= evalToFixed(2*2) && iteration < evalMaxIteration()) {
                val xTemp = (evalFixedMul(x, x) - evalFixedMul(y,y)) + xi
                y = (2 * evalFixedMul(x, y)) + yi
                x = xTemp
                iteration = iteration + 1
            }
            return iteration
        }

        fun evalFixedMul(a: Int, b: Int) = (a * b) shr evalShiftFactor()
        fun evalToFixed(n: Int) = n shl evalShiftFactor()
        fun evalShiftFactor() = 14

        fun evalMaxIteration() = 32
       
        """
      )
    )

    // SideNote, the multiline string looks so weired because you cannot write three double qoutes in it and there is no
    // way to escape it, so instead here it is a template and as a value I insert three double quotes.
    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(${"\"\"\""}
          111111111222222253336054787486654333333354555544473556705032254447054333364222222222222233
          111111122222270133311553333333333333345864444448555567823728765844444083333445222222222222
          11111122222823336454644333333333333650674444455557666 43  72826551044467533333642222222222
          111112222633346477641633333333336255544444555816687840      307665555844443333333422222222
          111122233345614854563333333333744444444656277777781324      538876667666544333333444222222
          1111223565573534553333333336444444760557583486262 8         4687 0288518754433333618522222
          1112243354133333333333334444056756555766781                       88   6265543333474822222
          11228733333333333333444485566255550766281335                          61765145333325747222
          11244333333333344444578666666666666027841                              2876544633337056522
          124333333465854407557721188806888770834                                  22564403333352432
          123337486444440775570781803562 73221116                                 780614673333333512
          1334466644446365556178328           47                                   06554453333335543
          134444444455558688300541             4                                   72574446333356443
          1450555566666877802 536                                                2765564456333344523
          4547500 888 12322                                                   7301205104372333887643
          1457555666666877803 1 7                                                2765564455333364523
          16444444445555068830024              8                                   78584448333355443
          1334468544445765556878328           46                                   56554453333337343
          123336455444448315572781082562 71021126                                 680684663333333552
          124333333465454480557741188802288770824                                  62564473333375532
          11244333333333344444578666666666667277851                              2876544633334658722
          11226533333333333333444405566155558766081335                          01765045333330446222
          1112203334333333333333334444856655555766781                       88   6265543333464612222
          111122374016755463333333333044444426755008338525  8          427 1188418754433333626022222
          11112223334157418458333333333574444444465607777778132       588870668666544333333444222222
          111112222533346467145333333333335755544444555782687820      607665555344443333333422222222
          1111112222256333865406433333333333765778444445555161  81  72216556144406533333142222222222
          111111122222276733343553333333333333345474444441755517823428765744444733333447222222222222
          ${"\"\"\""})
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)

  }


  @Test
  fun `FizzBuzz`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalFizzBuzz(15))
          }
          
          fun evalFizzBuzz(n: Int): String {
            var i = 0
            var output = ""
            while (i < n) {
              i = i + 1
              output += when {
                (i % 3 == 0 && i % 5 == 0) -> "FizzBuzz"
                i % 3 == 0 -> "Fizz"
                i % 5 == 0 -> "Buzz"
                else -> i.toString()
              } + "\n"
            }
            return output
          }
        """
      )
    )

    // SideNote, the multiline string looks so weired because you cannot write three double qoutes in it and there is no
    // way to escape it, so instead here it is a template and as a value I insert three double quotes.
    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(${"\"\"\""}1
          2
          Fizz
          4
          Buzz
          Fizz
          7
          8
          Fizz
          Buzz
          11
          Fizz
          13
          14
          FizzBuzz
          ${"\"\"\""})
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)

  }

  @Test
  fun `Fibonacci sequence (recursively)`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalFib(1))
            println(evalFib(2))
            println(evalFib(3))
            println(evalFib(4))
            println(evalFib(5))
            println(evalFib(6))
            println(evalFib(7))
            println(evalFib(8))
            println(evalFib(9))
            println(evalFib(10))
          }
          
          fun evalFib(n: Int): Int {
            if (n <= 1) {
              return n
            }
            
            return evalFib(n - 1) + evalFib(n - 2)
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(1)
            println(1)
            println(2)
            println(3)
            println(5)
            println(8)
            println(13)
            println(21)
            println(34)
            println(55)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

  @Test
  fun `Factorial (recursively)`() {
    val result = compileWithEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(evalFactorial(1))
            println(evalFactorial(2))
            println(evalFactorial(3))
            println(evalFactorial(4))
            println(evalFactorial(5))
            println(evalFactorial(6))
            println(evalFactorial(7))
          }
          
          fun evalFactorial(n: Int): Int {
            return if(n == 0) 1 else n * evalFactorial(n - 1)
          }
        """
      )
    )

    val expectedResult = compileWithOutEval(
      sourceFile = SourceFile.kotlin(
        "main.kt",
        """
          fun main() {
            println(1)
            println(2)
            println(6)
            println(24)
            println(120)
            println(720)
            println(5040)
          }
        """.trimIndent()
      )
    )

    assertTrue(result.wasSuccessfull)
    assertTrue(expectedResult.wasSuccessfull)
    assertEquals(result.mainIrDump, expectedResult.mainIrDump)
  }

}
