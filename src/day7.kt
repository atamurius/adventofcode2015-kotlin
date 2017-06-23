import java.io.File

typealias Circuit = List<Pair<String, Day7.Instr>>

object Day7 : Puzzle() {

    /**
     * Full circuit.
     * @param instruction map wire name to instruction it gets signal from
     */
    data class Circuit(val instruction: Map<String, Instr>) {
        private val cachedValues = mutableMapOf<String, Int>()
        /**
         * Evaluates wire signal and caches it
         */
        operator fun get(wire: String) = cachedValues[wire] ?:
                (instruction[wire]?.evaluate(this) ?: error("$wire is undefined")).apply {
                    cachedValues.put(wire, this)
                }

        /**
         * Overrides wire signal, so it's not evaluated any more
         */
        fun override(wire: String, value: Int) {
            cachedValues.put(wire, value)
        }
    }

    /**
     * Signal source: just signal or wire
     */
    interface Instr {
        fun evaluate(instr: Circuit): Int
    }

    data class Signal(val value: Int) : Instr {
        override fun evaluate(instr: Circuit) = value
    }
    data class Wire(val name: String) : Instr {
        override fun evaluate(instr: Circuit) = instr[name]
    }

    data class BiGate(val left: Instr,
                      val right: Instr,
                      val op: (Int, Int) -> Int) : Instr {

        override fun evaluate(instr: Circuit): Int = op(left.evaluate(instr), right.evaluate(instr))
    }

    data class UnGate(val arg: Instr,
                      val op: (Int) -> Int) : Instr {

        override fun evaluate(instr: Circuit) = op(arg.evaluate(instr))
    }

    // --- End of types definition -------------------------------------------------------------------------------------

    val binaryOps = mapOf<String, (Int, Int) -> Int>(
            "AND"       to Int::and,
            "OR"        to Int::or,
            "LSHIFT"    to Int::shl,
            "RSHIFT"    to Int::shr)

    val unaryOps = mapOf<String, (Int) -> Int>("NOT" to { x -> x.inv() and 0xffff })

    // DSL
    infix fun Instr.connectedTo(wire: String): Pair<String, Instr> = wire to this
    infix fun Instr.and(right: Instr) = BiGate(this, right, Int::and)
    infix fun Instr.or(right: Instr) = BiGate(this, right, Int::or)
    infix fun Instr.lsh(right: Instr) = BiGate(this, right, Int::shl)
    infix fun Instr.rsh(right: Instr) = BiGate(this, right, Int::shr)
    operator fun Instr.not() = UnGate(this, unaryOps["NOT"]!!)

    /**
     * [Instr] type and DSL functions allow to create complex instructions:
     */
    val complexInstructions = ! (Wire("x") and Signal(5)) lsh Wire("y") connectedTo "z"


    fun parseSource(str: String) = if (Character.isDigit(str[0])) Signal(str.toInt()) else Wire(str)

    fun parse(str: String,
              unOps: Map<String, (Int) -> Int> = unaryOps,
              biOps: Map<String, (Int, Int) -> Int> = binaryOps): Pair<String, Instr> =

            Regex("""(\S+) -> (\w+)""").matchEntire(str)?.destructured?.let { (source, target) ->
                parseSource(source) connectedTo target
            } ?:
            Regex("""(\S+) (\w+) (\S+) -> (\w+)""").matchEntire(str)?.destructured?.let { (left, op, right, target) ->
                BiGate(parseSource(left), parseSource(right), biOps[op] ?: error("Operation $op is unknown")) connectedTo target
            } ?:
            Regex("""(\w+) (\S+) -> (\w+)""").matchEntire(str)?.destructured?.let { (op, arg, target) ->
                UnGate(parseSource(arg), unOps[op] ?: error("Undefined unary operation $op")) connectedTo target
            } ?: error("Unknown instruction $str")

    val example =
        """|123 -> x
           |456 -> y
           |x AND y -> d
           |x OR y -> e
           |x LSHIFT 2 -> f
           |y RSHIFT 2 -> g
           |NOT x -> h
           |NOT y -> i"""
            .trimMargin()
                .lines()
                .associate { parse(it) }

    fun testParse() = example shouldBe mapOf(
            Signal(123) connectedTo "x",
            Signal(456) connectedTo "y",
            Wire("x") and Wire("y") connectedTo "d",
            Wire("x") or Wire("y") connectedTo "e",
            Wire("x") lsh Signal(2) connectedTo "f",
            Wire("y") rsh Signal(2) connectedTo "g",
            ! Wire("x") connectedTo "h",
            ! Wire("y") connectedTo "i")

    fun testEvaluate() = example.mapValues { it.value.evaluate(Circuit(example)) } shouldBe mapOf(
            "d" to 72,
            "e" to 507,
            "f" to 492,
            "g" to 114,
            "h" to 65412,
            "i" to 65079,
            "x" to 123,
            "y" to 456)

    val input = File("src/day7.txt").readLines().map { parse(it) }.toMap()

    override fun part1() = Circuit(input)["a"]
    override fun part2() = Circuit(input).apply { override("b", 3176) }["a"]

    @JvmStatic
    fun main(vararg args: String) = solve()
}
