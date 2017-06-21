import java.lang.reflect.InvocationTargetException
import kotlin.reflect.full.declaredFunctions

abstract class Puzzle {

    open fun part1(): Any? = "UNIMPLEMENTED"
    open fun part2(): Any? = "UNIMPLEMENTED"

    infix fun Any.shouldBe(expected: Any) {
        if (this != expected) {
            throw AssertionError("$expected expected, but got $this")
        }
    }

    fun solve() {
        val RED = "31"
        val GRN = "32"
        val YEL = "33"
        val BLU = "34"
        val BLD = "1"
        fun String.colored(vararg c: String) = "\u001b[${c.joinToString(";")}m$this\u001b[0m"

        fun <T> profile(f: () -> T, res: (T, Double) -> Unit) {
            val time = System.currentTimeMillis()
            val result = f()
            val resultingTime = (System.currentTimeMillis() - time) / 1000.0
            res(result, resultingTime)
        }
        println("Puzzle ${this::class.simpleName}\n".colored(BLU))

        val success = this::class.declaredFunctions
                .filter { it.name.startsWith("test") }
                .fold(true) { prevSuccess, it ->
                    val title = "* ${it.name.replace(Regex("""[A-Z0-9]"""), { " ${it.value}" }).padEnd(50, '.')}"
                    try {
                        profile({ it.call(this) }) { _, time ->
                            println("$title SUCCESS in ${time}s".colored(GRN))
                        }
                        prevSuccess
                    } catch (e: InvocationTargetException) {
                        println("$title FAILED".colored(RED))
                        e.targetException.let {
                            when (it) {
                                is AssertionError -> println("> ${it.message}\n".colored(RED))
                                else -> it.printStackTrace(System.out)
                            }
                        }
                        false
                    }
                }

        if (success) {
            println()

            profile(this::part1) { res, time ->
                println("Part 1: ".colored(YEL) + res.toString().colored(YEL, BLD) +" in ${time}s".colored(YEL))
            }
            profile(this::part2) { res, time ->
                println("Part 2: ".colored(YEL) + res.toString().colored(YEL, BLD) +" in ${time}s".colored(YEL))
            }
        } else {
            println("\nTests failed".colored(RED))
        }
    }
}